/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.ethereumserviceapp.model.*;
import com.example.ethereumserviceapp.model.entities.SsiApplication;
import com.example.ethereumserviceapp.service.EthereumService;
import com.example.ethereumserviceapp.service.MockServices;
import com.example.ethereumserviceapp.service.MongoService;
import com.example.ethereumserviceapp.service.MonitorService;
import com.example.ethereumserviceapp.utils.DateUtils;
import com.example.ethereumserviceapp.utils.EthAppUtils;
import com.example.ethereumserviceapp.utils.ExportCaseToExcel;
import com.example.ethereumserviceapp.utils.MonitorUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author nikos
 */
@Service
@Slf4j
public class MonitorServiceImpl implements MonitorService {

    private MongoService mongoServ;

    private EthereumService ethServ;

    private MockServices mockServ;

    @Autowired
    public MonitorServiceImpl(MongoService mongoS, EthereumService ethServ, MockServices mockServices) {
        this.mongoServ = mongoS;
        this.ethServ = ethServ;
        this.mockServ = mockServices;
    }

    MonitorUtils monitorUtils;

    @Override
    @Scheduled(cron = "0 0 12 * * ?")
    public void startScheduledMonitoring() {
        startMonitoring(LocalDateTime.now(), false, 0, false);
    }

    // parameters:
    // dateNow: the date of the monitoring process
    // uuid: the uuid of the case to be monitored
    // sync: parameter used for testing true for synchronization with the
    // blockchaing, false(default) make asynchronous transactions with the
    // blockchain
    @Override
    public void startMonitoring(LocalDateTime dateNow, Boolean isTest, double pValue, Boolean makeMockChecks) {
        LocalDateTime currentDate = dateNow == null ? LocalDateTime.now() : dateNow;
        List<String> uuids = this.ethServ.getAllCaseUUID();
        
        // count for random changes to applications for test/economy purposes
        Integer count = 0;

        uuids.stream().forEach(uuid -> {

            //if there are 2 or more changes already been made this day then stop the mock checks
            Boolean mockChecks = makeMockChecks && count < 2;

            Optional<Case> theCase = this.ethServ.getCaseByUUID(uuid);
            if (!theCase.isPresent()) {
                log.info("error - case not present");
                return;
            }

            Case monitoredCase = theCase.get();
            // int caseState = c.get().getState().getValue();

            if (monitoredCase.getState().equals(State.NONPRINCIPAL)) {
                log.info("case non principal");
                return;
            }

            log.info("looking into case {} with state {}", uuid, monitoredCase.getState());
            Optional<SsiApplication> ssiCase = mongoServ.findByUuid(uuid);
            if (!ssiCase.isPresent()) {
                log.info("application in database not present");
                updateCase(monitoredCase, State.REJECTED, null, currentDate, isTest, uuid, null);
                return;
            }

            SsiApplication ssiApp = ssiCase.get();

            // if this is not a principal case update state as non principal and continue to
            // the next case
            if (!ssiApp.getTaxisAfm().equals(ssiApp.getHouseholdPrincipal().getAfm())) {
                log.info("case non principal");
                updateCase(monitoredCase, State.NONPRINCIPAL, ssiApp, currentDate, isTest, uuid, null);
                return;
            }

            if (monitoredCase.getState().equals(State.REJECTED)) {
                Iterator<Entry<LocalDateTime, State>> itr = monitoredCase.getHistory().entrySet().iterator();
                while (itr.hasNext()) {
                    Map.Entry<LocalDateTime, State> entry = itr.next();
                    // if the case is rejected for more than one month then delete it
                    if (entry.getValue().equals(State.REJECTED)
                            && entry.getKey().toLocalDate().isBefore(currentDate.toLocalDate().minusMonths(1))) {
                        // Optional<SsiApplication> ssiCase = mongoServ.findByUuid(uuid);
                        Set<String> householdAfms = ssiApp.getHouseholdComposition().stream().map(s -> s.getAfm())
                                .collect(Collectors.toSet());
                        for (String hhuuid : mongoServ.findUuidByTaxisAfmIn(householdAfms)) {
                            this.mongoServ.deleteByUuid(hhuuid);
                        }
                        break;
                    }
                }
                // return;
            }
            // if the payment has failed for 3 consecutive months delete the case
            if (checkForFailedPayments(monitoredCase)) {
                log.info("payment failed for 3 concsecutive months");
                mongoServ.deleteByUuid(uuid);
                ethServ.deleteCaseByUuid(uuid);
                return;
            }

            Set<String> householdAfms = ssiApp.getHouseholdComposition().stream().map(s -> s.getAfm())
                    .collect(Collectors.toSet());
            List<SsiApplication> householdApps = mongoServ.findByTaxisAfmIn(householdAfms);

            // make external API calls that may update certain values in DB 
            //Case monitoredCase, double pValue, Boolean makeMockCheck, List<SsiApplication> householdApps, SsiApplication principalApp, Integer count, LocalDate currentDate
            externalChecksAndUpdate(monitoredCase, pValue, mockChecks, householdApps, ssiApp, count, currentDate.toLocalDate());

            // if (!externalChecksAndUpdate(monitoredCase, pValue, mockChecks, householdApps, ssiApp, count, currentDate.toLocalDate())) {
            //     updateCase(monitoredCase, State.REJECTED, ssiApp, currentDate, isTest, uuid, null);
            // }


            // check if credentials are valid and not expired
            if (!credentialsOk(uuid, householdApps, currentDate, isTest)) {
                log.info("credential fail");
                return;
            }
            LocalDateTime firstAcceptedDate = LocalDateTime.of(ssiApp.getTime(), LocalTime.of(00, 00, 00));
            Boolean accepted = false;
            // find the first day the case was accepted
            Iterator<Entry<LocalDateTime, State>> it = monitoredCase.getHistory().entrySet().iterator();
            while (it.hasNext() && !accepted) {
                Entry<LocalDateTime, State> entry = it.next();
                accepted = entry.getValue().equals(State.ACCEPTED) ? true : false;
                if (accepted) {
                    firstAcceptedDate = entry.getKey();
                }
            }
            // if (MonitorUtils.isCaseOlderThanSixMonths(firstAcceptedDate, currentDate) ||
            // !MonitorUtils.checkExternalSources()) {
            // //update the status of the case to REJECTED and the date with the current
            // date
            // updateCase(uuid, State.REJECTED, ssiCase.isPresent()? ssiApp : null,
            // currentDate, sync);
            // //this.mongoServ.deleteByUuid(uuid);
            // } else {
            // final SsiApplication ssiApp = ssiApp;
            // handleDeceasedMember(ssiApp);
            // check the application by the uuid and update the case accordingly

            // aggregate all the financial values of the household to one new application
            // for verification
            SsiApplication aggregatedSsiApp = EthAppUtils.aggregateHouseholdValues(householdApps);
            if (checkHouseholdCredentials(monitoredCase, ssiApp, householdApps, aggregatedSsiApp)
                    && !MonitorUtils.isCaseOlderThanSixMonths(firstAcceptedDate, currentDate)) {
                // if there is a missing application in the household suspend the case
                if (!checkHouseholdApplications(monitoredCase, ssiApp, householdApps, currentDate.toLocalDate())) {
                    log.info("household apps not all present, case suspended");
                    rejectOrSuspendCases(uuid, State.SUSPENDED, householdApps, currentDate, isTest);
                    return;
                }
                log.info("case accepted");
                updateCase(monitoredCase, State.ACCEPTED, ssiApp, currentDate, isTest, uuid, aggregatedSsiApp);
            } else {
                log.info("validation failed, case rejected");
                rejectOrSuspendCases(uuid, State.REJECTED, householdApps, currentDate, isTest);
            }
            // }
        });
    }

