/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp.service.impl;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.State;
import com.example.ethereumserviceapp.service.EthereumService;
import com.example.ethereumserviceapp.service.MongoService;
import com.example.ethereumserviceapp.service.MonitorService;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public void startMonitoring() {
        List<String> uuids = this.ethServ.getAllCaseUUID();
        uuids.stream().forEach(uuid -> {

            //check if the case state is rejected, if so, skip the test
            int caseState = this.ethServ.getCaseByUUID(uuid).get().getState().getValue();
            if (caseState != 2) {
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
                        if (isRevoked) {
                            //update the status of the case to REJECTED
                            Optional<Case> theCase = this.ethServ.getCaseByUUID(uuid);
                            if (theCase.isPresent()) {
                                theCase.get().setState(State.REJECTED);
                                this.ethServ.updateCase(theCase.get());
                            } else {
                                log.error("cannot find case {} while trying to update it", uuid);
                            }
                        }
                    };

                });
            }
        });
    }

}
