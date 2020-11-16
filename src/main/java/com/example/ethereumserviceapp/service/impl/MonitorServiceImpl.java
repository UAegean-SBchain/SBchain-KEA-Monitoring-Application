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
import java.util.Set;
import java.util.stream.Collectors;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.CredsAndExp;
import com.example.ethereumserviceapp.model.HouseholdMember;
import com.example.ethereumserviceapp.model.State;
import com.example.ethereumserviceapp.model.entities.SsiApplication;
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
            //int caseState = c.get().getState().getValue();
            Iterator<Entry<LocalDateTime, State>> it = c.get().getHistory().entrySet().iterator();
            
            if(c.get().getState().equals(State.NONPRINCIPAL)){
                return;
            }
            if(c.get().getState().equals(State.REJECTED)){
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
            log.info("looking into case {} with state {}", uuid, c.get().getState());
            Optional<SsiApplication> ssiCase = mongoServ.findByUuid(uuid);
            if (!ssiCase.isPresent()) {
                updateCase(uuid, State.REJECTED, null);
                return;
            }
            // if this is not a principal case update state as non principal and continue to the next case
            if(!ssiCase.get().getTaxisAfm().equals(ssiCase.get().getHouseholdPrincipal().getAfm())){
                updateCase(uuid, State.NONPRINCIPAL, null);
                return;
            }

            Set<String> householdAfms = ssiCase.get().getHouseholdComposition().stream().map(s -> s.getAfm()).collect(Collectors.toSet());
            List<SsiApplication> householdApps = mongoServ.findByTaxisAfmIn(householdAfms);
            
            // check if credentials are valid and not expired
            if(!credentialsOk(uuid, householdApps)){
                return;
            }

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
            
            if (MonitorUtils.isCaseOlderThanSixMonths(firstAcceptedDate) || !MonitorUtils.checkExternalSources()) {
                //update the status of the case to REJECTED and the date with the current date
                updateCase(uuid, State.REJECTED, ssiCase.isPresent()? ssiCase.get() : null);
                //this.mongoServ.deleteByUuid(uuid);
            } else {
                final SsiApplication ssiApp = ssiCase.get();
                //check the application by the uuid and update the case accordingly
                if (checkHouseholdCredentials(c.get(), ssiApp, householdApps)) {
                    //TODO replace mock check has green card with valid check
                    if(!MonitorUtils.hasGreenCard(uuid)){
                        rejectOrSuspendCases(uuid, State.SUSPENDED, householdApps);
                    } else {
                        updateCase(uuid, State.ACCEPTED, ssiApp);
                    }
                } else {
                    rejectOrSuspendCases(uuid, State.REJECTED, householdApps);
                }
            }
        });
    }

    private void rejectOrSuspendCases(String uuid, State state, List<SsiApplication> householdApps){
        for(SsiApplication hhSsiApp:householdApps){
            updateCase(hhSsiApp.getUuid(), state, hhSsiApp);
        }
    }

    private void updateCase(String uuid, State state, SsiApplication ssiApp) {
        
        Optional<Case> theCase = this.ethServ.getCaseByUUID(uuid);
        if (theCase.isPresent()) {
            theCase.get().setState(state);
            theCase.get().setDate(LocalDateTime.now());
            if(ssiApp != null){
                List<SsiApplication> allHouseholdApps = mongoServ.findByTaxisAfmIn(EthAppUtils.fetchAllHouseholdAfms(ssiApp)); 
                MonitorUtils.calculateOffset(theCase.get(), ssiApp, allHouseholdApps);
            }
            this.ethServ.updateCase(theCase.get());
            log.info("updated case uuid :{}, date :{}, state :{}, offset:{} ", theCase.get().getUuid(), theCase.get().getDate(), theCase.get().getState(), theCase.get().getOffset());
        } else {
            log.error("cannot find case {} while trying to update it", uuid);
        }
    }

    private Boolean credentialsOk(String uuid, List<SsiApplication> householdApps){
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
                //if credentials have expired update case as suspended(paused)
                rejectOrSuspendCases(uuid, State.SUSPENDED, householdApps);
                credsOk = false;
                break;
            }
            //check if the credential is revoked
            boolean isRevoked = this.ethServ.checkRevocationStatus(credIdAndExp[i].getId());
            log.info("is credential {} revoked? == {}", credIdAndExp[i].getId(), isRevoked);
            if (isRevoked){
                rejectOrSuspendCases(uuid, State.REJECTED, householdApps);
                credsOk = false;
                break;
            }
            credsOk = true;
        }

        return credsOk;
    }

    private Boolean checkHouseholdCredentials(Case monitoredCase, SsiApplication ssiApp, List<SsiApplication> householdApps){
        final LocalDate currentDate = LocalDate.now();
        final LocalDate endDate = LocalDate.of(currentDate.getYear(), currentDate.getMonthValue(), EthAppUtils.monthDays(currentDate));

        List<HouseholdMember> household = ssiApp.getHouseholdComposition();

        //check if by the end of the month all the members of the household have submitted an application
        if(currentDate.equals(endDate)){
            List<String> appAfms = householdApps.stream().map(a -> a.getTaxisAfm()).collect(Collectors.toList());
            List<String> householdAfms = household.stream().map(m -> m.getAfm()).collect(Collectors.toList());

            if(!householdAfms.containsAll(appAfms)){
                return false;
            }
        }

        //check for deceased members in the household
        if(household.stream().anyMatch(h -> checkForDeceasedMembers(h))){
            return false;
        }

        //check for failed payments
        if(monitoredCase.getPaymentHistory() == null? false : monitoredCase.getPaymentHistory().stream().filter(s -> s.getState().equals(State.FAILED)).count() >= 3){
            return false;
        }

        //check if there are more than one principal members
        if(mongoServ.findByHouseholdPrincipalIn(household).size()>1){
            return false;
        }

        for(HouseholdMember member:household){
            List<SsiApplication> householdDuplicates = mongoServ.findByHouseholdComposition(member);
            if(householdDuplicates.size()>1){
                return false;
            }
        }

        //check if two months have passed while the application is in status suspended
        Iterator<Entry<LocalDateTime, State>> it = monitoredCase.getHistory().entrySet().iterator();
        LocalDate suspendStartDate = LocalDate.of(1900, 1, 1);
        LocalDate suspendEndDate = LocalDate.of(1900, 1, 1);
        while(it.hasNext()){
            if(suspendEndDate.equals(suspendStartDate.plusMonths(2))){
                return false;
            }
            Map.Entry<LocalDateTime, State> entry = it.next();
            if(!entry.getValue().equals(State.SUSPENDED)){
                suspendStartDate = LocalDate.of(1900, 1, 1);
                continue;
            }
            if(suspendStartDate.equals(LocalDate.of(1900, 1, 1))){
                suspendStartDate = entry.getKey().toLocalDate();
            }
            suspendEndDate = entry.getKey().toLocalDate();
        }

        // aggregate all the financial values of the household to one new application for verification
        SsiApplication aggregatedSsiApp = EthAppUtils.aggregateHouseholdValues(householdApps);

        //economics check
        if(EthAppUtils.getTotalMonthlyValue(aggregatedSsiApp, null).compareTo(BigDecimal.ZERO) == 0){
            return false;
        }

        //validate each household application credentials
        for(SsiApplication app:householdApps){
            if(!checkIndividualCredentials(app)){
                return false;
            }
        }

        return true;
    }

    public Boolean checkIndividualCredentials(SsiApplication ssiApp){

        List<HouseholdMember> household = ssiApp.getHouseholdComposition();
        if(household == null){
            return false;
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
        
        //check Ergome benefits
        if(BigInteger.valueOf(Long.valueOf(ssiApp.getErgomeR() == null? "0" : ssiApp.getErgomeR())).compareTo(BigInteger.valueOf(300)) > 0 ){
            return false;
        }

        // check that if there differences in Amka register
        if(differenceInAmka(ssiApp.getTaxisAmka())){
            return false;
        }

        //check if iban exists in other application
        if(mongoServ.findByIban(ssiApp.getIban()).size() > 1){
            return false;
        }
        log.info("return check true ?");
        return true;
    }

    //mock check for deceased members in the household
    private Boolean checkForDeceasedMembers(HouseholdMember member){
        return false;
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