    private void rejectOrSuspendCases(String uuid, State state, List<SsiApplication> householdApps,
            LocalDateTime currentDate, Boolean isTest) {

        log.info("reject or suspend case with uuid :{} and state :{}", uuid, state);
        for (SsiApplication hhSsiApp : householdApps) {
            Optional<Case> theCase = this.ethServ.getCaseByUUID(hhSsiApp.getUuid());
            updateCase(theCase.isPresent() ? theCase.get() : new Case(), state, hhSsiApp, currentDate, isTest, uuid,
                    null);
        }
    }

    private void updateCase(Case monitoredCase, State state, SsiApplication ssiApp, LocalDateTime currentDate,
            Boolean isTest, String uuid, SsiApplication aggregatedSsiApp) {
                log.info("update case with uuid :{} and state :{}", uuid, state);
        // Optional<Case> theCase = this.ethServ.getCaseByUUID(uuid);
        if (!(monitoredCase.getUuid() == null || "".equals(monitoredCase.getUuid()))) {
            // synchronize transaction for test data only if the state changes
            // if(sync && theCase.get().getState().equals(state)){
            // sync = false;
            // }
            
            monitoredCase.setDate(currentDate);
            if (ssiApp != null) {
                List<SsiApplication> allHouseholdApps = mongoServ
                        .findByTaxisAfmIn(EthAppUtils.fetchAllHouseholdAfms(ssiApp));
                // BigDecimal offsetBefore = theCase.get().getOffset();
                if(!monitoredCase.getState().equals(State.NONPRINCIPAL)){
                    //MonitorUtils.calculateOffset(monitoredCase, ssiApp, allHouseholdApps);
                }

                monitoredCase.setState(state);
                
                if (state.equals(State.ACCEPTED)) {

                    // find the dates the case is accepted during this month and add 1 for the
                    // current day that hasn't yet been saved in the block chain
                    Long acceptedDatesCurrMonth = monitoredCase.getHistory().entrySet().stream().filter(
                            e -> (e.getKey().toLocalDate().compareTo(currentDate.toLocalDate().withDayOfMonth(1)) >= 0)
                                    && e.getKey().toLocalDate().compareTo(currentDate.toLocalDate()) <= 0
                                    && e.getValue().equals(State.ACCEPTED))
                            .count() + 1;

                    BigDecimal dailySum = MonitorUtils.calculateCurrentPayment(monitoredCase, ssiApp, allHouseholdApps,
                            currentDate.toLocalDate(), true);

                    monitoredCase.setDailyValue(
                            dailySum.divide(BigDecimal.valueOf(acceptedDatesCurrMonth), 2, RoundingMode.HALF_UP));
                    monitoredCase.setDailySum(dailySum);

                    if (isTest) {
                        ExportCaseToExcel excelExporter = new ExportCaseToExcel(monitoredCase, allHouseholdApps);
                        try {
                            excelExporter.export();
                        } catch (IOException e1) {
                            log.error("export to excel error :{}", e1.getMessage());
                        }
                    }
                }
                
                
                // synchronize transaction for test data only if the offset changes
                // if(offsetBefore.compareTo(theCase.get().getOffset()) == 0){
                //     sync = false;
                // }
            }
            this.ethServ.updateCase(monitoredCase);
            log.info("updated case uuid :{}, date :{}, state :{}, dailyValue :{}, offset:{}, sum:{} ", monitoredCase.getUuid(), monitoredCase.getDate(), monitoredCase.getState(), monitoredCase.getDailyValue(), monitoredCase.getOffset(), monitoredCase.getDailySum());
        } else {
            log.error("cannot find case {} while trying to update it", uuid);
        }
    }

