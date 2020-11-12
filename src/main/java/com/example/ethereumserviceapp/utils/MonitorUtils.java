/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.CasePayment;
import com.example.ethereumserviceapp.model.HouseholdMember;
import com.example.ethereumserviceapp.model.PaymentCredential;
import com.example.ethereumserviceapp.model.State;
import com.example.ethereumserviceapp.model.entities.SsiApplication;
import com.example.ethereumserviceapp.service.MongoService;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author nikos
 */
@Slf4j
//@Service
public class MonitorUtils extends EthAppUtils{
//mock checks replace with correct ones

    private static MongoService mongoServ;

    @Autowired
    public MonitorUtils(MongoService mongoS) {
        MonitorUtils.mongoServ = mongoS;
    }

    public static Boolean isApplicationAccepted(SsiApplication ssiApp) {
        try {
            String employmentStatus = ssiApp.getEmploymentStatus();
            String hospitalized = ssiApp.getHospitalized();
            Long totalIncome = Long.valueOf(ssiApp.getTotalIncome());
            ssiApp.getTaxisFamilyName();
            ssiApp.getTaxisFirstName();

            if (employmentStatus.equals("unemployed") && totalIncome < Long.valueOf(10000) && hospitalized.equals("true")) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }
                
    public static Boolean isCaseOlderThanSixMonths(LocalDateTime firstAcceptedDate){
        if(LocalDateTime.now().isAfter(firstAcceptedDate.plusMonths(6))){
            return true;
        }
        return false;
    }

    //mock method, fill up when more information is available
    public static Boolean checkExternalSources(){
        return true;
    }

    public static Boolean hasGreenCard(String uuid){
        return true;
    }

    // public static void calculateOffset(Case monitoredCase, SsiApplication ssiAppTest, List<SsiApplication> householdApps){

    //     List<PaymentCredential> changedCredentials = alteredCredentialsList(ssiAppTest);

    //     if(!changedCredentials.isEmpty()){
    //         //sort the list of altered credentials by date
    //         List<PaymentCredential> changedCredentialsSorted = changedCredentials.stream().sorted(Comparator.comparing(PaymentCredential::getDate)).collect(Collectors.toList());

    //         //create a map of altered credentials grouped by month, with key the start date of the month and value the credentials that were altered during this month
    //         Map<LocalDate, List<PaymentCredential>> monthlyGroupMap = changedCredentialsSorted.stream().collect(Collectors.groupingBy(e -> e.getDate().withDayOfMonth(1).toLocalDate()));
    //         List<CasePayment> paymentHistory = monitoredCase.getPaymentHistory().stream().sorted(Comparator.comparing(CasePayment::getPaymentDate)).collect(Collectors.toList());
            
    //         monthlyGroupMap.entrySet().forEach(m -> {
    //             if(m.getKey().compareTo(paymentHistory.get(0).getPaymentDate().toLocalDate()) <= 0){
    //                 m.getValue().forEach(l ->{
    //                     householdApps.stream().filter(h -> l.getAfm().equals(h.getTaxisAfm())).collect(Collectors.toList()).get(0);
    //                     updateSsiApplication(l.getName(), l.getValue(), ssiAppTest);   
    //                 });
    //             }
    //         });

    //         paymentHistory.forEach(ph -> {
    //             LocalDate startOfMonth = ph.getPaymentDate().minusMonths(1).withDayOfMonth(1).toLocalDate();
    //             LocalDate endOfMonth = ph.getPaymentDate().minusMonths(1).withDayOfMonth(monthDays(ph.getPaymentDate().minusMonths(1).toLocalDate())).toLocalDate();
    //             Integer fullMonthDays = monthDays(startOfMonth);

    //             if(monthlyGroupMap.get(startOfMonth) == null && startOfMonth.isAfter(monthlyGroupMap.entrySet().iterator().next().getValue().get(0).getDate().toLocalDate())){
                    
    //                 Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
    //                     e -> e.getKey().toLocalDate().compareTo(startOfMonth) >= 0 && e.getKey().toLocalDate().compareTo(endOfMonth) <=0 && e.getValue().equals(State.ACCEPTED)).count();
                    
    //                 BigDecimal offsetPayment = calculatePayment(fullMonthDays, offsetDays.intValue(), ssiAppTest);
    //                 BigDecimal monthlyOffset = ph.getPayment().subtract(offsetPayment);

    //                 monitoredCase.setOffset(monitoredCase.getOffset().add(monthlyOffset));
    //             }

    //             if(monthlyGroupMap.get(startOfMonth)!= null){
    //                 Long nonOffsetDays = monitoredCase.getHistory().entrySet().stream().filter(
    //                         e -> e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(0).getDate().withDayOfMonth(1).toLocalDate()) >= 0 && e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(0).getDate().toLocalDate()) <0 && e.getValue().equals(State.ACCEPTED)).count();
                        
    //                 CasePayment payment = monitoredCase.getPaymentHistory().stream()
    //                     .filter(p -> p.getPaymentDate().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(0).getDate().withDayOfMonth(1).toLocalDate()) >= 0
    //                     && p.getPaymentDate().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(0).getDate().withDayOfMonth(monthDays(monthlyGroupMap.get(startOfMonth).get(0).getDate().toLocalDate())).toLocalDate()) <= 0)
    //                     .collect(Collectors.toList()).get(0);

    //                 BigDecimal correctedPayment = (payment.getPayment()
    //                     .divide(BigDecimal.valueOf(monthDays(payment.getPaymentDate().toLocalDate())), 2, RoundingMode.HALF_UP))
    //                     .multiply(BigDecimal.valueOf(nonOffsetDays));
                    
    //                 for(int i = 0; i< monthlyGroupMap.get(startOfMonth).size(); i++){
                        
    //                     if( i+1 <  monthlyGroupMap.get(startOfMonth).size() ) {
    //                         final int innerI = i;
    //                         Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
    //                             e -> e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().toLocalDate()) >= 0 && e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI+1).getDate().toLocalDate()) <0 && e.getValue().equals(State.ACCEPTED)).count();
                            
    //                         updateSsiApplication(monthlyGroupMap.get(startOfMonth).get(i).getName(), monthlyGroupMap.get(startOfMonth).get(i).getValue(), ssiAppTest);   
    //                         BigDecimal offsetPayment = calculatePayment(fullMonthDays, offsetDays.intValue(), ssiAppTest);
    //                         correctedPayment = correctedPayment.add(offsetPayment);
    //                     } else {
    //                         final int innerI = i;
    //                         Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
    //                             e -> e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().toLocalDate()) >= 0 && e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().withDayOfMonth(monthDays(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().toLocalDate())).toLocalDate()) <=0 && e.getValue().equals(State.ACCEPTED)).count();
                            
    //                         updateSsiApplication(monthlyGroupMap.get(startOfMonth).get(i).getName(), monthlyGroupMap.get(startOfMonth).get(i).getValue(), ssiAppTest);   
                            
    //                         BigDecimal offsetPayment = calculatePayment(fullMonthDays, offsetDays.intValue(), ssiAppTest);
    //                         correctedPayment = correctedPayment.add(offsetPayment);
    //                     }
    //                 }

    //                 BigDecimal monthlyOffset = ph.getPayment().subtract(correctedPayment);

    //                 monitoredCase.setOffset(monitoredCase.getOffset().add(monthlyOffset));

    //             }
    //         });
    //     }
    // }

    // public static BigDecimal calculateCurrentPayment(Case monitoredCase, SsiApplication ssiAppTest){
        
    //     LocalDate startOfPayment = LocalDate.now().minusMonths(1).withDayOfMonth(1);
    //     Integer monthDays = monthDays(startOfPayment);
    //     LocalDate endOfPayment = LocalDate.now().minusMonths(1).withDayOfMonth(monthDays);
    //     Long acceptedDays = monitoredCase.getHistory().entrySet().stream().filter(
    //                     e -> e.getKey().toLocalDate().compareTo(startOfPayment) >= 0 && e.getKey().toLocalDate().compareTo(endOfPayment) <=0 && e.getValue().equals(State.ACCEPTED)).count();
                    
    //     BigDecimal projectedPayment = calculatePayment(monthDays, acceptedDays.intValue(), ssiAppTest);

    //     List<PaymentCredential> changedCredentials = alteredCredentialsList(ssiAppTest);
        
    //     BigDecimal correctedPayment = BigDecimal.ZERO;

    //     if(changedCredentials.isEmpty() || !changedCredentials.stream().anyMatch(c -> c.getDate().withDayOfMonth(1).toLocalDate().equals(startOfPayment))){
    //         return projectedPayment;
    //     }
    //     //sort the list of altered credentials by date
    //     List<PaymentCredential> changedCredentialsSorted = changedCredentials.stream().sorted(Comparator.comparing(PaymentCredential::getDate)).collect(Collectors.toList());
    //     //create a map of altered credentials grouped by month, with key the start date of the month and value the credentials that were altered during this month
    //     Map<LocalDate, List<PaymentCredential>> monthlyGroupMap = changedCredentialsSorted.stream().collect(Collectors.groupingBy(e -> e.getDate().withDayOfMonth(1).toLocalDate()));
        
    //     List<CasePayment> paymentHistory = monitoredCase.getPaymentHistory().stream().sorted(Comparator.comparing(CasePayment::getPaymentDate)).collect(Collectors.toList());
        
    //     CasePayment currentPayment = new CasePayment();
    //     currentPayment.setPaymentDate(LocalDateTime.now());
    //     currentPayment.setPayment(projectedPayment);
    //     paymentHistory.add(currentPayment);

    //     monthlyGroupMap.entrySet().forEach(m -> {
    //         if(m.getKey().compareTo(paymentHistory.get(0).getPaymentDate().toLocalDate()) <= 0){
    //             m.getValue().forEach(l ->{
    //                 updateSsiApplication(l.getName(), l.getValue(), ssiAppTest);   
    //             });
    //         }
    //     });

    //     for(CasePayment payment : paymentHistory){
    //         LocalDate startOfMonth = payment.getPaymentDate().minusMonths(1).withDayOfMonth(1).toLocalDate();
    //         Integer fullMonthDays = monthDays(startOfMonth);

    //         if(monthlyGroupMap.get(startOfMonth)!= null){
    //             if(!startOfMonth.isEqual(startOfPayment)){
    //                 continue;   
    //             }
    //             Long nonOffsetDays = monitoredCase.getHistory().entrySet().stream().filter(
    //                     e -> e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(0).getDate().withDayOfMonth(1).toLocalDate()) >= 0 && e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(0).getDate().toLocalDate()) <0 && e.getValue().equals(State.ACCEPTED)).count();
                    
    //             correctedPayment = calculatePayment(fullMonthDays, nonOffsetDays.intValue(), ssiAppTest);
    //             for(int i = 0; i< monthlyGroupMap.get(startOfMonth).size(); i++){
    //                 if( i+1 <  monthlyGroupMap.get(startOfMonth).size() ) {
    //                     if(!startOfMonth.isEqual(startOfPayment)){
    //                         updateSsiApplication(monthlyGroupMap.get(startOfMonth).get(i).getName(), monthlyGroupMap.get(startOfMonth).get(i).getValue(), ssiAppTest);
    //                         continue;   
    //                     }
                    
    //                     final int innerI = i;
    //                     Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
    //                         e -> e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().toLocalDate()) >= 0 && e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI+1).getDate().toLocalDate()) <0 && e.getValue().equals(State.ACCEPTED)).count();
                        
    //                     updateSsiApplication(monthlyGroupMap.get(startOfMonth).get(i).getName(), monthlyGroupMap.get(startOfMonth).get(i).getValue(), ssiAppTest);   
    //                     BigDecimal offsetPayment = calculatePayment(fullMonthDays, offsetDays.intValue(), ssiAppTest);
    //                     correctedPayment = correctedPayment.add(offsetPayment);
    //                 } else {
    //                     if(!startOfMonth.isEqual(startOfPayment)){
    //                         updateSsiApplication(monthlyGroupMap.get(startOfMonth).get(i).getName(), monthlyGroupMap.get(startOfMonth).get(i).getValue(), ssiAppTest); 
    //                         continue;   
    //                     }
    //                     final int innerI = i;
    //                     Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
    //                         e -> e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().toLocalDate()) >= 0 && e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().withDayOfMonth(monthDays(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().toLocalDate())).toLocalDate()) <=0 && e.getValue().equals(State.ACCEPTED)).count();
                        
    //                     updateSsiApplication(monthlyGroupMap.get(startOfMonth).get(i).getName(), monthlyGroupMap.get(startOfMonth).get(i).getValue(), ssiAppTest);   
                        
    //                     BigDecimal offsetPayment = calculatePayment(fullMonthDays, offsetDays.intValue(), ssiAppTest);
    //                     correctedPayment = correctedPayment.add(offsetPayment);
    //                 }
    //             }
    //         }
    //     }

    //     return correctedPayment;
    // }

    // private static List<PaymentCredential> alteredCredentialsList(SsiApplication ssiAppTest){

    //     List<PaymentCredential> changedCredentials = new ArrayList<>();

    //     Map<String, LinkedHashMap<LocalDateTime, String>> credHistoriesMap = new HashMap<>();
    //     if(ssiAppTest.getPensionsRHistory()!=null){
    //         credHistoriesMap.put("pension", ssiAppTest.getPensionsRHistory());
    //     }
    //     if(ssiAppTest.getSalariesRHistory()!=null){
    //         credHistoriesMap.put("salaries", ssiAppTest.getSalariesRHistory());
    //     }
    //     if(ssiAppTest.getOtherBenefitsRHistory()!=null){
    //         credHistoriesMap.put("otherBnfts", ssiAppTest.getOtherBenefitsRHistory());
    //     }
    //     if(ssiAppTest.getFreelanceRHistory()!=null){
    //         credHistoriesMap.put("freelance", ssiAppTest.getFreelanceRHistory());
    //     }
    //     if(ssiAppTest.getDepositsAHistory()!=null){
    //         credHistoriesMap.put("deposits", ssiAppTest.getDepositsAHistory());
    //     }
    //     if(ssiAppTest.getDomesticRealEstateAHistory()!=null){
    //         credHistoriesMap.put("domesticRE", ssiAppTest.getDomesticRealEstateAHistory());
    //     }
    //     if(ssiAppTest.getForeignRealEstateAHistory()!=null){
    //         credHistoriesMap.put("foreignRE", ssiAppTest.getForeignRealEstateAHistory());
    //     }

    //     //if there are altered credentials (credential history size > 1) set the credential date and value to the default (first value) and add it to the list of changed credentials
    //     if(!credHistoriesMap.isEmpty()){
    //         credHistoriesMap.entrySet().forEach(e -> {
    //             if(e.getValue().size()>1){
    //                 e.getValue().entrySet().stream().skip(1).forEach(p -> {
    //                     PaymentCredential credential = new PaymentCredential();
    //                     credential.setDate(p.getKey()); 
    //                     credential.setValue(p.getValue());
    //                     credential.setName(e.getKey());
    //                     changedCredentials.add(credential);
    //                 });
    //                 updateSsiApplication(e.getKey(), e.getValue().entrySet().iterator().next().getValue(), ssiAppTest);
    //             }
    //         });
    //     }

    //     return changedCredentials;
    // }

    // private static void updateSsiApplication(String name, String value, SsiApplication ssiAppTest){
    //     switch(name) {
    //         case "pension":
    //         ssiAppTest.setPensionsR(value);
    //         break;
    //         case "salaries":
    //         ssiAppTest.setSalariesR(value);
    //         break;
    //         case "otherBnfts":
    //         ssiAppTest.setOtherBenefitsR(value);
    //         break;
    //         case "freelance":
    //         ssiAppTest.setFreelanceR(value);
    //         break;
    //         case "deposits":
    //         ssiAppTest.setDepositsA(value);
    //         break;
    //         case "domesticRE":
    //         ssiAppTest.setDomesticRealEstateA(value);
    //         break;
    //         case "foreignRE":
    //         ssiAppTest.setForeignRealEstateA(value);
    //         break;
    //     }
    // }
    
    private static List<PaymentCredential> alteredCredentialsList3(SsiApplication ssiAppTest, List<SsiApplication> householdApps){

        List<PaymentCredential> changedCredentials = new ArrayList<>();

        if(ssiAppTest.getHouseholdCompositionHistory()!=null){
            ssiAppTest.getHouseholdCompositionHistory().entrySet().stream().skip(1).forEach(p -> {
                PaymentCredential credential = new PaymentCredential();
                credential.setDate(p.getKey()); 
                //credential.setValue(p.getValue());
                credential.setName("household");
                credential.setHousehold(p.getValue());
                changedCredentials.add(credential);
            });
            updateSsiApplication2("household", null, ssiAppTest, ssiAppTest.getHouseholdCompositionHistory().entrySet().iterator().next().getValue());
        }

        householdApps.forEach(h -> {
            Map<String, LinkedHashMap<LocalDateTime, String>> credHistoriesMap = new HashMap<>();
            if(h.getPensionsRHistory()!=null){
                credHistoriesMap.put("pension", h.getPensionsRHistory());
            }
            if(h.getSalariesRHistory()!=null){
                credHistoriesMap.put("salaries", h.getSalariesRHistory());
            }
            if(h.getOtherBenefitsRHistory()!=null){
                credHistoriesMap.put("otherBnfts", h.getOtherBenefitsRHistory());
            }
            if(h.getFreelanceRHistory()!=null){
                credHistoriesMap.put("freelance", h.getFreelanceRHistory());
            }
            if(h.getDepositsAHistory()!=null){
                credHistoriesMap.put("deposits", h.getDepositsAHistory());
            }
            if(h.getDomesticRealEstateAHistory()!=null){
                credHistoriesMap.put("domesticRE", h.getDomesticRealEstateAHistory());
            }
            if(h.getForeignRealEstateAHistory()!=null){
                credHistoriesMap.put("foreignRE", h.getForeignRealEstateAHistory());
            }

            //if there are altered credentials (credential history size > 1) set the credential date and value to the default (first value) and add it to the list of changed credentials
            if(!credHistoriesMap.isEmpty()){
                credHistoriesMap.entrySet().forEach(e -> {
                    if(e.getValue().size()>1){
                        e.getValue().entrySet().stream().skip(1).forEach(p -> {
                            PaymentCredential credential = new PaymentCredential();
                            credential.setDate(p.getKey()); 
                            credential.setValue(p.getValue());
                            credential.setName(e.getKey());
                            credential.setAfm(h.getTaxisAfm());
                            changedCredentials.add(credential);
                        });
                        updateSsiApplication2(e.getKey(), e.getValue().entrySet().iterator().next().getValue(), h, null);
                    }
                });
            }
        });

        

        List<String> othrBnfts = householdApps.stream().map(h -> h.getOtherBenefitsR()).collect(Collectors.toList());
        List<String> pensions = householdApps.stream().map(h -> h.getPensionsR()).collect(Collectors.toList());
        log.info("ggggggggggggggggggggggggg household before any aggregation apps benefits:{}, pensions :{}", othrBnfts, pensions);
        log.info("hhhhhhhhhhhhhhhhhhhhhhhhh ssiapp before aggregate :{}", ssiAppTest);
        //ssiAppTest = aggregateHouseholdValues(householdApps);
        ssiAppTest = filterHHAndAggregate(householdApps, ssiAppTest.getHouseholdComposition());
        List<String> othrBnftsA = householdApps.stream().map(h -> h.getOtherBenefitsR()).collect(Collectors.toList());
        List<String> pensionsA = householdApps.stream().map(h -> h.getPensionsR()).collect(Collectors.toList());
        log.info("ggggggggggggggggggggggggg household after aggregation apps benefits:{}, pensions :{}", othrBnftsA, pensionsA);
        log.info("hhhhhhhhhhhhhhhhhhhhhhhhh ssiapp after aggregate :{}", ssiAppTest);

        return changedCredentials;
    }

    private static List<SsiApplication> reCalculateHousehold(List<SsiApplication> householdApps, SsiApplication ssiApp ){
        List<String> newHouseholdAfms = ssiApp.getHouseholdComposition().stream().map(h -> h.getAfm()).collect(Collectors.toList());
        //householdApps = householdApps.stream().filter(h -> newHouseholdAfms.contains(h.getTaxisAfm())).collect(Collectors.toList());
        List<SsiApplication> alteredHousehold = new ArrayList<>();
        for(SsiApplication app:householdApps){
            if(newHouseholdAfms.contains(app.getTaxisAfm())){
                app.setHouseholdComposition(ssiApp.getHouseholdComposition());
                alteredHousehold.add(app);
            }
        }
        return alteredHousehold;
    }

    private static List<SsiApplication> reCalculateHousehold2(List<SsiApplication> householdApps, List<SsiApplication> allHouseholdApps , SsiApplication ssiApp ){
        List<String> newHouseholdAfms = ssiApp.getHouseholdComposition().stream().map(h -> h.getAfm()).collect(Collectors.toList());
        //householdApps = householdApps.stream().filter(h -> newHouseholdAfms.contains(h.getTaxisAfm())).collect(Collectors.toList());
        // helper list contains all the applications of the current iteration of the household but with the latest data(if a new member was introduced to the household the housholdApps list may not contain it)
        List<SsiApplication> helperList = allHouseholdApps.stream().filter(h -> newHouseholdAfms.contains(h.getTaxisAfm())).collect(Collectors.toList());
        List<SsiApplication> alteredHousehold = new ArrayList<>();
        for(SsiApplication app:householdApps){
            if(newHouseholdAfms.contains(app.getTaxisAfm())){
                app.setHouseholdComposition(ssiApp.getHouseholdComposition());
                alteredHousehold.add(app);
            }
        }
        List<String> alteredAfms = alteredHousehold.stream().map(h -> h.getTaxisAfm()).collect(Collectors.toList());
        //if an afm appears in the helperList but not on the altetedHousehold it means that a member was added in the household and should be added to the new alteredList
        for(SsiApplication app:helperList){
            if(!alteredAfms.contains(app.getTaxisAfm())){
                app.setHouseholdComposition(ssiApp.getHouseholdComposition());
                alteredHousehold.add(app);
            }
        }
        return alteredHousehold;
    }

    private static void reCalculateHousehold3(List<SsiApplication> householdApps, SsiApplication ssiApp ){
        List<String> newHouseholdAfms = ssiApp.getHouseholdComposition().stream().map(h -> h.getAfm()).collect(Collectors.toList());
        //householdApps = householdApps.stream().filter(h -> newHouseholdAfms.contains(h.getTaxisAfm())).collect(Collectors.toList());
        List<SsiApplication> alteredHousehold = new ArrayList<>();
        for(SsiApplication app:householdApps){
            if(newHouseholdAfms.contains(app.getTaxisAfm())){
                app.setHouseholdComposition(ssiApp.getHouseholdComposition());
            }
        }
    }

    private static void updateSsiApplication2(String name, String value, SsiApplication ssiAppTest, List<HouseholdMember> household ){
        switch(name) {
            case "pension":
            ssiAppTest.setPensionsR(value);
            break;
            case "salaries":
            ssiAppTest.setSalariesR(value);
            break;
            case "otherBnfts":
            ssiAppTest.setOtherBenefitsR(value);
            break;
            case "freelance":
            ssiAppTest.setFreelanceR(value);
            break;
            case "deposits":
            ssiAppTest.setDepositsA(value);
            break;
            case "domesticRE":
            ssiAppTest.setDomesticRealEstateA(value);
            break;
            case "foreignRE":
            ssiAppTest.setForeignRealEstateA(value);
            break;
            case "household" :
            ssiAppTest.setHouseholdComposition(household);
            break;
        }
    }


    // private static List<PaymentCredential> alteredCredentialsList2(SsiApplication ssiAppTest, List<SsiApplication> householdApps){

    //     List<PaymentCredential> changedCredentials = new ArrayList<>();

    //     householdApps.forEach(h -> {
    //         Map<String, LinkedHashMap<LocalDateTime, String>> credHistoriesMap = new HashMap<>();
    //         if(h.getPensionsRHistory()!=null){
    //             credHistoriesMap.put("pension", h.getPensionsRHistory());
    //         }
    //         if(h.getSalariesRHistory()!=null){
    //             credHistoriesMap.put("salaries", h.getSalariesRHistory());
    //         }
    //         if(h.getOtherBenefitsRHistory()!=null){
    //             credHistoriesMap.put("otherBnfts", h.getOtherBenefitsRHistory());
    //         }
    //         if(h.getFreelanceRHistory()!=null){
    //             credHistoriesMap.put("freelance", h.getFreelanceRHistory());
    //         }
    //         if(h.getDepositsAHistory()!=null){
    //             credHistoriesMap.put("deposits", h.getDepositsAHistory());
    //         }
    //         if(h.getDomesticRealEstateAHistory()!=null){
    //             credHistoriesMap.put("domesticRE", h.getDomesticRealEstateAHistory());
    //         }
    //         if(h.getForeignRealEstateAHistory()!=null){
    //             credHistoriesMap.put("foreignRE", h.getForeignRealEstateAHistory());
    //         }

    //         //if there are altered credentials (credential history size > 1) set the credential date and value to the default (first value) and add it to the list of changed credentials
    //         if(!credHistoriesMap.isEmpty()){
    //             credHistoriesMap.entrySet().forEach(e -> {
    //                 if(e.getValue().size()>1){
    //                     e.getValue().entrySet().stream().skip(1).forEach(p -> {
    //                         PaymentCredential credential = new PaymentCredential();
    //                         credential.setDate(p.getKey()); 
    //                         credential.setValue(p.getValue());
    //                         credential.setName(e.getKey());
    //                         credential.setAfm(h.getTaxisAfm());
    //                         changedCredentials.add(credential);
    //                     });
    //                     updateSsiApplication(e.getKey(), e.getValue().entrySet().iterator().next().getValue(), h);
    //                 }
    //             });
    //         }
    //     });

    //     ssiAppTest = aggregateHouseholdValues(householdApps);

    //     return changedCredentials;
    // }

    public static void calculateOffset2(Case monitoredCase, SsiApplication ssiAppTest, List<SsiApplication> householdApps){

        Set<String> allAfms = new HashSet<>();
        //List<SsiApplication> householdApps = new ArrayList<>();
        for (Map.Entry<LocalDateTime, List<HouseholdMember>> app : ssiAppTest.getHouseholdCompositionHistory().entrySet()) {
            for(HouseholdMember member : app.getValue()){
                allAfms.add(member.getAfm());
            }
        }

        // List<SsiApplication> allHouseholdApps = mongoServ.findByTaxisAfmIn(allAfms);
        // List<SsiApplication> householdApps = new ArrayList<>();
        // List<String> startAfms = ssiAppTest.getHouseholdCompositionHistory().entrySet().iterator().next().getValue().stream().map(m -> m.getAfm()).collect(Collectors.toList());
        
        // householdApps = allHouseholdApps.stream().filter(h -> startAfms.contains(h.getTaxisAfm())).collect(Collectors.toList());

        List<PaymentCredential> changedCredentials = alteredCredentialsList3(ssiAppTest, householdApps);

        if(changedCredentials.isEmpty()){
            return;
        }
        //sort the list of altered credentials by date
        List<PaymentCredential> changedCredentialsSorted = changedCredentials.stream().sorted(Comparator.comparing(PaymentCredential::getDate)).collect(Collectors.toList());
        log.info("qqqqqqqqqqqqqqqqqqqqq altered credentials sorted :{}", changedCredentialsSorted);
        //create a map of altered credentials grouped by month, with key the start date of the month and value the credentials that were altered during this month
        Map<LocalDate, List<PaymentCredential>> monthlyGroupMap = changedCredentialsSorted.stream().collect(Collectors.groupingBy(e -> e.getDate().withDayOfMonth(1).toLocalDate()));
        log.info("wwwwwwwwwwwwwwwwwwwwwwwwwwww monthlyGroupMap :{}", monthlyGroupMap);
        List<CasePayment> paymentHistory = monitoredCase.getPaymentHistory().stream().sorted(Comparator.comparing(CasePayment::getPaymentDate)).collect(Collectors.toList());
        Boolean credBeforeAppStart = false;
        LocalDate firstAcceptedDate = LocalDate.of(2000, 1, 1);
        //find the first date that the case was accepted
        for (Map.Entry<LocalDateTime,State> mCase : monitoredCase.getHistory().entrySet()) {
            if(mCase.getValue().equals(State.ACCEPTED)){
                firstAcceptedDate = mCase.getKey().toLocalDate();
                break;
            }
        }
        //check if any credential has been altered at a date before the start of the application and use that value as the base one
        for (Map.Entry<LocalDate, List<PaymentCredential>> mCred : monthlyGroupMap.entrySet()) {
            if(mCred.getKey().compareTo(firstAcceptedDate) > 0){
                continue;
            }
            credBeforeAppStart = true;
            for(PaymentCredential credential: mCred.getValue()){
                //householdApps.stream().filter(h -> credential.getAfm().equals(h.getTaxisAfm())).collect(Collectors.toList()).get(0);
                if(credential.getName().equals("household")){
                    updateSsiApplication2(credential.getName(), null, ssiAppTest, credential.getHousehold());
                } else {
                    updateSsiApplication2(credential.getName(), credential.getValue(), householdApps.stream().filter(h -> credential.getAfm().equals(h.getTaxisAfm())).collect(Collectors.toList()).get(0), null);
                }
                //ssiAppTest = aggregateHouseholdValues(householdApps);
            }
        }
        if(credBeforeAppStart){
            //ssiAppTest = aggregateHouseholdValues(householdApps);
            ssiAppTest = filterHHAndAggregate(householdApps, ssiAppTest.getHouseholdComposition());
        }
        log.info("rrrrrrrrrrrrrrrrrrrrrrr sssiApp other benefits:{}, pensions :{}", ssiAppTest.getOtherBenefitsR(), ssiAppTest.getPensionsR());

        for(CasePayment ph:paymentHistory){
            
            log.info("111111111111111111111 start loop ph");
            
            LocalDate startOfMonth = ph.getPaymentDate().minusMonths(1).withDayOfMonth(1).toLocalDate();
            LocalDate endOfMonth = ph.getPaymentDate().minusMonths(1).withDayOfMonth(monthDays(ph.getPaymentDate().minusMonths(1).toLocalDate())).toLocalDate();
            Integer fullMonthDays = monthDays(startOfMonth);

            // if there is no credential change during this month then calculate the payment with the last credentials
            if(monthlyGroupMap.get(startOfMonth) == null && startOfMonth.isAfter(firstAcceptedDate)){
                
                Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                    e -> e.getKey().toLocalDate().compareTo(startOfMonth) >= 0 && e.getKey().toLocalDate().compareTo(endOfMonth) <=0 && e.getValue().equals(State.ACCEPTED)).count();
                
                BigDecimal offsetPayment = calculatePayment2(fullMonthDays, offsetDays.intValue(), ssiAppTest, startOfMonth);
                BigDecimal monthlyOffset = ph.getPayment().subtract(offsetPayment);

                monitoredCase.setOffset(monitoredCase.getOffset().add(monthlyOffset));
            }

            if(monthlyGroupMap.get(startOfMonth)!= null){
                //calculate the starting days of the month with a payment value of the lastly updated credentials
                Long nonOffsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                        e -> e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(0).getDate().withDayOfMonth(1).toLocalDate()) >= 0 && e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(0).getDate().toLocalDate()) <0 && e.getValue().equals(State.ACCEPTED)).count();
                    
