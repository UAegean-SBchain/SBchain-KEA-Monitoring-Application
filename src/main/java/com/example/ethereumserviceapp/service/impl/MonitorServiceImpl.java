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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
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

import org.apache.xpath.operations.Bool;
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

    private final MongoService mongoServ;

    private final EthereumService ethServ;

    private final MockServices mockServ;

    @Autowired
    public MonitorServiceImpl(MongoService mongoS, EthereumService ethServ, MockServices mockServices) {
        this.mongoServ = mongoS;
        this.ethServ = ethServ;
        this.mockServ = mockServices;
    }

    MonitorUtils monitorUtils;

    @Override
    //@Scheduled(cron = "0 0 12 * * ?")
    public void startScheduledMonitoring() {
        startMonitoring(LocalDateTime.now(), false, 0, false, null);
    }

    // parameters:
    // dateNow: the date of the monitoring process
    // uuid: the uuid of the case to be monitored
    // sync: parameter used for testing true for synchronization with the
    // blockchaing, false(default) make asynchronous transactions with the
    // blockchain
    @Override
    public void startMonitoring(LocalDateTime dateNow, Boolean isTest, double pValue, Boolean makeMockChecks, List<CaseAppDTO> storeDataForSE) {
        LocalDateTime currentDate = dateNow == null ? LocalDateTime.now() : dateNow;
        List<String> uuids = this.ethServ.getAllCaseUUID();

        // count for random changes to applications for test/economy purposes
        //Integer count = 0;
        AtomicInteger count = new AtomicInteger();
        count.set(0);
        //for(String uuid:uuids){
        uuids.parallelStream().forEach(uuid -> {
            // log.info("========================= count :{}", count);
            // if there are 2 or more changes already been made this day then stop the mock
            // checks

            Boolean mockChecks = makeMockChecks && count.get() < 2;

            // check if any of this case's credentials has changed during the monitoring
            // process
            Boolean credChange = false;

            Optional<Case> theCase = this.ethServ.getCaseByUUID(uuid);
            if (!theCase.isPresent()) {
                log.info("error - case not present");
                return;
                //continue;
            }

            Case monitoredCase = theCase.get();
            // int caseState = c.get().getState().getValue();

            if (monitoredCase.getState().equals(State.NONPRINCIPAL)) {
                log.info("case non principal");
                return;
                //continue;
            }

            log.info("looking into case {} with state {}", uuid, monitoredCase.getState());
            Optional<SsiApplication> ssiCase = mongoServ.findByUuid(uuid);
            if (!ssiCase.isPresent()) {
                log.info("application in database not present");
                monitoredCase.setRejectionCode(RejectionCode.REJECTION104);
                updateCase(monitoredCase, State.REJECTED, null, currentDate, isTest, uuid, null, credChange,
                        storeDataForSE);
                return;
                //continue;
            }

            SsiApplication ssiApp = ssiCase.get();

            // if this is not a principal case update state as non principal and continue to
            // the next case
            if (!ssiApp.getTaxisAfm().equals(ssiApp.getHouseholdPrincipal().getAfm())
                    && !(monitoredCase.getState().equals(State.REJECTED)
                            || monitoredCase.getState().equals(State.SUSPENDED))) {
                log.info("case non principal");
                updateCase(monitoredCase, State.NONPRINCIPAL, ssiApp, currentDate, isTest, uuid, null, credChange,
                        storeDataForSE);
                return;
                //continue;
            }

            // if this is not a principal case that has been rejected or suspended then
            // continue to the next case
            if ((monitoredCase.getState().equals(State.REJECTED) || monitoredCase.getState().equals(State.SUSPENDED))
                    && !ssiApp.getTaxisAfm().equals(ssiApp.getHouseholdPrincipal().getAfm())) {
                log.info("non principal case that is rejected or suspended");
                return;
                //continue;
            }

            // TODO this check should probably be held in a different scheduled method and not run during social economy
            // if the case is rejected for more than one month then delete it
            // if (monitoredCase.getState().equals(State.REJECTED) && monitoredCase.getDate().toLocalDate().isBefore(currentDate.toLocalDate().minusMonths(1))) {
                
            //     Set<String> householdAfms = ssiApp.getHouseholdComposition().stream().map(s -> s.getAfm())
            //                 .collect(Collectors.toSet());
            //     for (String hhuuid : mongoServ.findUuidByTaxisAfmIn(householdAfms)) {
            //         this.mongoServ.deleteByUuid(hhuuid);
            //     }
            // }

            // if the payment has failed for 3 consecutive months delete the case
            if (checkForFailedPayments(monitoredCase)) {
                log.info("payment failed for 3 concsecutive months");
                mongoServ.deleteByUuid(uuid);
                ethServ.deleteCaseByUuid(uuid);
                return;
                //continue;
            }

            Set<String> householdAfms = ssiApp.getHouseholdComposition().stream().map(s -> s.getAfm())
                    .collect(Collectors.toSet());
            List<SsiApplication> householdApps = mongoServ.findByTaxisAfmIn(householdAfms);

            // make external API calls that may update certain values in DB
            // Case monitoredCase, double pValue, Boolean makeMockCheck,
            // List<SsiApplication> householdApps, SsiApplication principalApp, Integer
            // count, LocalDate currentDate
            ExternalChecksResult exCheckResult = externalChecksAndUpdate(monitoredCase, pValue, mockChecks,
                    householdApps, ssiApp, count.get(), currentDate.toLocalDate());
            count.set(exCheckResult.getCount());
            // if there is a financial change then set the credChange to true so that the service calls tha calculate offset method
            credChange = exCheckResult.getChangedFinancials() || exCheckResult.getRejection();

            // if the case is already rejected update only if there are new credential changes
            if(monitoredCase.getState().equals(State.REJECTED)){
                if(exCheckResult.getRejection()){
                    monitoredCase.setRejectionCode(exCheckResult.getRejectionCode());
                    monitoredCase.setRejectionDate(DateUtils.dateToString(exCheckResult.getDate()));
                }
                if(credChange || exCheckResult.getRejection()){
                    log.info("rejected case uuid :{}, date :{}, state :{}, dailyValue :{}, offset:{}, sum:{}, rejectionCode :{}, rejection date :{} ", monitoredCase.getUuid(), monitoredCase.getDate(), monitoredCase.getState(), monitoredCase.getDailyValue(), monitoredCase.getOffset(), monitoredCase.getDailySum(), monitoredCase.getRejectionCode(), monitoredCase.getRejectionDate());
            
                    updateCase(monitoredCase, State.REJECTED, ssiApp, currentDate, isTest, uuid, null, credChange, storeDataForSE);
                    //continue;
                    return;
                }
                if(storeDataForSE != null){
                    if(!monitoredCase.getState().equals(State.NONPRINCIPAL)){
                        CaseAppDTO caseAppDto = new CaseAppDTO();
                        caseAppDto.setPrincipalCase(monitoredCase);
                        caseAppDto.setHouseholdApps(householdApps);
                        storeDataForSE.add(caseAppDto);
                    }
                }
                //continue;
                return;
            }

            if(exCheckResult.getRejection()){
                monitoredCase.setRejectionDate(DateUtils.dateToString(exCheckResult.getDate()));
                monitoredCase.setRejectionCode(exCheckResult.getRejectionCode());
                monitoredCase.setDailyValue(BigDecimal.ZERO);
                monitoredCase.setDailySum(BigDecimal.ZERO);
                updateCase(monitoredCase, State.REJECTED, ssiApp, currentDate, isTest, uuid, null, credChange, storeDataForSE);
                //rejectOrSuspendCases(uuid, State.REJECTED, householdApps, currentDate, DateUtils.dateToString(exCheckResult.getDate()), RejectionCode.REJECTION1, isTest, true, storeDataForSE);
                //continue;
                return;
            }

            // if (!externalChecksAndUpdate(monitoredCase, pValue, mockChecks, householdApps, ssiApp, count, currentDate.toLocalDate())) {
            //     updateCase(monitoredCase, State.REJECTED, ssiApp, currentDate, isTest, uuid, null);
            // }


            // check if credentials are valid and not expired
            if (!credentialsOk(monitoredCase, ssiApp, householdApps, currentDate, isTest, credChange, storeDataForSE)) {
                log.info("credential fail");
                return;
                //continue;
            }
            LocalDateTime firstAcceptedDate = LocalDateTime.of(ssiApp.getTime(), LocalTime.of(00, 00, 00));
            Boolean accepted = false;
            // find the first day the case was accepted
            Iterator<Entry<LocalDateTime, State>> it = monitoredCase.getHistory().entrySet().iterator();
            while (it.hasNext() && !accepted) {
                Entry<LocalDateTime, State> entry = it.next();
                accepted = entry.getValue().equals(State.ACCEPTED);
                if (accepted) {
                    firstAcceptedDate = entry.getKey();
                }
            }

            
            // final SsiApplication ssiApp = ssiApp;
            // handleDeceasedMember(ssiApp);
            // check the application by the uuid and update the case accordingly

            // aggregate all the financial values of the household to one new application
            // for verification
            SsiApplication aggregatedSsiApp = new SsiApplication();

            //if there is only 1 application in the household don't aggregate the values
            if(householdApps.size()==1){
                aggregatedSsiApp = householdApps.get(0);
            } else {
                aggregatedSsiApp = EthAppUtils.aggregateHouseholdValues(householdApps);
            }

            if (MonitorUtils.isCaseOlderThanSixMonths(firstAcceptedDate, currentDate)) {
                //update the status of the case to REJECTED and the date with the current date
                    monitoredCase.setRejectionDate(DateUtils.dateToString(currentDate.toLocalDate()));
                    monitoredCase.setRejectionCode(RejectionCode.REJECTION103);
                    monitoredCase.setDailyValue(BigDecimal.ZERO);
                    monitoredCase.setDailySum(BigDecimal.ZERO);
                    updateCase(monitoredCase, State.REJECTED, ssiApp, currentDate, isTest, uuid, aggregatedSsiApp, credChange, storeDataForSE);
                    //updateCase(uuid, State.REJECTED, ssiCase.isPresent()? ssiApp : null,currentDate, sync);
                //this.mongoServ.deleteByUuid(uuid);
                }
            //SsiApplication aggregatedSsiApp = EthAppUtils.aggregateHouseholdValues(householdApps);
            if (checkHouseholdCredentials(monitoredCase, ssiApp, householdApps, aggregatedSsiApp)
                    && !MonitorUtils.isCaseOlderThanSixMonths(firstAcceptedDate, currentDate)) {
                // if there is a missing application in the household suspend the case
                if (!checkHouseholdApplications(monitoredCase, ssiApp, currentDate.toLocalDate())) {
                    log.info("household apps not all present, case suspended");
                    monitoredCase.setRejectionCode(RejectionCode.REJECTION0);
                    monitoredCase.setDailyValue(BigDecimal.ZERO);
                    monitoredCase.setDailySum(BigDecimal.ZERO);
                    updateCase(monitoredCase, State.SUSPENDED, ssiApp, currentDate, isTest, monitoredCase.getUuid(), null, credChange, storeDataForSE);
                    //rejectOrSuspendCases(uuid, State.SUSPENDED, householdApps, currentDate, null, null, isTest, credChange, storeDataForSE);
                    //continue;
                    return;
                }
                log.info("case accepted");
                updateCase(monitoredCase, State.ACCEPTED, ssiApp, currentDate, isTest, uuid, aggregatedSsiApp, credChange, storeDataForSE);
                //continue;
                return;
            } else {
                log.info("validation failed, case rejected");
                monitoredCase.setRejectionDate(DateUtils.dateToString(currentDate.toLocalDate()));
                //monitoredCase.setRejectionCode(RejectionCode.REJECTION1);
                monitoredCase.setDailyValue(BigDecimal.ZERO);
                monitoredCase.setDailySum(BigDecimal.ZERO);
                //rejectOrSuspendCases(uuid, State.REJECTED, householdApps, currentDate, null, RejectionCode.REJECTION1, isTest, credChange, storeDataForSE);
                updateCase(monitoredCase, State.REJECTED, ssiApp, currentDate, isTest, uuid, aggregatedSsiApp, credChange, storeDataForSE);
                //continue;
                return;
            }
            // }
        });
        //}
    }

    // private void rejectOrSuspendCases(String uuid, State state, List<SsiApplication> householdApps,
    //         LocalDateTime currentDate, String rejectionDate, RejectionCode rejectionCode, Boolean isTest, Boolean credChange, List<CaseAppDTO> storeDataForSE) {

    //     log.info("reject or suspend case with uuid :{} and state :{}", uuid, state);
    //     for (SsiApplication hhSsiApp : householdApps) {
    //         Optional<Case> theCase = this.ethServ.getCaseByUUID(hhSsiApp.getUuid());
    //         Case monitoredCase = theCase.isPresent() ? theCase.get() : new Case();
    //         //if(rejectionDate != null && !"".equals(rejectionDate)){
    //             monitoredCase.setRejectionDate(rejectionDate);
    //             monitoredCase.setRejectionCode(rejectionCode);
    //             monitoredCase.setDailyValue(BigDecimal.ZERO);
    //             monitoredCase.setDailySum(BigDecimal.ZERO);
    //         //}
    //         updateCase(monitoredCase, state, hhSsiApp, currentDate, isTest, uuid,
    //                 null, credChange, storeDataForSE);
    //     }
    // }

    private Boolean checkHouseholdApplications(Case monitoredCase, SsiApplication ssiApp, LocalDate currentDate) {
        //final LocalDate currentDate = LocalDate.now();
        final LocalDate endDate = LocalDate.of(currentDate.getYear(), currentDate.getMonthValue(), EthAppUtils.monthDays(currentDate));

        List<HouseholdMember> household = ssiApp.getHouseholdComposition();

        //check if by the end of the month all the members of the household have submitted an application
        if (currentDate.equals(endDate)) {
            for(HouseholdMember hm: household){
                if(mongoServ.findByTaxisAfm(hm.getAfm()).isEmpty()){
                    return false;
                }
            }
        }
        return true;
    }

    private void updateCase(Case monitoredCase, State state, SsiApplication ssiApp, LocalDateTime currentDate,
            Boolean isTest, String uuid, SsiApplication aggregatedSsiApp, Boolean credChange, List<CaseAppDTO> storeDataForSE) {
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

                // calculate offset only for principal members and only if there has been a change in credentials and there has already been a payment
                if(ssiApp.getTaxisAfm().equals(ssiApp.getHouseholdPrincipal().getAfm())){
                    if((credChange && !monitoredCase.getPaymentHistory().isEmpty()) || isTest){
                        MonitorUtils.calculateOffset(monitoredCase, ssiApp, allHouseholdApps);
                        log.info("offset calculation for case :{}, offset :{}", monitoredCase.getUuid(), monitoredCase.getOffset());
                    }
                }
                monitoredCase.setState(state);

                if (state.equals(State.ACCEPTED)) {
                    monitoredCase.setRejectionDate("");
                    monitoredCase.setRejectionCode(RejectionCode.REJECTION0);

                    // find the dates the case is accepted during this month and add 1 for the
                    // current day that hasn't yet been saved in the block chain
                    Long acceptedDatesCurrMonth = monitoredCase.getHistory().entrySet().stream().filter(
                        e -> (e.getKey().toLocalDate().compareTo(currentDate.toLocalDate().withDayOfMonth(1)) >= 0)
                                && e.getKey().toLocalDate().compareTo(currentDate.toLocalDate()) <= 0
                                && e.getValue().equals(State.ACCEPTED))
                        .count() + 1;

                    BigDecimal dailySum = BigDecimal.ZERO;

                    // SsiApplication aggregatedApp = new SsiApplication();

                    // //if there is only 1 application in the household don't aggregate the values
                    // if(allHouseholdApps.size()==1){
                    //     aggregatedApp = allHouseholdApps.get(0);
                    // } else {
                    //     aggregatedApp = EthAppUtils.aggregateHouseholdValues(allHouseholdApps);
                    // }
                    //int count = 0;
                   // if(credChange){
                        dailySum = MonitorUtils.calculateCurrentPayment(monitoredCase, ssiApp, allHouseholdApps, currentDate.toLocalDate(), true);
                        //} else {
                        //dailySum = EthAppUtils.calculatePayment(EthAppUtils.monthDays(currentDate.toLocalDate()), acceptedDatesCurrMonth.intValue(), aggregatedSsiApp, currentDate.toLocalDate());
                    
                    //}

                    monitoredCase.setDailyValue(
                            dailySum.divide(BigDecimal.valueOf(acceptedDatesCurrMonth), 2, RoundingMode.HALF_UP));
                    monitoredCase.setDailySum(dailySum);

                    if (isTest) {
                        ExportCaseToExcel excelExporter = new ExportCaseToExcel(monitoredCase, allHouseholdApps);
                        try {
                            excelExporter.export(isTest, "ExampleCase.xlsx");
                        } catch (IOException e1) {
                            log.error("export to excel error :{}", e1.getMessage());
                        }
                    }
                }
                
                 //store cases for social economy
                if(storeDataForSE != null){
                    if(!monitoredCase.getState().equals(State.NONPRINCIPAL)){
                        CaseAppDTO caseAppDto = new CaseAppDTO();
                        caseAppDto.setPrincipalCase(monitoredCase);
                        caseAppDto.setHouseholdApps(allHouseholdApps);
                        storeDataForSE.add(caseAppDto);
                    }
                }
            }
            this.ethServ.updateCase(monitoredCase);
            log.info("updated case uuid :{}, date :{}, state :{}, dailyValue :{}, offset:{}, sum:{}, rejectionCode :{}, rejectionDate :{} ", monitoredCase.getUuid(), monitoredCase.getDate(), monitoredCase.getState(), monitoredCase.getDailyValue(), monitoredCase.getOffset(), monitoredCase.getDailySum(), monitoredCase.getRejectionCode(), monitoredCase.getRejectionDate());
            
        } else {
            log.error("cannot find case {} while trying to update it", uuid);
        }

        //return;
    }

    private Boolean credentialsOk(Case monitoredCase, SsiApplication ssiApp, List<SsiApplication> householdApps, LocalDateTime currentDate, Boolean isTest, Boolean credChange, List<CaseAppDTO> storeDataForSE){
        Boolean credsOk = true;
        CredsAndExp[] credIdAndExp = this.mongoServ.findCredentialIdsByUuid(monitoredCase.getUuid());
        if (credIdAndExp == null) {
            return true;
        }
        for (int i = 0; i < credIdAndExp.length; i++) {
            log.info("checking credential {} from case {}", credIdAndExp[i].getId(), monitoredCase.getUuid());
            //check if the credential has not expired
            LocalDateTime expiresAt = LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(credIdAndExp[i].getExp())), ZoneId.systemDefault());
            //Date expiresAt = Date.from(Instant.ofEpochSecond(Long.parseLong(credIdAndExp[i].getExp())));
            log.info("credential expires at {}", expiresAt);
            if (!expiresAt.isAfter(currentDate)) {
                //if credentials have expired update case as suspended(paused)
                monitoredCase.setRejectionCode(RejectionCode.REJECTION0);
                monitoredCase.setDailyValue(BigDecimal.ZERO);
                monitoredCase.setDailySum(BigDecimal.ZERO);
                updateCase(monitoredCase, State.SUSPENDED, ssiApp, currentDate, isTest, monitoredCase.getUuid(), null, credChange, storeDataForSE);
                //rejectOrSuspendCases(uuid, State.SUSPENDED, householdApps, currentDate, null, null, isTest, credChange, storeDataForSE);
                credsOk = false;
                break;
            }
            //check if the credential is revoked
            boolean isRevoked = this.ethServ.checkRevocationStatus(credIdAndExp[i].getId());
            log.info("is credential {} revoked? == {}", credIdAndExp[i].getId(), isRevoked);
            if (isRevoked){
                monitoredCase.setRejectionDate(DateUtils.dateToString(currentDate.toLocalDate()));
                monitoredCase.setRejectionCode(RejectionCode.REJECTION107);
                monitoredCase.setDailyValue(BigDecimal.ZERO);
                monitoredCase.setDailySum(BigDecimal.ZERO);
                updateCase(monitoredCase, State.REJECTED, ssiApp, currentDate, isTest, monitoredCase.getUuid(), null, credChange, storeDataForSE);
                //rejectOrSuspendCases(uuid, State.REJECTED, householdApps, currentDate, null, RejectionCode.REJECTION2, isTest, credChange, storeDataForSE);
                credsOk = false;
                break;
            }
            credsOk = true;
        }

        return credsOk;
    }

    private Boolean checkForFailedPayments(Case monitoredCase) {
        List<CasePayment> failedPayments = monitoredCase.getPaymentHistory().stream().filter(s -> s.getState().equals(State.FAILED)).collect(Collectors.toList());
        int failedCount = 1;
        if (failedPayments.size() >= 3) {
            for (int i = 1; i < failedPayments.size(); i++) {
                if (failedPayments.get(i).getPaymentDate().getMonthValue() == failedPayments.get(i - 1).getPaymentDate().getMonthValue() + 1) {
                    failedCount++;
                    if (failedCount >= 3) {
                        log.info("rejected - payment failed for 3 or more months");
                        return true;
                    }
                } else {
                    failedCount = 1;
                }
            }
        }

        return false;
    }

    private Boolean checkHouseholdCredentials(Case monitoredCase, SsiApplication ssiApp, List<SsiApplication> householdApps, SsiApplication aggregatedSsiApp) {
        
        //check if there are more than one principal members
        Long principalCount = householdApps.stream().filter(h -> h.getHouseholdPrincipal().getAfm().equals(h.getTaxisAfm())).count();
        if (principalCount > 1) {
            log.info("rejected - more than one principal in household");
            monitoredCase.setRejectionCode(RejectionCode.REJECTION110);
            return false;
        }

        // Set<String> duplicateApps = new HashSet<>();
        // for(SsiApplication app:householdApps){
        //    if(!duplicateApps.add(app.getTaxisAfm())){
        //         log.info("rejected - duplicate applications in household for uuid :{}", app.getUuid());
        //         monitoredCase.setRejectionCode(RejectionCode.REJECTION110);
        //         return false;
        //    }
        // }

        //check if two months have passed while the application is in status suspended
        Iterator<Entry<LocalDateTime, State>> it = monitoredCase.getHistory().entrySet().iterator();
        LocalDate suspendStartDate = LocalDate.of(1900, 1, 1);
        LocalDate suspendEndDate = LocalDate.of(1900, 1, 1);
        while (it.hasNext()) {
            if (suspendEndDate.equals(suspendStartDate.plusMonths(2))) {
                log.info("rejected - application suspended for 2 months or more");
                monitoredCase.setRejectionCode(RejectionCode.REJECTION108);
                return false;
            }
            Map.Entry<LocalDateTime, State> entry = it.next();
            if (!entry.getValue().equals(State.SUSPENDED)) {
                suspendStartDate = LocalDate.of(1900, 1, 1);
                continue;
            }
            if (suspendStartDate.equals(LocalDate.of(1900, 1, 1))) {
                suspendStartDate = entry.getKey().toLocalDate();
            }
            suspendEndDate = entry.getKey().toLocalDate();
        }

        //economics check
        if (EthAppUtils.getTotalMonthlyValue(aggregatedSsiApp, null).compareTo(BigDecimal.ZERO) == 0) {
            log.info("rejected - financial data restriction (total household income > payment thresshold)");
            monitoredCase.setRejectionCode(RejectionCode.REJECTION101);
            return false;
        }

        //does an application exist in a different household
        for (SsiApplication app : householdApps) {
            if(mongoServ.findByTaxisAfm(app.getTaxisAfm()).size()>1){
                log.info("rejected - afm exists in different houshold");
                monitoredCase.setRejectionCode(RejectionCode.REJECTION109);
                return false;
            }
        }

        return true;
    }

    /**
     * @return the number of API calls that resulted in a change of value.
     * At maximum two API calls per run should change
     */
    private ExternalAPICallResult financialsCheck(SsiApplication principalApp,
                                                  double pValue, Boolean makeMockCheck,
                                                  List<SsiApplication> householdApps, Integer count, LocalDate currentDate, List<LocalDate> rejectionDates) {
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
                //updateCurrentFinancial(financialHistoryMap, "otherBnfts", updatedApp.get());
                updatedApp.get().setOtherBenefitsR(String.valueOf(otherBenefitsResult.get().getValue()));
                mongoServ.updateSsiApp(updatedApp.get());
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
                //updateCurrentFinancial(financialHistoryMap, "unemploymentBnft", updatedApp.get());
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
                updatedApp.get().setErgomRHistory(financialHistoryMap);
                //updateCurrentFinancial(financialHistoryMap, "ergome", updatedApp.get());
                updatedApp.get().setErgomeR(String.valueOf(ergomBenefitUpdate.get().getValue()));
                mongoServ.updateSsiApp(updatedApp.get());
            }
        }


        Optional<UpdateMockResult> salary = mockServ.getUpdateSalariesData(currentDate, currentDate, pValue, principalApp,
                makeMockCheck && count < 2, householdApps);
        if (salary.isPresent() && count < 2) {
            updatedCaseUUID = salary.get().getUuid();
            changed = true;
            count++;
            //principalApp.setSalariesR(String.valueOf(salary.get().getValue()));
            Optional<SsiApplication> updatedApp = mongoServ.findByUuid(updatedCaseUUID);
            financialHistoryMap = updatedApp.get().getSalariesRHistory();
            if (financialHistoryMap == null) {
                financialHistoryMap = new LinkedHashMap<>();
            }
            financialHistoryMap.put(DateUtils.dateToString(salary.get().getDate()), String.valueOf(salary.get().getValue()));
            if (updatedApp.isPresent()) {
                updatedApp.get().setSalariesRHistory(financialHistoryMap);
                //updateCurrentFinancial(financialHistoryMap, "salaries", updatedApp.get());
                updatedApp.get().setSalariesR(String.valueOf(salary.get().getValue()));
                mongoServ.updateSsiApp(updatedApp.get());
            }
        }


        Optional<UpdateMockResult> pensionResult = mockServ.getUpdatedPension(currentDate, currentDate, pValue, principalApp,
                makeMockCheck && count < 2, householdApps);
        if (pensionResult.isPresent() && count < 2) {
            updatedCaseUUID = pensionResult.get().getUuid();
            changed = true;
            count++;
           //principalApp.setSalariesR(String.valueOf(pensionResult.get().getValue()));
            Optional<SsiApplication> updatedApp = mongoServ.findByUuid(updatedCaseUUID);
            financialHistoryMap = updatedApp.get().getPensionsRHistory();
            if (financialHistoryMap == null) {
                financialHistoryMap = new LinkedHashMap<>();
            }
            financialHistoryMap.put(DateUtils.dateToString(pensionResult.get().getDate()), String.valueOf(pensionResult.get().getValue()));
            if (updatedApp.isPresent()) {
                updatedApp.get().setPensionsRHistory(financialHistoryMap);
                //updateCurrentFinancial(financialHistoryMap, "pension", updatedApp.get());
                updatedApp.get().setPensionsR(String.valueOf(pensionResult.get().getValue()));
                mongoServ.updateSsiApp(updatedApp.get());
            }
        }

        Optional<UpdateMockResult> freelanceUpdate = mockServ.getUpdatedFreelance(currentDate, currentDate, pValue, principalApp,
                makeMockCheck && count < 2, householdApps);
        if (freelanceUpdate.isPresent() && count < 2) {
            updatedCaseUUID = freelanceUpdate.get().getUuid();
            changed = true;
            count++;
            //principalApp.setSalariesR(String.valueOf(freelanceUpdate.get().getValue()));
            Optional<SsiApplication> updatedApp = mongoServ.findByUuid(updatedCaseUUID);
            financialHistoryMap = updatedApp.get().getSalariesRHistory();
            if (financialHistoryMap == null) {
                financialHistoryMap = new LinkedHashMap<>();
            }
            financialHistoryMap.put(DateUtils.dateToString(freelanceUpdate.get().getDate()), String.valueOf(freelanceUpdate.get().getValue()));
            if (updatedApp.isPresent()) {
                updatedApp.get().setFreelanceRHistory(financialHistoryMap);
                //updateCurrentFinancial(financialHistoryMap, "freelance", updatedApp.get());
                updatedApp.get().setFreelanceR(String.valueOf(freelanceUpdate.get().getValue()));
                mongoServ.updateSsiApp(updatedApp.get());
            }
        }

        Optional<UpdateMockResult> depositsUpdates = mockServ.getUpdatedDepoists(currentDate, currentDate, pValue, principalApp,
                makeMockCheck && count < 2, householdApps);
        if (depositsUpdates.isPresent() && count < 2) {
            updatedCaseUUID = depositsUpdates.get().getUuid();
            changed = true;
            count++;
            //principalApp.setSalariesR(String.valueOf(depositsUpdates.get().getValue()));
            Optional<SsiApplication> updatedApp = mongoServ.findByUuid(updatedCaseUUID);
            financialHistoryMap = updatedApp.get().getDepositsAHistory();
            if (financialHistoryMap == null) {
                financialHistoryMap = new LinkedHashMap<>();
            }
            financialHistoryMap.put(DateUtils.dateToString(depositsUpdates.get().getDate()), String.valueOf(depositsUpdates.get().getValue()));
            if (updatedApp.isPresent()) {
                updatedApp.get().setDepositsAHistory(financialHistoryMap);
                //updateCurrentFinancial(financialHistoryMap, "deposits", updatedApp.get());
                updatedApp.get().setDepositsA(String.valueOf(depositsUpdates.get().getValue()));
                mongoServ.updateSsiApp(updatedApp.get());
            }
        }
        // if (changed) {
        //     mongoServ.updateSsiApp(principalApp);
        // }
        //if the financial checks fails now the application should be rejected

        ExternalAPICallResult result = new ExternalAPICallResult();
        result.setCount(count);
        result.setChanged(changed);
        return result;
    }


    private ExternalAPICallResult deceasedCheck(SsiApplication principalApp,
                                                double pValue, Boolean makeMockCheck,
                                                List<SsiApplication> householdApps, Integer count, LocalDate currentDate, List<LocalDate> rejectionDates) {
        //mock check
        Boolean changed = false;
        //LinkedHashMap<String, List<HouseholdMember>> householdCompositionHistory;
        Optional<BooleanMockResult> deceasedResult = mockServ.getDeaths(currentDate, currentDate, pValue,
                principalApp, makeMockCheck && count < 2, householdApps);
        ExternalAPICallResult result = new ExternalAPICallResult();
        if (deceasedResult.isPresent() && count < 2) {
            count++;
            changed = true;
            //update the list of household members
            LinkedHashMap<String, List<HouseholdMember>> householdHistory = principalApp.getHouseholdCompositionHistory();
            List<HouseholdMember> newHoushold = principalApp.getHouseholdComposition().stream().
                    filter(member -> !member.getAfm().equals(deceasedResult.get().getData())).collect(Collectors.toList());
            householdHistory.put(DateUtils.dateToString(deceasedResult.get().getDate()), newHoushold);
            principalApp.setHouseholdCompositionHistory(householdHistory);
            principalApp.setHouseholdComposition(newHoushold);
            mongoServ.updateSsiApp(principalApp);
            result.setDate(deceasedResult.get().getDate());
        }
        result.setCount(count);
        result.setChanged(changed);
        return result;
    }


    //mock oaed registration check
    private ExternalAPICallResult oaedRegistrationCheck(SsiApplication principalApp,
                                                        double pValue, Boolean makeMockCheck,
                                                        List<SsiApplication> householdApps, Integer count, LocalDate currentDate, List<LocalDate> rejectionDates) {
        //mock check
        ExternalAPICallResult result = new ExternalAPICallResult();
        Optional<BooleanMockResult> oaedRegistrationResult = mockServ.getOAEDRegistration(currentDate, currentDate, pValue,
                principalApp, makeMockCheck && count < 2, householdApps);
        if (oaedRegistrationResult.isPresent() && count < 2) {
            String updatedCaseUUID = oaedRegistrationResult.get().getUuid();
            count++;
            //TODO the date should be today
            // rejectOrSuspendCases(updatedCaseUUID, State.REJECTED,
            //         householdApps, oaedRegistrationResult.get().getDate().atStartOfDay(), false);
            result.setDate(oaedRegistrationResult.get().getDate());
        }
        result.setCount(count);
        return result;
    }

    //mock oaed registration check
    private ExternalAPICallResult luxuryCheck(SsiApplication principalApp,
                            double pValue, Boolean makeMockCheck,
                            List<SsiApplication> householdApps, Integer count, LocalDate currentDate, List<LocalDate> rejectionDates) {
        //mock check
        ExternalAPICallResult result = new ExternalAPICallResult();
        Optional<BooleanMockResult> luxuryCheckResult = mockServ.getLuxury(currentDate, currentDate, pValue,
                principalApp, makeMockCheck && count < 2, householdApps);
        if (luxuryCheckResult.isPresent() && count < 2) {
            String updatedCaseUUID = luxuryCheckResult.get().getUuid();
            count++;
            // rejectOrSuspendCases(updatedCaseUUID, State.REJECTED,
            //         householdApps, luxuryCheckResult.get().getDate().atStartOfDay(), false);
            result.setDate(luxuryCheckResult.get().getDate());
        }
        result.setCount(count);
        return result;
    }


    // external api calls
    // calls all extrnal APIs
    private ExternalChecksResult externalChecksAndUpdate(Case monitoredCase, double pValue, Boolean makeMockCheck,
                                            List<SsiApplication> householdApps, SsiApplication principalApp,
                                            Integer count, LocalDate currentDate) {

        List<LocalDate> rejectionDates = new ArrayList<>();
        //validate each household application credentials

        ExternalChecksResult finalResult = new ExternalChecksResult();
        // calls external APIs and updates DB
        ExternalAPICallResult result = financialsCheck(principalApp, pValue, makeMockCheck, householdApps, count, currentDate, rejectionDates);
        Integer apiCallsUpdates = result.getCount();

        finalResult.setChangedFinancials(result.getChanged());
        if(result.getDate() != null) rejectionDates.add(result.getDate().toLocalDate());
        if (apiCallsUpdates < 2) {
            //handle deceased member
            result = deceasedCheck(principalApp, pValue, makeMockCheck, householdApps, count, currentDate, rejectionDates);
            if(!finalResult.getChangedFinancials()){
                finalResult.setChangedFinancials(result.getChanged());
            }
            apiCallsUpdates = result.getCount() > 0? apiCallsUpdates + result.getCount() : apiCallsUpdates;
            //if(result.getDate() != null ) finalResult.setDate(result.getDate().toLocalDate());  //rejectionDates.add(result.getDate().toLocalDate());
            if (apiCallsUpdates < 2) {
                result = oaedRegistrationCheck(principalApp, pValue, makeMockCheck, householdApps, count, currentDate, rejectionDates);
                apiCallsUpdates = result.getCount() > 0? apiCallsUpdates + result.getCount() : apiCallsUpdates;
                if(result.getDate() != null && (finalResult.getDate() == null || result.getDate().toLocalDate().isBefore(finalResult.getDate()))) {
                    finalResult.setDate(result.getDate().toLocalDate());
                    finalResult.setRejection(true);
                    finalResult.setRejectionCode(RejectionCode.REJECTION102);
                    //rejectionDates.add(result.getDate().toLocalDate());
                }  
                if (apiCallsUpdates < 2) {
                    result = luxuryCheck(principalApp, pValue, makeMockCheck, householdApps, count, currentDate, rejectionDates);
                    apiCallsUpdates = result.getCount() > 0? apiCallsUpdates + result.getCount() : apiCallsUpdates;
                    //if(result.getDate() != null) rejectionDates.add(result.getDate().toLocalDate());
                    if(result.getDate() != null && (finalResult.getDate() == null || result.getDate().toLocalDate().isBefore(finalResult.getDate()))) {
                        finalResult.setDate(result.getDate().toLocalDate());
                        finalResult.setRejection(true);
                        finalResult.setRejectionCode(RejectionCode.REJECTION105);
                        
                        //rejectionDates.add(result.getDate().toLocalDate());
                    }  

                }
            }
        }

        if(finalResult.getRejection() != null && finalResult.getRejection().equals(Boolean.TRUE)){
            if (finalResult.getDate() != null && (monitoredCase.getRejectionDate().equals("") || finalResult.getDate().isBefore(DateUtils.dateStringToLD(monitoredCase.getRejectionDate())))) {
                monitoredCase.setRejectionDate(DateUtils.dateToString(finalResult.getDate()));
            } else {
                finalResult.setDate(DateUtils.dateStringToLD(monitoredCase.getRejectionDate()));
                finalResult.setRejectionCode(monitoredCase.getRejectionCode());
            }
        } else {
            finalResult.setRejection(false);
            finalResult.setRejectionCode(RejectionCode.REJECTION0);
        }
        
        finalResult.setCount(apiCallsUpdates);
        //ExternalChecksResult finalResult = new ExternalChecksResult();
        // finalResult.setRejection(false);
        // finalResult.setCount(apiCallsUpdates);
        // //finalResult.setChangedFinancials(result.getChanged());
        // if (!rejectionDates.isEmpty()) {
        //     LocalDate minDate = rejectionDates.stream()
        //             .min(Comparator.naturalOrder())
        //             .get();
        //     if (monitoredCase.getRejectionDate().equals("") || DateUtils.dateStringToLD(monitoredCase.getRejectionDate()).isAfter(minDate)) {
        //         monitoredCase.setRejectionDate(DateUtils.dateToString(minDate));
        //     }
        //     finalResult.setRejection(true);
        //     finalResult.setDate(minDate);
        // }

        return finalResult;

    }

}
