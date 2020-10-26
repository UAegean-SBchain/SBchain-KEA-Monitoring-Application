/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp.service.impl;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.CasePayment;
import com.example.ethereumserviceapp.model.State;
import com.example.ethereumserviceapp.model.entities.SsiApplication;
import com.example.ethereumserviceapp.service.EthereumService;
import com.example.ethereumserviceapp.service.MongoService;
import com.example.ethereumserviceapp.service.MonitorService;
import com.example.ethereumserviceapp.utils.MonitorUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author nikos
 */
@Service
@Slf4j
public class MonitorServiceImpl implements MonitorService {

    private MongoService mongoServ;

    private EthereumService ethServ;

    @Autowired
    public MonitorServiceImpl(MongoService mongoS, EthereumService ethServ) {
        this.mongoServ = mongoS;
        this.ethServ = ethServ;
    }

    MonitorUtils monitorUtils;

    @Override
    @Scheduled(cron = "0 0 12 * * ?")
    public void startMonitoring() {
        List<String> uuids = this.ethServ.getAllCaseUUID();
        uuids.stream().forEach(uuid -> {

            //check if the case state is rejected, if so, skip the test
            Optional<Case> c = this.ethServ.getCaseByUUID(uuid);
            int caseState = c.get().getState().getValue();
            Iterator<Entry<LocalDateTime, State>> it = c.get().getHistory().entrySet().iterator();
            
            if (caseState == 2) {
                Iterator<Entry<LocalDateTime, State>> itr = c.get().getHistory().entrySet().iterator();
                while(itr.hasNext()){
                    Map.Entry<LocalDateTime, State> entry = itr.next();
                    //if the case is rejected for more than one month then delete it
                    if(entry.getValue().equals(State.REJECTED) && entry.getKey().toLocalDate().isBefore(LocalDate.now().minusMonths(1))){
                        this.mongoServ.deleteByUuid(uuid);
                    }
                }
                return;
            }

            log.info("looking into case {} with state {}", uuid, caseState);
            Arrays.stream(this.mongoServ.findCredentialIdsByUuid(uuid)).forEach(credIdAndExp -> {
                log.info("checking credential {} from case {}", credIdAndExp.getId(), uuid);
                //check if the credential has not expired
                Date expiresAt = Date.from(Instant.ofEpochSecond(Long.parseLong(credIdAndExp.getExp())));
                log.info("credential expires at {}", expiresAt.toString());
                if (expiresAt.after(new Date(System.currentTimeMillis()))) {
                    //check if the credential is revoked
                    boolean isRevoked = this.ethServ.checkRevocationStatus(credIdAndExp.getId());
                    log.info("is credential {} revoked? == {}", credIdAndExp.getId(), isRevoked);
                    LocalDateTime firstAcceptedDate = LocalDateTime.of(2020, 1, 1, 00, 00, 00);
                    Boolean accepted = false;
                    //find the first day the case was accepted
                    while(it.hasNext() && !accepted){
                        Entry<LocalDateTime, State> entry = it.next();
                        accepted = entry.getValue().equals(State.ACCEPTED)? true : false;
                        if(accepted){
                            firstAcceptedDate = entry.getKey();
                        }
                    }
                    Optional<SsiApplication> ssiCase = mongoServ.findByUuid(uuid);
                    if (isRevoked || MonitorUtils.isCaseOlderThanSixMonths(firstAcceptedDate) || !MonitorUtils.checkExternalSources()) {
                        //update the status of the case to REJECTED and the date with the current date
                        updateCase(uuid, State.REJECTED, ssiCase.isPresent()? ssiCase.get() : null);
                        //this.mongoServ.deleteByUuid(uuid);
                    } else {
                        
                        if (ssiCase.isPresent()) {
                            final SsiApplication ssiApp = ssiCase.get();
                            //check the application by the uuid and update the case accordingly
                            if (MonitorUtils.isApplicationAccepted(ssiApp)) {
                                //TODO replace mock check has green card with valid check
                                if(!MonitorUtils.hasGreenCard(uuid)){
                                    updateCase(uuid, State.PAUSED, ssiApp);
                                } else {
                                    updateCase(uuid, State.ACCEPTED, ssiApp);
                                }
                            } else {
                                updateCase(uuid, State.REJECTED, ssiApp);
                            }
                        } else {
                            updateCase(uuid, State.REJECTED, null);
                        }
                    }
                } else {
                    //if credentials have expired update case as rejected
                    updateCase(uuid, State.REJECTED, null);
                    //this.mongoServ.deleteByUuid(uuid);
                };
            });
        });
    }

    private void updateCase(String uuid, State state, SsiApplication ssiApp) {
        Optional<Case> theCase = this.ethServ.getCaseByUUID(uuid);
        if (theCase.isPresent()) {
            theCase.get().setState(state);
            theCase.get().setDate(LocalDateTime.now());
            if(ssiApp != null){
                MonitorUtils.updateOffset(theCase.get(), ssiApp);
            }
            this.ethServ.updateCase(theCase.get());
            
        } else {
            log.error("cannot find case {} while trying to update it", uuid);
        }
    }

}
