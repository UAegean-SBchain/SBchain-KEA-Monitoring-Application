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
                    while(it.hasNext() && !accepted){
                        Entry<LocalDateTime, State> entry = it.next();
                        accepted = entry.getValue().equals(State.ACCEPTED)? true : false;
                        if(accepted){
                            firstAcceptedDate = entry.getKey();
                        }
                    }
                    if (isRevoked || MonitorUtils.isCaseOlderThanSixMonths(firstAcceptedDate) || !MonitorUtils.checkExternalSources()) {
                        //update the status of the case to REJECTED and the date with the current date
                        updateCase(uuid, State.REJECTED, null);
                        this.mongoServ.deleteByUuid(uuid);
                    } else {
                        Optional<SsiApplication> ssiCase = mongoServ.findByUuid(uuid);
                        if (ssiCase.isPresent()) {
                            final SsiApplication ssiApp = ssiCase.get();
                            //check the application by the uuid and update the case accordingly
                            if (MonitorUtils.isApplicationAccepted(ssiApp)) {
                                
                                updateCase(uuid, State.ACCEPTED, ssiApp);
                            } else {
                                updateCase(uuid, State.REJECTED, null);
                            }
                        } else {
                            updateCase(uuid, State.REJECTED, null);
                        }
                    }
                } else {
                    //if credentials have expired update case as rejected
                    updateCase(uuid, State.REJECTED, null);
                    this.mongoServ.deleteByUuid(uuid);
                };

            });
        });
    }

    private void updateCase(String uuid, State state, SsiApplication ssiApp) {
        Optional<Case> theCase = this.ethServ.getCaseByUUID(uuid);
        if (theCase.isPresent()) {
            theCase.get().setState(state);
            theCase.get().setDate(LocalDateTime.now());
            if(state.getValue() == 1 && ssiApp != null ){
                updateOffset(ssiApp, theCase.get());
            } else {
                this.ethServ.updateCase(theCase.get());
            }
            
        } else {
            log.error("cannot find case {} while trying to update it", uuid);
        }
    }

    // mock projection
    private void updateOffset(SsiApplication ssiApp, Case monitoredCase){

        //Mock credential date, this illustrates a date at which a credential has been modified prior to being updated in the system
        LocalDate date = LocalDate.of(2020, 8, 12);
        Boolean changed = false;

        for(CasePayment payment:monitoredCase.getPaymentHistory()){

            // update iff the month of the date is equal or after the date of the modified credential and the offset of this payment hasn't been paid
            if(payment.getPaymentDate().getMonthValue() < date.getMonthValue()){
                continue;
            }

            // this should probably call the payment calculation method and return the value that should have been paid for all paid days with the new credentials
            BigInteger fullMonthProjection = BigInteger.valueOf(0);

            Long paidDays = monitoredCase.getHistory().entrySet().stream().filter(
                e -> e.getKey().getMonthValue() == date.getMonthValue() && e.getKey().getYear() == date.getYear() && e.getValue().equals(State.ACCEPTED)).count();

            Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                e -> e.getKey().getMonthValue() == date.getMonthValue() && e.getKey().getYear() == date.getYear() && e.getKey().getDayOfMonth()>=date.getDayOfMonth() && e.getValue().equals(State.ACCEPTED)).count();

                
            BigInteger paymentPerDayActual = payment.getPayment().divide(BigInteger.valueOf(paidDays));

            BigInteger projection = fullMonthProjection;
            if(paidDays != offsetDays){
                projection = (BigInteger.valueOf(paidDays - offsetDays ).multiply(paymentPerDayActual))
                .add(BigInteger.valueOf(offsetDays).multiply(fullMonthProjection.divide(BigInteger.valueOf(paidDays))));
            }

            BigInteger actualPayment = payment.getPayment();
            BigInteger offset = projection.subtract(actualPayment);

            // if the new offset is different than the old offset update
            if(offset.compareTo(payment.getOffset()) != 0){
                if(payment.getIsOffsetPaid()){
                    offset = offset.subtract(payment.getOffset());
                    payment.setIsOffsetPaid(false);
                }
                payment.setOffset(offset);
                changed = true;
                this.ethServ.updateExistingPayment(monitoredCase.getUuid(), payment);
            }
        }

        if(!changed){
            this.ethServ.updateCase(monitoredCase);
        }
        

        // if(ssiApp.getHospitalized().equals("true")){
        //     projection = BigInteger.valueOf(22 * 100);
        // } else {
        //     projection = BigInteger.valueOf(22 * 120);
        // }

        // return monitoredCase;
    }

}