                CasePayment payment = monitoredCase.getPaymentHistory().stream()
                    .filter(p -> p.getPaymentDate().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(0).getDate().withDayOfMonth(1).toLocalDate()) >= 0
                    && p.getPaymentDate().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(0).getDate().withDayOfMonth(monthDays(monthlyGroupMap.get(startOfMonth).get(0).getDate().toLocalDate())).toLocalDate()) <= 0)
                    .collect(Collectors.toList()).get(0);

                BigDecimal correctedPayment = (payment.getPayment()
                    .divide(BigDecimal.valueOf(monthDays(payment.getPaymentDate().toLocalDate())), 2, RoundingMode.HALF_UP))
                    .multiply(BigDecimal.valueOf(nonOffsetDays));
                log.info("hhhhhhhhhhhhhhhhh start of corrected payment :{}", correctedPayment);
                for(int i = 0; i< monthlyGroupMap.get(startOfMonth).size(); i++){
                    List<String> othrBnfts = householdApps.stream().map(h -> h.getOtherBenefitsR()).collect(Collectors.toList());
                    List<String> pensions = householdApps.stream().map(h -> h.getPensionsR()).collect(Collectors.toList());
                    log.info("ooooooooooooooooooooo household apps benefits:{}, pensions :{}", othrBnfts, pensions);
                    log.info("22222222222222222 loop per month :{}", monthlyGroupMap.get(startOfMonth).get(i));
                    //if there are more credentials in the history list for this month then calculate the offset days and payment value for each period
                    if( i+1 <  monthlyGroupMap.get(startOfMonth).size() ) {
                        final int innerI = i;
                        Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                            e -> e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().toLocalDate()) >= 0 && e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI+1).getDate().toLocalDate()) <0 && e.getValue().equals(State.ACCEPTED)).count();
                        log.info("ddddddddddddddddddddddd offsetDays :{}", offsetDays);
                        if(monthlyGroupMap.get(startOfMonth).get(i).getName().equals("household")){
                            log.info("vvvvvvvvvvvvvvvvvvvvvv houshold change :{}", monthlyGroupMap.get(startOfMonth).get(i).getHousehold());
                            updateSsiApplication2(monthlyGroupMap.get(startOfMonth).get(i).getName(), null, ssiAppTest, monthlyGroupMap.get(startOfMonth).get(i).getHousehold());
                            log.info("bbbbbbbbbbbbbbbbbbbbbbb houshold change ssiapp :{}", ssiAppTest.getHouseholdComposition());
                            //householdApps = reCalculateHousehold(householdApps, ssiAppTest);
                            reCalculateHousehold3(householdApps, ssiAppTest);
                            List<String> othrBnftsB = householdApps.stream().map(h -> h.getOtherBenefitsR()).collect(Collectors.toList());
                            List<String> pensionsB = householdApps.stream().map(h -> h.getPensionsR()).collect(Collectors.toList());
                            log.info("jjjjjjjjjjjjjjjjjjjjjjjjjj household -> household apps before aggregation benefits:{}, pensions :{}", othrBnftsB, pensionsB);
                        }else{
                            log.info("not household");
                            updateSsiApplication2(monthlyGroupMap.get(startOfMonth).get(i).getName(), monthlyGroupMap.get(startOfMonth).get(i).getValue(), householdApps.stream().filter(h -> monthlyGroupMap.get(startOfMonth).get(innerI).getAfm().equals(h.getTaxisAfm())).collect(Collectors.toList()).get(0), null);   
                            List<String> othrBnftsB = householdApps.stream().map(h -> h.getOtherBenefitsR()).collect(Collectors.toList());
                            List<String> pensionsB = householdApps.stream().map(h -> h.getPensionsR()).collect(Collectors.toList());
                            log.info("ooooooooooooooooooooo household apps before aggregation benefits:{}, pensions :{}", othrBnftsB, pensionsB);
                        }
                        //ssiAppTest = aggregateHouseholdValues(householdApps);
                        ssiAppTest = filterHHAndAggregate(householdApps, ssiAppTest.getHouseholdComposition());
                        log.info("lllllllllllllllllllllllllll sssiApp loop other benefits:{}, pensions :{}", ssiAppTest.getOtherBenefitsR(), ssiAppTest.getPensionsR());
                        log.info("ttttttttttttttttttttttttttt ssiApp household after aggregation :{}", ssiAppTest.getHouseholdComposition());
                        BigDecimal offsetPayment = calculatePayment2(fullMonthDays, offsetDays.intValue(), ssiAppTest, monthlyGroupMap.get(startOfMonth).get(i).getDate().toLocalDate());
                        correctedPayment = correctedPayment.add(offsetPayment);
                    } else {
                        // last credential change in the history list for this month
                        final int innerI = i;
                        Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                            e -> e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().toLocalDate()) >= 0 && e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().withDayOfMonth(monthDays(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().toLocalDate())).toLocalDate()) <=0 && e.getValue().equals(State.ACCEPTED)).count();
                            log.info("ddddddddddddddddddddddd last offsetDays :{}", offsetDays);
                        if(monthlyGroupMap.get(startOfMonth).get(i).getName().equals("household")){
                            updateSsiApplication2(monthlyGroupMap.get(startOfMonth).get(i).getName(), null, ssiAppTest, monthlyGroupMap.get(startOfMonth).get(i).getHousehold());   
                            //householdApps = reCalculateHousehold(householdApps, ssiAppTest);
                            reCalculateHousehold3(householdApps, ssiAppTest);
                        }else{
                            updateSsiApplication2(monthlyGroupMap.get(startOfMonth).get(i).getName(), monthlyGroupMap.get(startOfMonth).get(i).getValue(), householdApps.stream().filter(h -> monthlyGroupMap.get(startOfMonth).get(innerI).getAfm().equals(h.getTaxisAfm())).collect(Collectors.toList()).get(0), null);   
                            List<String> othrBnftsB = householdApps.stream().map(h -> h.getOtherBenefitsR()).collect(Collectors.toList());
                            List<String> pensionsB = householdApps.stream().map(h -> h.getPensionsR()).collect(Collectors.toList());
                            log.info("ooooooooooooooooooooo last household apps before aggregation benefits:{}, pensions :{}", othrBnftsB, pensionsB);
                        }
                        //ssiAppTest = aggregateHouseholdValues(householdApps);
                        ssiAppTest = filterHHAndAggregate(householdApps, ssiAppTest.getHouseholdComposition());
                        log.info("lllllllllllllllllllllllllll last sssiApp loop other benefits:{}, pensions :{}", ssiAppTest.getOtherBenefitsR(), ssiAppTest.getPensionsR());
                        
                        BigDecimal offsetPayment = calculatePayment2(fullMonthDays, offsetDays.intValue(), ssiAppTest, monthlyGroupMap.get(startOfMonth).get(i).getDate().toLocalDate());
                        correctedPayment = correctedPayment.add(offsetPayment);
                    }
                    log.info("333333333333333333333333 corrected payment :{}", correctedPayment);
                    log.info("444444444444444444444444 offset :{}", monitoredCase.getOffset());
                }

                BigDecimal monthlyOffset = ph.getPayment().subtract(correctedPayment);

                monitoredCase.setOffset(monitoredCase.getOffset().add(monthlyOffset));
                log.info("aaaaaaaaaaaaaaaaaaaaaa offset :{}", monitoredCase.getOffset());
            }
        }
    }

    // public static void calculateCurrentPayment3(Case monitoredCase, SsiApplication ssiAppTest, List<SsiApplication> householdApps){

    //     List<PaymentCredential> changedCredentials = alteredCredentialsList3(ssiAppTest, householdApps);

    //     if(changedCredentials.isEmpty()){
    //         return;
    //     }
    //     //sort the list of altered credentials by date
    //     List<PaymentCredential> changedCredentialsSorted = changedCredentials.stream().sorted(Comparator.comparing(PaymentCredential::getDate)).collect(Collectors.toList());
    //     log.info("qqqqqqqqqqqqqqqqqqqqq altered credentials sorted :{}", changedCredentialsSorted);
    //     //create a map of altered credentials grouped by month, with key the start date of the month and value the credentials that were altered during this month
    //     Map<LocalDate, List<PaymentCredential>> monthlyGroupMap = changedCredentialsSorted.stream().collect(Collectors.groupingBy(e -> e.getDate().withDayOfMonth(1).toLocalDate()));
    //     log.info("wwwwwwwwwwwwwwwwwwwwwwwwwwww monthlyGroupMap :{}", monthlyGroupMap);
    //     List<CasePayment> paymentHistory = monitoredCase.getPaymentHistory().stream().sorted(Comparator.comparing(CasePayment::getPaymentDate)).collect(Collectors.toList());
    //     Boolean credBeforeAppStart = false;
    //     LocalDate firstAcceptedDate = LocalDate.of(2000, 1, 1);
    //     //find the first date that the case was accepted
    //     for (Map.Entry<LocalDateTime,State> mCase : monitoredCase.getHistory().entrySet()) {
    //         if(mCase.getValue().equals(State.ACCEPTED)){
    //             firstAcceptedDate = mCase.getKey().toLocalDate();
    //             break;
    //         }
    //     }
    //     //check if any credential has been altered at a date before the start of the application and use that value as the base one
    //     for (Map.Entry<LocalDate, List<PaymentCredential>> mCred : monthlyGroupMap.entrySet()) {
    //         if(mCred.getKey().compareTo(firstAcceptedDate) > 0){
    //             continue;
    //         }
    //         credBeforeAppStart = true;
    //         for(PaymentCredential credential: mCred.getValue()){
    //             //householdApps.stream().filter(h -> credential.getAfm().equals(h.getTaxisAfm())).collect(Collectors.toList()).get(0);
    //             if(credential.getName().equals("household")){
    //                 updateSsiApplication2(credential.getName(), null, ssiAppTest, credential.getHousehold());
    //             } else {
    //                 updateSsiApplication2(credential.getName(), credential.getValue(), householdApps.stream().filter(h -> credential.getAfm().equals(h.getTaxisAfm())).collect(Collectors.toList()).get(0), null);
    //             }
    //             //ssiAppTest = aggregateHouseholdValues(householdApps);
    //         }
    //     }
    //     if(credBeforeAppStart){
    //         ssiAppTest = aggregateHouseholdValues(householdApps);
    //     }
    //     log.info("rrrrrrrrrrrrrrrrrrrrrrr sssiApp other benefits:{}, pensions :{}", ssiAppTest.getOtherBenefitsR(), ssiAppTest.getPensionsR());

    //     for(CasePayment ph:paymentHistory){
            
    //         log.info("111111111111111111111 start loop ph");
            
    //         LocalDate startOfMonth = ph.getPaymentDate().minusMonths(1).withDayOfMonth(1).toLocalDate();
    //         LocalDate endOfMonth = ph.getPaymentDate().minusMonths(1).withDayOfMonth(monthDays(ph.getPaymentDate().minusMonths(1).toLocalDate())).toLocalDate();
    //         Integer fullMonthDays = monthDays(startOfMonth);

    //         // if there is no credential change during this month then calculate the payment with the last credentials
    //         if(monthlyGroupMap.get(startOfMonth) == null && startOfMonth.isAfter(firstAcceptedDate)){
                
    //             Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
    //                 e -> e.getKey().toLocalDate().compareTo(startOfMonth) >= 0 && e.getKey().toLocalDate().compareTo(endOfMonth) <=0 && e.getValue().equals(State.ACCEPTED)).count();
                
    //             BigDecimal offsetPayment = calculatePayment2(fullMonthDays, offsetDays.intValue(), ssiAppTest, startOfMonth);
    //             BigDecimal monthlyOffset = ph.getPayment().subtract(offsetPayment);

    //             monitoredCase.setOffset(monitoredCase.getOffset().add(monthlyOffset));
    //         }

    //         if(monthlyGroupMap.get(startOfMonth)!= null){
    //             //calculate the starting days of the month with a payment value of the lastly updated credentials
    //             Long nonOffsetDays = monitoredCase.getHistory().entrySet().stream().filter(
    //                     e -> e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(0).getDate().withDayOfMonth(1).toLocalDate()) >= 0 && e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(0).getDate().toLocalDate()) <0 && e.getValue().equals(State.ACCEPTED)).count();
                    
    //             CasePayment payment = monitoredCase.getPaymentHistory().stream()
    //                 .filter(p -> p.getPaymentDate().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(0).getDate().withDayOfMonth(1).toLocalDate()) >= 0
    //                 && p.getPaymentDate().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(0).getDate().withDayOfMonth(monthDays(monthlyGroupMap.get(startOfMonth).get(0).getDate().toLocalDate())).toLocalDate()) <= 0)
    //                 .collect(Collectors.toList()).get(0);

    //             BigDecimal correctedPayment = (payment.getPayment()
    //                 .divide(BigDecimal.valueOf(monthDays(payment.getPaymentDate().toLocalDate())), 2, RoundingMode.HALF_UP))
    //                 .multiply(BigDecimal.valueOf(nonOffsetDays));
    //             log.info("hhhhhhhhhhhhhhhhh start of corrected payment :{}", correctedPayment);
    //             for(int i = 0; i< monthlyGroupMap.get(startOfMonth).size(); i++){
    //                 List<String> othrBnfts = householdApps.stream().map(h -> h.getOtherBenefitsR()).collect(Collectors.toList());
    //                 List<String> pensions = householdApps.stream().map(h -> h.getPensionsR()).collect(Collectors.toList());
    //                 log.info("ooooooooooooooooooooo household apps benefits:{}, pensions :{}", othrBnfts, pensions);
    //                 log.info("22222222222222222 loop per month :{}", monthlyGroupMap.get(startOfMonth).get(i));
    //                 //if there are more credentials in the history list for this month then calculate the offset days and payment value for each period
    //                 if( i+1 <  monthlyGroupMap.get(startOfMonth).size() ) {
    //                     final int innerI = i;
    //                     Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
    //                         e -> e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().toLocalDate()) >= 0 && e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI+1).getDate().toLocalDate()) <0 && e.getValue().equals(State.ACCEPTED)).count();
    //                     log.info("ddddddddddddddddddddddd offsetDays :{}", offsetDays);
    //                     if(monthlyGroupMap.get(startOfMonth).get(i).getName().equals("household")){
    //                         log.info("vvvvvvvvvvvvvvvvvvvvvv houshold change :{}", monthlyGroupMap.get(startOfMonth).get(i).getHousehold());
    //                         updateSsiApplication2(monthlyGroupMap.get(startOfMonth).get(i).getName(), null, ssiAppTest, monthlyGroupMap.get(startOfMonth).get(i).getHousehold());
    //                         log.info("bbbbbbbbbbbbbbbbbbbbbbb houshold change ssiapp :{}", ssiAppTest.getHouseholdComposition());
    //                         householdApps = reCalculateHousehold(householdApps, ssiAppTest);
    //                         List<String> othrBnftsB = householdApps.stream().map(h -> h.getOtherBenefitsR()).collect(Collectors.toList());
    //                         List<String> pensionsB = householdApps.stream().map(h -> h.getPensionsR()).collect(Collectors.toList());
    //                         log.info("jjjjjjjjjjjjjjjjjjjjjjjjjj household -> household apps before aggregation benefits:{}, pensions :{}", othrBnftsB, pensionsB);
    //                     }else{
    //                         log.info("not household");
    //                         updateSsiApplication2(monthlyGroupMap.get(startOfMonth).get(i).getName(), monthlyGroupMap.get(startOfMonth).get(i).getValue(), householdApps.stream().filter(h -> monthlyGroupMap.get(startOfMonth).get(innerI).getAfm().equals(h.getTaxisAfm())).collect(Collectors.toList()).get(0), null);   
    //                         List<String> othrBnftsB = householdApps.stream().map(h -> h.getOtherBenefitsR()).collect(Collectors.toList());
    //                         List<String> pensionsB = householdApps.stream().map(h -> h.getPensionsR()).collect(Collectors.toList());
    //                         log.info("ooooooooooooooooooooo household apps before aggregation benefits:{}, pensions :{}", othrBnftsB, pensionsB);
    //                     }
    //                     ssiAppTest = aggregateHouseholdValues(householdApps);
    //                     log.info("lllllllllllllllllllllllllll sssiApp loop other benefits:{}, pensions :{}", ssiAppTest.getOtherBenefitsR(), ssiAppTest.getPensionsR());
    //                     log.info("ttttttttttttttttttttttttttt ssiApp household after aggregation :{}", ssiAppTest.getHouseholdComposition());
    //                     BigDecimal offsetPayment = calculatePayment2(fullMonthDays, offsetDays.intValue(), ssiAppTest, monthlyGroupMap.get(startOfMonth).get(i).getDate().toLocalDate());
    //                     correctedPayment = correctedPayment.add(offsetPayment);
    //                 } else {
    //                     // last credential change in the history list for this month
    //                     final int innerI = i;
    //                     Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
    //                         e -> e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().toLocalDate()) >= 0 && e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().withDayOfMonth(monthDays(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().toLocalDate())).toLocalDate()) <=0 && e.getValue().equals(State.ACCEPTED)).count();
    //                         log.info("ddddddddddddddddddddddd last offsetDays :{}", offsetDays);
    //                     if(monthlyGroupMap.get(startOfMonth).get(i).getName().equals("household")){
    //                         updateSsiApplication2(monthlyGroupMap.get(startOfMonth).get(i).getName(), null, ssiAppTest, monthlyGroupMap.get(startOfMonth).get(i).getHousehold());   
    //                         householdApps = reCalculateHousehold(householdApps, ssiAppTest);
    //                     }else{
    //                         updateSsiApplication2(monthlyGroupMap.get(startOfMonth).get(i).getName(), monthlyGroupMap.get(startOfMonth).get(i).getValue(), householdApps.stream().filter(h -> monthlyGroupMap.get(startOfMonth).get(innerI).getAfm().equals(h.getTaxisAfm())).collect(Collectors.toList()).get(0), null);   
    //                         List<String> othrBnftsB = householdApps.stream().map(h -> h.getOtherBenefitsR()).collect(Collectors.toList());
    //                         List<String> pensionsB = householdApps.stream().map(h -> h.getPensionsR()).collect(Collectors.toList());
    //                         log.info("ooooooooooooooooooooo last household apps before aggregation benefits:{}, pensions :{}", othrBnftsB, pensionsB);
    //                     }
    //                     ssiAppTest = aggregateHouseholdValues(householdApps);

    //                     log.info("lllllllllllllllllllllllllll last sssiApp loop other benefits:{}, pensions :{}", ssiAppTest.getOtherBenefitsR(), ssiAppTest.getPensionsR());
                        
    //                     BigDecimal offsetPayment = calculatePayment2(fullMonthDays, offsetDays.intValue(), ssiAppTest, monthlyGroupMap.get(startOfMonth).get(i).getDate().toLocalDate());
    //                     correctedPayment = correctedPayment.add(offsetPayment);
    //                 }
    //                 log.info("333333333333333333333333 corrected payment :{}", correctedPayment);
    //                 log.info("444444444444444444444444 offset :{}", monitoredCase.getOffset());
    //             }

    //             BigDecimal monthlyOffset = ph.getPayment().subtract(correctedPayment);

    //             monitoredCase.setOffset(monitoredCase.getOffset().add(monthlyOffset));
    //             log.info("aaaaaaaaaaaaaaaaaaaaaa offset :{}", monitoredCase.getOffset());
    //         }
    //     }
    // }

    public static BigDecimal calculateCurrentPayment2(Case monitoredCase, SsiApplication ssiAppTest, List<SsiApplication> householdApps){

        Set<String> allAfms = new HashSet<>();
        //List<SsiApplication> householdApps = new ArrayList<>();
        for (Map.Entry<LocalDateTime, List<HouseholdMember>> app : ssiAppTest.getHouseholdCompositionHistory().entrySet()) {
            for(HouseholdMember member : app.getValue()){
                allAfms.add(member.getAfm());
            }
        }
        
        
        LocalDate startOfPayment = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        Integer monthDays = monthDays(startOfPayment);
        LocalDate endOfPayment = LocalDate.now().minusMonths(1).withDayOfMonth(monthDays);
        Long acceptedDays = monitoredCase.getHistory().entrySet().stream().filter(
                        e -> e.getKey().toLocalDate().compareTo(startOfPayment) >= 0 && e.getKey().toLocalDate().compareTo(endOfPayment) <=0 && e.getValue().equals(State.ACCEPTED)).count();
        
        //ssiAppTest = aggregateHouseholdValues(householdApps); 
        SsiApplication ssiAppProjection = filterHHAndAggregate(householdApps, ssiAppTest.getHouseholdComposition());           
        BigDecimal projectedPayment = calculatePayment2(monthDays, acceptedDays.intValue(), ssiAppProjection, LocalDate.now());

        List<PaymentCredential> changedCredentials = latestAlteredCredentials(ssiAppTest, householdApps);
        ssiAppTest = filterHHAndAggregate(householdApps, ssiAppTest.getHouseholdComposition());    
        log.info("======================= changedCredentials :{}", changedCredentials);
        
        BigDecimal correctedPayment = BigDecimal.ZERO;

        if(changedCredentials.isEmpty() || !changedCredentials.stream().anyMatch(c -> c.getDate().withDayOfMonth(1).toLocalDate().equals(startOfPayment))){
            return projectedPayment;
        }
        //sort the list of altered credentials by date
        List<PaymentCredential> changedCredentialsSorted = changedCredentials.stream().sorted(Comparator.comparing(PaymentCredential::getDate)).collect(Collectors.toList());
        //create a map of altered credentials grouped by month, with key the start date of the month and value the credentials that were altered during this month
        // Map<LocalDate, List<PaymentCredential>> monthlyGroupMap = changedCredentialsSorted.stream().collect(Collectors.groupingBy(e -> e.getDate().withDayOfMonth(1).toLocalDate()));
        
        // List<CasePayment> paymentHistory = monitoredCase.getPaymentHistory().stream().sorted(Comparator.comparing(CasePayment::getPaymentDate)).collect(Collectors.toList());
        
        // CasePayment currentPayment = new CasePayment();
        // currentPayment.setPaymentDate(LocalDateTime.now());
        // currentPayment.setPayment(projectedPayment);
        // paymentHistory.add(currentPayment);

        // monthlyGroupMap.entrySet().forEach(m -> {
        //     if(m.getKey().compareTo(paymentHistory.get(0).getPaymentDate().toLocalDate()) <= 0){
        //         m.getValue().forEach(l ->{
        //             updateSsiApplication2(l.getName(), l.getValue(), ssiAppTest);   
        //         });
        //     }
        // });

        //for(CasePayment payment : paymentHistory){
            //LocalDate startOfMonth = payment.getPaymentDate().minusMonths(1).withDayOfMonth(1).toLocalDate();
            LocalDate startOfMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
            Integer fullMonthDays = monthDays(startOfMonth);

            //if(monthlyGroupMap.get(startOfMonth)!= null){
                // if(!startOfMonth.isEqual(startOfPayment)){
                //     continue;   
                // }
                Long nonOffsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                        e -> e.getKey().toLocalDate().compareTo(startOfMonth) >= 0 
                        && e.getKey().toLocalDate().compareTo(changedCredentialsSorted.get(0).getDate().toLocalDate()) <0 
                        && e.getValue().equals(State.ACCEPTED)).count();

                        log.info("11111111111111111111 nonOffsetDays :{}", nonOffsetDays);
                        log.info("GGGGGGGGGGGGGGGGGGGGGGGGG ssiAppTest non offset :{}", ssiAppTest);
                    
                correctedPayment = calculatePayment2(fullMonthDays, nonOffsetDays.intValue(), ssiAppTest, startOfMonth);
                for(int i = 0; i< changedCredentialsSorted.size(); i++){
                    if( i+1 < changedCredentialsSorted.size() ) {
                        // if(!startOfMonth.isEqual(startOfPayment)){
                        //     updateSsiApplication(monthlyGroupMap.get(startOfMonth).get(i).getName(), monthlyGroupMap.get(startOfMonth).get(i).getValue(), ssiAppTest);
                        //     continue;   
                        // }
                    
                        final int innerI = i;
                        Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                            e -> e.getKey().toLocalDate().compareTo(changedCredentialsSorted.get(innerI).getDate().toLocalDate()) >= 0 
                            && e.getKey().toLocalDate().compareTo(changedCredentialsSorted.get(innerI+1).getDate().toLocalDate()) <0 
                            && e.getValue().equals(State.ACCEPTED)).count();
                            log.info("22222222222222222222 offsetDays :{}", offsetDays);
                        if(changedCredentialsSorted.get(i).getName().equals("household")){
                            updateSsiApplication2(changedCredentialsSorted.get(i).getName(), null, ssiAppTest, changedCredentialsSorted.get(i).getHousehold());
                            //householdApps = reCalculateHousehold(householdApps, ssiAppTest);
                            reCalculateHousehold3(householdApps, ssiAppTest);
                        } else{
                            log.info("3333333333333333333333333 not household");
                            log.info("OOOOOOOOOOOOOOOOOOOOOO changedCredentialsSorted.get(i).getName() :{}, changedCredentialsSorted.get(i).getValue() :{}", changedCredentialsSorted.get(i).getName(), changedCredentialsSorted.get(i).getValue());
                            updateSsiApplication2(changedCredentialsSorted.get(i).getName(), changedCredentialsSorted.get(i).getValue(), householdApps.stream().filter(h -> changedCredentialsSorted.get(innerI).getAfm().equals(h.getTaxisAfm())).collect(Collectors.toList()).get(0), null);   
                        }
                        
                        //ssiAppTest = aggregateHouseholdValues(householdApps);
                        ssiAppTest = filterHHAndAggregate(householdApps, ssiAppTest.getHouseholdComposition());
                        BigDecimal offsetPayment = calculatePayment2(fullMonthDays, offsetDays.intValue(), ssiAppTest, changedCredentialsSorted.get(i).getDate().toLocalDate());
                        correctedPayment = correctedPayment.add(offsetPayment);
                    } else {
                        // if(!startOfMonth.isEqual(startOfPayment)){
                        //     updateSsiApplication(monthlyGroupMap.get(startOfMonth).get(i).getName(), monthlyGroupMap.get(startOfMonth).get(i).getValue(), ssiAppTest); 
                        //     continue;   
                        // }
                        final int innerI = i;
                        Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                            e -> e.getKey().toLocalDate().compareTo(changedCredentialsSorted.get(innerI).getDate().toLocalDate()) >= 0 
                            && e.getKey().toLocalDate().compareTo(changedCredentialsSorted.get(innerI).getDate().withDayOfMonth(monthDays(changedCredentialsSorted.get(innerI).getDate().toLocalDate())).toLocalDate()) <=0 
                            && e.getValue().equals(State.ACCEPTED)).count();
                            log.info("444444444444444444444 offsetDays last:{}", offsetDays);
                        if(changedCredentialsSorted.get(i).getName().equals("household")){
                            updateSsiApplication2(changedCredentialsSorted.get(i).getName(), null, ssiAppTest, changedCredentialsSorted.get(i).getHousehold());
                            //householdApps = reCalculateHousehold(householdApps, ssiAppTest);
                            reCalculateHousehold3(householdApps, ssiAppTest);
                        } else{
                            log.info("5555555555555555555555555 not household last");
                            log.info("PPPPPPPPPPPPPPPPPPPPPPP changedCredentialsSorted.get(i).getName() :{}, changedCredentialsSorted.get(i).getValue() :{}", changedCredentialsSorted.get(i).getName(), changedCredentialsSorted.get(i).getValue());
                            
                            updateSsiApplication2(changedCredentialsSorted.get(i).getName(), changedCredentialsSorted.get(i).getValue(), householdApps.stream().filter(h -> changedCredentialsSorted.get(innerI).getAfm().equals(h.getTaxisAfm())).collect(Collectors.toList()).get(0), null);   
                        }
                        
                        //ssiAppTest = aggregateHouseholdValues(householdApps);
                        ssiAppTest = filterHHAndAggregate(householdApps, ssiAppTest.getHouseholdComposition());
                        BigDecimal offsetPayment = calculatePayment2(fullMonthDays, offsetDays.intValue(), ssiAppTest, changedCredentialsSorted.get(i).getDate().toLocalDate());
                        correctedPayment = correctedPayment.add(offsetPayment);
                    }
                }
           // }
        //}

        return correctedPayment;
    }

    private static List<PaymentCredential> latestAlteredCredentials(SsiApplication ssiAppTest, List<SsiApplication> householdApps){

        List<PaymentCredential> changedCredentials = new ArrayList<>();

        householdApps.forEach(h -> {
            Map<String, LinkedHashMap<LocalDateTime, String>> credHistoriesMap = new HashMap<>();
            if(h.getPensionsRHistory()!=null){
                credHistoriesMap.put("pension", h.getPensionsRHistory());
            }
            if(h.getSalariesRHistory()!=null){
                credHistoriesMap.put("salaries", h.getSalariesRHistory());
            }
            if(h.getOtherBenefitsRHistory()!=null){
                credHistoriesMap.put("otherBnfts", h.getOtherBenefitsRHistory());
            }
            if(h.getFreelanceRHistory()!=null){
                credHistoriesMap.put("freelance", h.getFreelanceRHistory());
            }
            if(h.getDepositsAHistory()!=null){
                credHistoriesMap.put("deposits", h.getDepositsAHistory());
            }
            if(h.getDomesticRealEstateAHistory()!=null){
                credHistoriesMap.put("domesticRE", h.getDomesticRealEstateAHistory());
            }
            if(h.getForeignRealEstateAHistory()!=null){
                credHistoriesMap.put("foreignRE", h.getForeignRealEstateAHistory());
            }

            //if there are altered credentials (credential history size > 1) set the credential date and value to the default (first value) and add it to the list of changed credentials
            if(!credHistoriesMap.isEmpty()){
                credHistoriesMap.entrySet().forEach(e -> {
                    //if(e.getValue().size()>1){
                    Optional<Entry<LocalDateTime, String>> maxEntry = e.getValue().entrySet().stream().filter(m -> m.getKey().toLocalDate().compareTo(LocalDate.now().minusMonths(1).withDayOfMonth(1)) <= 0)
                    .max((Entry<LocalDateTime, String> e1, Entry<LocalDateTime, String> e2) -> e1.getKey()
                    .compareTo(e2.getKey()));
                        log.info("FFFFFFFFFFFFFFFFFFFFFFFFFFF max entry date:{}, value :{}", maxEntry.get().getKey(), maxEntry.get().getValue());
                    if(maxEntry.isPresent()){
                        updateSsiApplication2(e.getKey(), maxEntry.get().getValue(), h, null);
                    }
                    if(e.getValue().size()>1){
                        e.getValue().entrySet().stream().skip(1).forEach(p -> {
                            if(p.getKey().toLocalDate().compareTo(LocalDate.now().minusMonths(1).withDayOfMonth(1)) > 0){
                                PaymentCredential credential = new PaymentCredential();
                                credential.setDate(p.getKey()); 
                                credential.setValue(p.getValue());
                                credential.setName(e.getKey());
                                credential.setAfm(h.getTaxisAfm());
                                changedCredentials.add(credential);
                            }
                        });
                    // updateSsiApplication2(e.getKey(), e.getValue().entrySet().iterator().next().getValue(), h, null);
                    }
                });
            }
        });
        log.info("KKKKKKKKKKKKKKKKKKKKKK household history :{}", ssiAppTest.getHouseholdCompositionHistory());
        if(ssiAppTest.getHouseholdCompositionHistory()!=null){
            Optional<Entry<LocalDateTime, List<HouseholdMember>>> maxEntry = ssiAppTest.getHouseholdCompositionHistory().entrySet().stream().filter(m -> m.getKey().toLocalDate().compareTo(LocalDate.now().minusMonths(1).withDayOfMonth(1)) <= 0)
            .max((Entry<LocalDateTime, List<HouseholdMember>> e1, Entry<LocalDateTime, List<HouseholdMember>> e2) -> e1.getKey()
            .compareTo(e2.getKey()));
            log.info("HHHHHHHHHHHHHHHHHHHHHHHHHH  household max entry date:{}, value :{}", maxEntry.get().getKey(), maxEntry.get().getValue());
            if(maxEntry.isPresent()){
                updateSsiApplication2("household", null, ssiAppTest, maxEntry.get().getValue());
            }
            if(ssiAppTest.getHouseholdCompositionHistory().size()>1){
                ssiAppTest.getHouseholdCompositionHistory().entrySet().stream().skip(1).forEach(p -> {
                    if(p.getKey().toLocalDate().compareTo(LocalDate.now().minusMonths(1).withDayOfMonth(1)) > 0){
                        PaymentCredential credential = new PaymentCredential();
                        credential.setDate(p.getKey()); 
                        //credential.setValue(p.getValue());
                        credential.setName("household");
                        credential.setHousehold(p.getValue());
                        changedCredentials.add(credential);
                    }
                });
            }
            
            //updateSsiApplication2("household", null, ssiAppTest, ssiAppTest.getHouseholdCompositionHistory().entrySet().iterator().next().getValue());
        }

        // List<String> othrBnfts = householdApps.stream().map(h -> h.getOtherBenefitsR()).collect(Collectors.toList());
        // List<String> pensions = householdApps.stream().map(h -> h.getPensionsR()).collect(Collectors.toList());
        // log.info("ggggggggggggggggggggggggg household before any aggregation apps benefits:{}, pensions :{}", othrBnfts, pensions);
        // log.info("hhhhhhhhhhhhhhhhhhhhhhhhh ssiapp before aggregate :{}", ssiAppTest);
        //ssiAppTest = filterHHAndAggregate(householdApps, ssiAppTest.getHouseholdComposition());

        // List<String> othrBnftsA = householdApps.stream().map(h -> h.getOtherBenefitsR()).collect(Collectors.toList());
        // List<String> pensionsA = householdApps.stream().map(h -> h.getPensionsR()).collect(Collectors.toList());
        // log.info("ggggggggggggggggggggggggg household after aggregation apps benefits:{}, pensions :{}", othrBnftsA, pensionsA);
        // log.info("hhhhhhhhhhhhhhhhhhhhhhhhh ssiapp after aggregate :{}", ssiAppTest);

        return changedCredentials;
    }

    private static SsiApplication filterHHAndAggregate(List<SsiApplication> householdApps/*, SsiApplication ssiApp*/, List<HouseholdMember> currentHousehold){

        List<String> currentAfms = currentHousehold.stream().map(h -> h.getAfm()).collect(Collectors.toList());

        List<SsiApplication> filteredHouseholdApps = householdApps.stream().filter(h -> currentAfms.contains(h.getTaxisAfm())).collect(Collectors.toList());
        return aggregateHouseholdValues(filteredHouseholdApps);

    }

}
