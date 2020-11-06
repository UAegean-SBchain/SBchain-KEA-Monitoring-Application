/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.CredsAndExp;
import com.example.ethereumserviceapp.model.State;
import com.example.ethereumserviceapp.model.entities.SsiApplication;
import com.example.ethereumserviceapp.model.entities.SsiApplicationTest;
import com.example.ethereumserviceapp.service.EthereumService;
import com.example.ethereumserviceapp.service.MongoService;
import com.example.ethereumserviceapp.service.MonitorService;
import com.example.ethereumserviceapp.utils.EthAppUtils;
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
            // check if credentials are valid and not expired
            if(!credentialsOk(uuid)){
                return;
            }
            // Arrays.stream(this.mongoServ.findCredentialIdsByUuid(uuid)).forEach(credIdAndExp -> {
            //     log.info("checking credential {} from case {}", credIdAndExp.getId(), uuid);
            //     //check if the credential has not expired
            //     Date expiresAt = Date.from(Instant.ofEpochSecond(Long.parseLong(credIdAndExp.getExp())));
            //     log.info("credential expires at {}", expiresAt.toString());
            //     if (!expiresAt.after(new Date(System.currentTimeMillis()))) {
            //         //if credentials have expired update case as rejected
            //         updateCase(uuid, State.REJECTED, null);
            //         return;
            //     }
            //     //check if the credential is revoked
            //     boolean isRevoked = this.ethServ.checkRevocationStatus(credIdAndExp.getId());
            //     log.info("is credential {} revoked? == {}", credIdAndExp.getId(), isRevoked);
            //     if (isRevoked){
            //         updateCase(uuid, State.REJECTED, null);
            //         return;
            //     }
            //     Boolean credentialsOk = true;
            // });

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
            if (MonitorUtils.isCaseOlderThanSixMonths(firstAcceptedDate) || !MonitorUtils.checkExternalSources()) {
                //update the status of the case to REJECTED and the date with the current date
                updateCase(uuid, State.REJECTED, ssiCase.isPresent()? ssiCase.get() : null);
                //this.mongoServ.deleteByUuid(uuid);
            } else {
                if (!ssiCase.isPresent()) {
                    updateCase(uuid, State.REJECTED, null);
                    return;
                }
                final SsiApplication ssiApp = ssiCase.get();
                //check the application by the uuid and update the case accordingly
                if (checkCredentials(c.get(), ssiApp)) {
                    //TODO replace mock check has green card with valid check
                    if(!MonitorUtils.hasGreenCard(uuid)){
                        updateCase(uuid, State.PAUSED, ssiApp);
                    } else {
                        updateCase(uuid, State.ACCEPTED, ssiApp);
                    }
                } else {
                    updateCase(uuid, State.REJECTED, ssiApp);
                }
            }
        });
    }

    private void updateCase(String uuid, State state, SsiApplication ssiApp) {
        Optional<Case> theCase = this.ethServ.getCaseByUUID(uuid);
        if (theCase.isPresent()) {
            theCase.get().setState(state);
            theCase.get().setDate(LocalDateTime.now());
            if(ssiApp != null){
                //TODO use new application with history when ready
                SsiApplicationTest ssiAppTest = new SsiApplicationTest();
                //MonitorUtils.updateOffset(LocalDate.of(2020, 8, 12), theCase.get(), ssiApp);
                MonitorUtils.calculateOffset(theCase.get(), ssiAppTest);
            }
            this.ethServ.updateCase(theCase.get());
            log.info("updated case uuid :{}, date :{}, state :{}, offset:{} ", theCase.get().getUuid(), theCase.get().getDate(), theCase.get().getState(), theCase.get().getOffset());
        } else {
            log.error("cannot find case {} while trying to update it", uuid);
        }
    }

    private Boolean credentialsOk(String uuid){
        Boolean credsOk = true;
        CredsAndExp[] credIdAndExp = this.mongoServ.findCredentialIdsByUuid(uuid);
        if(credIdAndExp == null){
            return true;
        }
        for(int i = 0; i < credIdAndExp.length; i++){
            log.info("checking credential {} from case {}", credIdAndExp[i].getId(), uuid);
            //check if the credential has not expired
            LocalDateTime expiresAt = LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(credIdAndExp[i].getExp())), ZoneId.systemDefault());
            //Date expiresAt = Date.from(Instant.ofEpochSecond(Long.parseLong(credIdAndExp[i].getExp())));
            log.info("credential expires at {}", expiresAt);
            if (!expiresAt.isAfter(LocalDateTime.now())) {
                //if credentials have expired update case as rejected
                updateCase(uuid, State.REJECTED, null);
                credsOk = false;
                break;
            }
            //check if the credential is revoked
            boolean isRevoked = this.ethServ.checkRevocationStatus(credIdAndExp[i].getId());
            log.info("is credential {} revoked? == {}", credIdAndExp[i].getId(), isRevoked);
            if (isRevoked){
                updateCase(uuid, State.REJECTED, null);
                credsOk = false;
                break;
            }
            credsOk = true;
        }

        return credsOk;
    }

    private Boolean checkCredentials(Case caseToBePaid, SsiApplication ssiApp){
        //mock household check
        Map<String, String>[] houseHold = ssiApp.getHouseholdComposition();
        if(houseHold != null){
            for(int i = 0; i < houseHold.length; i++){
                if(houseHold[i].entrySet().stream().anyMatch(h -> h.getValue().equals("deceased"))){
                    return false;
                }
            }
        }
        //external oaed check
        if(!oaedRegistrationCheck(ssiApp.getOaedId())){
            return false;
        }
        //external housing subsidy check
        if(!houseBenefitCheck(ssiApp.getTaxisAfm())){
            return false;
        }
        // check if meter number appears on other applications
        if(mongoServ.findByMeterNumber(ssiApp.getMeterNumber()).size() > 1){
            return false;
        }
        // check for luxury living
        if(ssiApp.getLuxury() == null? false : ssiApp.getLuxury().equals(String.valueOf(Boolean.TRUE))){
            return false;
        }
        //check OAED benefits
        if(BigInteger.valueOf(Long.valueOf(ssiApp.getUnemploymentBenefitR() == null? "0" : ssiApp.getUnemploymentBenefitR())).compareTo(BigInteger.valueOf(300)) > 0 ){
            return false;
        }
        //economics check
        if(EthAppUtils.getTotalMonthlyValue(ssiApp).compareTo(BigDecimal.valueOf(0)) == 0){
            return false;
        }
        //check Ergome benefits
        if(BigInteger.valueOf(Long.valueOf(ssiApp.getErgomeR() == null? "0" : ssiApp.getErgomeR())).compareTo(BigInteger.valueOf(300)) > 0 ){
            return false;
        }
        //check for failed payments
        if(caseToBePaid.getPaymentHistory() == null? false : caseToBePaid.getPaymentHistory().stream().filter(s -> s.getState().equals(State.FAILED)).count() >= 3){
            return false;
        }
        //check if two months have passed while the application is in status paused
        Iterator<Entry<LocalDateTime, State>> it = caseToBePaid.getHistory().entrySet().iterator();
        LocalDate pausedStartDate = LocalDate.of(1900, 1, 1);
        LocalDate pausedEndDate = LocalDate.of(1900, 1, 1);
        while(it.hasNext()){
            if(pausedEndDate.equals(pausedStartDate.plusMonths(2))){
                return false;
            }
            Map.Entry<LocalDateTime, State> entry = it.next();
            if(!entry.getValue().equals(State.PAUSED)){
                pausedStartDate = LocalDate.of(1900, 1, 1);
                continue;
            }
            if(pausedStartDate.equals(LocalDate.of(1900, 1, 1))){
                pausedStartDate = entry.getKey().toLocalDate();
            }
            pausedEndDate = entry.getKey().toLocalDate();
        }
        // check that if there differences in Amka register
        if(differenceInAmka(ssiApp.getTaxisAmka())){
            return false;
        }

        //check for duplicates in households
        Map<String, String>[] householdArray = ssiApp.getHouseholdComposition();
        if(householdArray != null){
            for(int i=0; i<householdArray.length; i++){
                Map<String, String> household = householdArray[i];
                List<SsiApplication> hSsiApp = mongoServ.findByHouseholdCompositionIn(household);
                if(hSsiApp.size()>1){
                    return false;
                }
            }
        }
        //check if iban exists in other application
        if(mongoServ.findByIban(ssiApp.getIban()).size() > 1){
            return false;
        }
        log.info("return check true ?");
        return true;
    }

    //mock oaed registration check
    private Boolean oaedRegistrationCheck(String oaedId){
        return true;
    }
    //mock housing subsidy check
    private Boolean houseBenefitCheck(String afm){
        return true;
    }

    //mockAmkaCheck
    private Boolean differenceInAmka(String amka){
        return false;
    }

}