    private Boolean credentialsOk(String uuid, List<SsiApplication> householdApps, LocalDateTime currentDate, Boolean sync){
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
            if (!expiresAt.isAfter(currentDate)) {
                //if credentials have expired update case as suspended(paused)
                rejectOrSuspendCases(uuid, State.SUSPENDED, householdApps, currentDate, sync);
                credsOk = false;
                break;
            }
            //check if the credential is revoked
            boolean isRevoked = this.ethServ.checkRevocationStatus(credIdAndExp[i].getId());
            log.info("is credential {} revoked? == {}", credIdAndExp[i].getId(), isRevoked);
            if (isRevoked){
                rejectOrSuspendCases(uuid, State.REJECTED, householdApps, currentDate, sync);
                credsOk = false;
                break;
            }
            credsOk = true;
        }

        return credsOk;
    }

    private Boolean checkForFailedPayments(Case monitoredCase){
        List<CasePayment> failedPayments = monitoredCase.getPaymentHistory().stream().filter(s -> s.getState().equals(State.FAILED)).collect(Collectors.toList());
        int failedCount = 1;
        if(failedPayments.size() >= 3){
            for(int i = 1; i<failedPayments.size(); i++){
                if(failedPayments.get(i).getPaymentDate().getMonthValue() == failedPayments.get(i-1).getPaymentDate().getMonthValue()+1){
                    failedCount++;
                    if(failedCount >= 3){
                        log.info("rejected - payment failed for 3 or more months");
                        return true; 
                    }
                } else{
                    failedCount = 1;
                }
            }
        }

        return false;
    }

    private Boolean checkHouseholdApplications(Case monitoredCase, SsiApplication ssiApp, List<SsiApplication> householdApps, LocalDate currentDate){
        //final LocalDate currentDate = LocalDate.now();
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
        return true;
    }

    private Boolean checkHouseholdCredentials(Case monitoredCase, SsiApplication ssiApp, List<SsiApplication> householdApps, SsiApplication aggregatedSsiApp){
        //final LocalDate currentDate = LocalDate.now();
        //final LocalDate endDate = LocalDate.of(currentDate.getYear(), currentDate.getMonthValue(), EthAppUtils.monthDays(currentDate));

        List<HouseholdMember> household = ssiApp.getHouseholdComposition();

        //check if by the end of the month all the members of the household have submitted an application
        // if(currentDate.equals(endDate)){
        //     List<String> appAfms = householdApps.stream().map(a -> a.getTaxisAfm()).collect(Collectors.toList());
        //     List<String> householdAfms = household.stream().map(m -> m.getAfm()).collect(Collectors.toList());

        //     if(!householdAfms.containsAll(appAfms)){
        //         return false;
        //     }
        // }

        //check for deceased members in the household
        // if(household.stream().anyMatch(h -> checkForDeceasedMembers(h))){
        //     log.info("rejected - deceased member in household");
        //     return false;
        // }

        //check if there are more than one principal members
        Long principalCount = householdApps.stream().filter(h -> h.getHouseholdPrincipal().getAfm().equals(h.getTaxisAfm())).count();
        log.info("principal count :{}", principalCount);
        if(principalCount > 1){
            log.info("rejected - more than one principal in household");
            return false;
        }
        // if(mongoServ.findByHouseholdPrincipalIn(household).size()>1){
        //     log.info("rejected - more than one principal in household");
        //     return false;
        // }

        for(HouseholdMember member:household){
            List<SsiApplication> householdDuplicates = mongoServ.findByHouseholdComposition(member);
            if(householdDuplicates.size()>1){
                log.info("rejected - duplicate applications in household");
                return false;
            }
        }

        //check if two months have passed while the application is in status suspended
        Iterator<Entry<LocalDateTime, State>> it = monitoredCase.getHistory().entrySet().iterator();
        LocalDate suspendStartDate = LocalDate.of(1900, 1, 1);
        LocalDate suspendEndDate = LocalDate.of(1900, 1, 1);
        while(it.hasNext()){
            if(suspendEndDate.equals(suspendStartDate.plusMonths(2))){
                log.info("rejected - application suspended for 2 months or more");
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

        //economics check
        if(EthAppUtils.getTotalMonthlyValue(aggregatedSsiApp, null).compareTo(BigDecimal.ZERO) == 0){
            log.info("rejected - financial data restriction (total household income > payment thresshold)");
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
            log.info("rejected - household missing");
            return false;
        }

        //external oaed check
        // if(!oaedRegistrationCheck(ssiApp.getOaedId())){
        //     log.info("rejected - applicant not found on OAED");
        //     return false;
        // }

        //check unemployment status
        // if(ssiApp.getUnemployed().equals("false")){
        //     return false;
        // }

        //external housing subsidy check
        // if(!houseBenefitCheck(ssiApp.getTaxisAfm()){
        //     log.info("rejected - housing benefits");
        //     return false;
        // }
        
        // check for luxury living
        // if(ssiApp.getLuxury() == null? false : ssiApp.getLuxury().equals(String.valueOf(Boolean.TRUE))){
        //     log.info("rejected - luxury living");
        //     return false;
        // }
        
        // check that if there differences in Amka register
        // if(differenceInAmka(ssiApp.getTaxisAmka())){
        //     log.info("rejected - differences in AMKA");
        //     return false;
        // }

        //check if iban exists in other application
        if(mongoServ.findByIban(ssiApp.getIban()).size() > 1){
            log.info("rejected - duplicate IBAN");
            return false;
        }
        log.info("return check true ?");
        return true;
    }

    //mock check for deceased members in the household
    private Boolean checkForDeceasedMembers(HouseholdMember member){
        return false;
    }

    private void handleDeceasedMember(SsiApplication ssiApp) {
        List<HouseholdMember> household = ssiApp.getHouseholdComposition();
        boolean changed = false;
        for (HouseholdMember member : household) {
            if (checkForDeceasedMembers(member)) {
                household.remove(member);
                changed = true;
            }
        }

        if (changed) {
            ssiApp.setHouseholdComposition(household);
            LinkedHashMap<String, List<HouseholdMember>> hhHistory = ssiApp.getHouseholdCompositionHistory();
            hhHistory.put(DateUtils.dateToString(LocalDateTime.now()), household);
            ssiApp.setHouseholdCompositionHistory(hhHistory);
            mongoServ.updateSsiApp(ssiApp);
        }
    }



    //mock housing subsidy check
    private void houseBenefitCheck(Case monitoredCase, SsiApplication ssiApp, List<LocalDate> rejectionDates){
        // mock check
        if(monitoredCase.equals("")){
            LocalDate actualUpdateDate = LocalDate.now(); // mock date, this should return the actual date of the altered credential
            rejectionDates.add(actualUpdateDate);
        }
    }

    private void luxuryLivingCheck(Case monitoredCase, SsiApplication ssiApp, List<LocalDate> rejectionDates){
        // mock check
        if(monitoredCase.equals("")){
            LocalDate actualUpdateDate = LocalDate.now(); // mock date, this should return the actual date of the altered credential
            ssiApp.setLuxury("true");
            mongoServ.updateSsiApp(ssiApp);
            rejectionDates.add(actualUpdateDate);
        }
    }

    //mockAmkaCheck
    private void amkaCheck(Case monitoredCase, SsiApplication ssiApp, List<LocalDate> rejectionDates){
        // mock check
        if(monitoredCase.equals("")){
            LocalDate actualUpdateDate = LocalDate.now(); // mock date, this should return the actual date of the altered credential
            rejectionDates.add(actualUpdateDate);
        }
    }

    //update financial values and histories

    /**
     * @return the number of API calls that resulted in a change of value.
     * At maximum two API calls per run should change
     */
    private int financialsCheck(SsiApplication principalApp,
                                double pValue, Boolean makeMockCheck,
                                List<SsiApplication> householdApps, Integer count, LocalDate currentDate,  List<LocalDate> rejectionDates) {
        //mock check
        Boolean changed = false;
        LinkedHashMap<String, String> financialHistoryMap;
        String updatedCaseUUID = "";

        Optional<UpdateMockResult> otherBenefitsResult = mockServ.getUpdatedOtherBenefitValue(currentDate, currentDate, pValue, principalApp, makeMockCheck && count < 2, householdApps);
        if (otherBenefitsResult.isPresent() && count < 2) {
            updatedCaseUUID = otherBenefitsResult.get().getUuid();
            changed = true;
            count++;
            // get the ssiApp that was updated
            Optional<SsiApplication> updatedApp = mongoServ.findByUuid(updatedCaseUUID);
            financialHistoryMap = updatedApp.get().getOtherBenefitsRHistory();
            if (financialHistoryMap == null) {
                financialHistoryMap = new LinkedHashMap<>();
            }
            financialHistoryMap.put(DateUtils.dateToString(otherBenefitsResult.get().getDate()), String.valueOf(otherBenefitsResult.get().getValue()));
            if (updatedApp.isPresent()) {
                updatedApp.get().setOtherBenefitsRHistory(financialHistoryMap);
                updatedApp.get().setOtherBenefitsR(String.valueOf(otherBenefitsResult.get().getValue()));
                mongoServ.updateSsiApp(principalApp);
            }
        }

        Optional<UpdateMockResult> unemploymentBenefit = mockServ.getUpdatedOAEDBenefitValue(currentDate, currentDate, pValue, principalApp, makeMockCheck && count < 2, householdApps);
        if (unemploymentBenefit.isPresent() && count < 2) {
            updatedCaseUUID = unemploymentBenefit.get().getUuid();
            changed = true;
            count++;
            Optional<SsiApplication> updatedApp = mongoServ.findByUuid(updatedCaseUUID);
            financialHistoryMap = updatedApp.get().getUnemploymentBenefitRHistory();
            if (financialHistoryMap == null) {
                financialHistoryMap = new LinkedHashMap<>();
            }
            financialHistoryMap.put(DateUtils.dateToString(unemploymentBenefit.get().getDate()), String.valueOf(unemploymentBenefit.get().getValue()));
            if (updatedApp.isPresent()) {
                updatedApp.get().setUnemploymentBenefitRHistory(financialHistoryMap);
                updatedApp.get().setUnemploymentBenefitR(String.valueOf(unemploymentBenefit.get().getValue()));
                mongoServ.updateSsiApp(updatedApp.get());
            }
        }


        Optional<UpdateMockResult> ergomBenefitUpdate = mockServ.getUpdatedERGOMValue(currentDate, currentDate,
                pValue, principalApp, makeMockCheck && count < 2, householdApps);
        if (ergomBenefitUpdate.isPresent() && count < 2) {
            updatedCaseUUID = ergomBenefitUpdate.get().getUuid();
            changed = true;
            count++;
            Optional<SsiApplication> updatedApp = mongoServ.findByUuid(updatedCaseUUID);
            financialHistoryMap = updatedApp.get().getErgomRHistory();
            if (financialHistoryMap == null) {
                financialHistoryMap = new LinkedHashMap<>();
            }
            financialHistoryMap.put(DateUtils.dateToString(ergomBenefitUpdate.get().getDate()), String.valueOf(ergomBenefitUpdate.get().getValue()));
            if (updatedApp.isPresent()) {
                updatedApp.get().setUnemploymentBenefitRHistory(financialHistoryMap);
                updatedApp.get().setErgomeR(String.valueOf(ergomBenefitUpdate.get().getValue()));
                mongoServ.updateSsiApp(updatedApp.get());
            }
        }


        Optional<UpdateMockResult> salary = mockServ.getUpdateSalariesData(currentDate, currentDate, pValue, principalApp,
                makeMockCheck && count < 2, householdApps);
        if (unemploymentBenefit.isPresent() && count < 2) {
            changed = true;
            count++;
            principalApp.setSalariesR(String.valueOf(salary.get().getValue()));
            Optional<SsiApplication> updatedApp = mongoServ.findByUuid(updatedCaseUUID);
            financialHistoryMap = updatedApp.get().getSalariesRHistory();
            if (financialHistoryMap == null) {
                financialHistoryMap = new LinkedHashMap<>();
            }
            financialHistoryMap.put(DateUtils.dateToString(salary.get().getDate()), String.valueOf(salary.get().getValue()));
            if (updatedApp.isPresent()) {
                updatedApp.get().setSalariesRHistory(financialHistoryMap);
                updatedApp.get().setSalariesR(String.valueOf(salary.get().getValue()));
                mongoServ.updateSsiApp(updatedApp.get());
            }
        }


        Optional<UpdateMockResult> pensionResult = mockServ.getUpdatedPension(currentDate, currentDate, pValue, principalApp,
                makeMockCheck && count < 2, householdApps);
        if (pensionResult.isPresent() && count < 2) {
            changed = true;
            count++;
            principalApp.setSalariesR(String.valueOf(pensionResult.get().getValue()));
            Optional<SsiApplication> updatedApp = mongoServ.findByUuid(updatedCaseUUID);
            financialHistoryMap = updatedApp.get().getPensionsRHistory();
            if (financialHistoryMap == null) {
                financialHistoryMap = new LinkedHashMap<>();
            }
            financialHistoryMap.put(DateUtils.dateToString(pensionResult.get().getDate()), String.valueOf(pensionResult.get().getValue()));
            if (updatedApp.isPresent()) {
                updatedApp.get().setPensionsRHistory(financialHistoryMap);
                updatedApp.get().setPensionsR(String.valueOf(salary.get().getValue()));
                mongoServ.updateSsiApp(updatedApp.get());
            }
        }

        Optional<UpdateMockResult> freelanceUpdate = mockServ.getUpdatedFreelance(currentDate, currentDate, pValue, principalApp,
                makeMockCheck && count < 2, householdApps);
        if (freelanceUpdate.isPresent() && count < 2) {
            changed = true;
            count++;
            principalApp.setSalariesR(String.valueOf(freelanceUpdate.get().getValue()));
            Optional<SsiApplication> updatedApp = mongoServ.findByUuid(updatedCaseUUID);
            financialHistoryMap = updatedApp.get().getSalariesRHistory();
            if (financialHistoryMap == null) {
                financialHistoryMap = new LinkedHashMap<>();
            }
            financialHistoryMap.put(DateUtils.dateToString(freelanceUpdate.get().getDate()), String.valueOf(salary.get().getValue()));
            if (updatedApp.isPresent()) {
                updatedApp.get().setFreelanceRHistory(financialHistoryMap);
                updatedApp.get().setFreelanceR(String.valueOf(freelanceUpdate.get().getValue()));
                mongoServ.updateSsiApp(updatedApp.get());
            }
        }

        Optional<UpdateMockResult> depositsUpdates = mockServ.getUpdatedDepoists(currentDate, currentDate, pValue, principalApp,
                makeMockCheck && count < 2, householdApps);
        if (depositsUpdates.isPresent() && count < 2) {
            changed = true;
            count++;
            principalApp.setSalariesR(String.valueOf(depositsUpdates.get().getValue()));
            Optional<SsiApplication> updatedApp = mongoServ.findByUuid(updatedCaseUUID);
            financialHistoryMap = updatedApp.get().getDepositsAHistory();
            if (financialHistoryMap == null) {
                financialHistoryMap = new LinkedHashMap<>();
            }
            financialHistoryMap.put(DateUtils.dateToString(depositsUpdates.get().getDate()), String.valueOf(salary.get().getValue()));
            if (updatedApp.isPresent()) {
                updatedApp.get().setDepositsAHistory(financialHistoryMap);
                updatedApp.get().setDepositsA(String.valueOf(depositsUpdates.get().getValue()));
                mongoServ.updateSsiApp(updatedApp.get());
            }
        }
        if (changed) {
            mongoServ.updateSsiApp(principalApp);
        }
        //if the financial checks fails now the application should be rejected



        return count;
    }


    private int deceasedCheck(SsiApplication principalApp,
                              double pValue, Boolean makeMockCheck,
                              List<SsiApplication> householdApps, Integer count, LocalDate currentDate,  List<LocalDate> rejectionDates) {
        //mock check
        LinkedHashMap<String, List<HouseholdMember>> householdCompositionHistory;
        Optional<BooleanMockResult> deceasedResult = mockServ.getDeaths(currentDate, currentDate, pValue,
                principalApp, makeMockCheck && count < 2, householdApps);
        if (deceasedResult.isPresent() && count < 2) {
            count++;
            //update the list of household members
            LinkedHashMap<String, List<HouseholdMember>> householdHistory = principalApp.getHouseholdCompositionHistory();
            List<HouseholdMember> newHoushold = principalApp.getHouseholdComposition().stream().filter(member -> !member.getAfm().equals(deceasedResult.get().getData())).collect(Collectors.toList());
            householdHistory.put(DateUtils.dateToString(deceasedResult.get().getDate()), newHoushold);
            principalApp.setHouseholdCompositionHistory(householdHistory);
            principalApp.setHouseholdComposition(newHoushold);
            mongoServ.updateSsiApp(principalApp);
        }
        return count;
    }


    //mock oaed registration check
    private int oaedRegistrationCheck(SsiApplication principalApp,
                                       double pValue, Boolean makeMockCheck,
                                       List<SsiApplication> householdApps, Integer count, LocalDate currentDate,  List<LocalDate> rejectionDates) {
        //mock check
        Optional<BooleanMockResult> oaedRegistrationResult = mockServ.getOAEDRegistration(currentDate, currentDate, pValue,
                principalApp, makeMockCheck && count < 2, householdApps);
        String updatedCaseUUID = oaedRegistrationResult.get().getUuid();
        if (oaedRegistrationResult.isPresent() && count < 2) {
            count++;
            rejectOrSuspendCases(updatedCaseUUID,State.REJECTED,
                    householdApps,oaedRegistrationResult.get().getDate().atStartOfDay(),false);
        }
        return count;
    }

    //mock oaed registration check
    private int luxuryCheck(SsiApplication principalApp,
                                      double pValue, Boolean makeMockCheck,
                                      List<SsiApplication> householdApps, Integer count, LocalDate currentDate,  List<LocalDate> rejectionDates) {
        //mock check
        Optional<BooleanMockResult> luxuryCheckResult = mockServ.getLuxury(currentDate, currentDate, pValue,
                principalApp, makeMockCheck && count < 2, householdApps);
        String updatedCaseUUID = luxuryCheckResult.get().getUuid();
        if (luxuryCheckResult.isPresent() && count < 2) {
            count++;
            rejectOrSuspendCases(updatedCaseUUID,State.REJECTED,
                    householdApps,luxuryCheckResult.get().getDate().atStartOfDay(),false);
        }
        return count;
    }


    // external api calls
    // calls all extrnal APIs
    private Boolean externalChecksAndUpdate(Case monitoredCase, double pValue, Boolean makeMockCheck,
                                            List<SsiApplication> householdApps, SsiApplication principalApp,
                                            Integer count, LocalDate currentDate) {

        List<LocalDate> rejectionDates = new ArrayList<>();
        //validate each household application credentials

        // calls external APIs and updates DB
        int apiCallsUpdates = financialsCheck(principalApp, pValue, makeMockCheck, householdApps, count, currentDate, rejectionDates);
        if (apiCallsUpdates < 2) {
            //handle deceased member
            apiCallsUpdates = deceasedCheck(principalApp, pValue, makeMockCheck, householdApps, count, currentDate, rejectionDates);
            if (apiCallsUpdates < 2) {
                apiCallsUpdates = oaedRegistrationCheck(principalApp, pValue, makeMockCheck, householdApps, count, currentDate, rejectionDates);
                if(apiCallsUpdates <2){
                    apiCallsUpdates = luxuryCheck(principalApp, pValue, makeMockCheck, householdApps, count, currentDate, rejectionDates);
                }
            }
        }

        if (!rejectionDates.isEmpty()) {
            LocalDate minDate = rejectionDates.stream()
                    .min(Comparator.comparing(LocalDate::toEpochDay))
                    .get();
            if (monitoredCase.getRejectionDate().equals("") || DateUtils.dateStringToLD(monitoredCase.getRejectionDate()).isAfter(minDate)) {
                monitoredCase.setRejectionDate(DateUtils.dateToString(minDate));
            }
            return false;
        }

        return true;

    }

}
