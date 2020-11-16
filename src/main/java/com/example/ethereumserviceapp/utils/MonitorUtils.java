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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.CasePayment;
import com.example.ethereumserviceapp.model.HouseholdMember;
import com.example.ethereumserviceapp.model.PaymentCredential;
import com.example.ethereumserviceapp.model.State;
import com.example.ethereumserviceapp.model.entities.SsiApplication;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author nikos
 */
@Slf4j
public class MonitorUtils extends EthAppUtils{
                
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

    public static void calculateOffset(Case monitoredCase, SsiApplication ssiApp, List<SsiApplication> householdApps){

        List<PaymentCredential> changedCredentials = alteredCredentialsList(ssiApp, householdApps);

        if(changedCredentials.isEmpty()){
            return;
        }
        //sort the list of altered credentials by date
        List<PaymentCredential> changedCredentialsSorted = changedCredentials.stream().sorted(Comparator.comparing(PaymentCredential::getDate)).collect(Collectors.toList());
        
        //create a map of altered credentials grouped by month, with key the start date of the month and value the credentials that were altered during this month
        Map<LocalDate, List<PaymentCredential>> monthlyGroupMap = changedCredentialsSorted.stream().collect(Collectors.groupingBy(e -> e.getDate().withDayOfMonth(1).toLocalDate()));
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
                if(credential.getName().equals("household")){
                    updateSsiApplication(credential.getName(), null, ssiApp, credential.getHousehold());
                } else {
                    updateSsiApplication(credential.getName(), credential.getValue(), householdApps.stream().filter(h -> credential.getAfm().equals(h.getTaxisAfm())).collect(Collectors.toList()).get(0), null);
                }
            }
        }
        if(credBeforeAppStart){
            ssiApp = filterHHAndAggregate(householdApps, ssiApp.getHouseholdComposition());
        }
        for(CasePayment ph:paymentHistory){
            
            LocalDate startOfMonth = ph.getPaymentDate().minusMonths(1).withDayOfMonth(1).toLocalDate();
            LocalDate endOfMonth = ph.getPaymentDate().minusMonths(1).withDayOfMonth(monthDays(ph.getPaymentDate().minusMonths(1).toLocalDate())).toLocalDate();
            Integer fullMonthDays = monthDays(startOfMonth);

            // if there is no credential change during this month then calculate the payment with the last credentials
            if(monthlyGroupMap.get(startOfMonth) == null && startOfMonth.isAfter(firstAcceptedDate)){
                
                Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                    e -> e.getKey().toLocalDate().compareTo(startOfMonth) >= 0 && e.getKey().toLocalDate().compareTo(endOfMonth) <=0 && e.getValue().equals(State.ACCEPTED)).count();
                
                BigDecimal offsetPayment = calculatePayment(fullMonthDays, offsetDays.intValue(), ssiApp, startOfMonth);
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
                for(int i = 0; i< monthlyGroupMap.get(startOfMonth).size(); i++){
                    Long offsetDays = Long.valueOf(0);
                    //if there are more credentials in the history list for this month then calculate the offset days and payment value for each period
                    if( i+1 <  monthlyGroupMap.get(startOfMonth).size() ) {
                        final int innerI = i;
                        offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                            e -> e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().toLocalDate()) >= 0
                                && e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI+1).getDate().toLocalDate()) <0 
                                && e.getValue().equals(State.ACCEPTED))
                                .count();
                        
                    } else {
                        // last credential change in the history list for this month
                        final int innerI = i;
                        offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                            e -> e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().toLocalDate()) >= 0 
                                && e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().withDayOfMonth(monthDays(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().toLocalDate())).toLocalDate()) <=0 
                                && e.getValue().equals(State.ACCEPTED))
                                .count();
                    }
                    updateAlteredCredential(monthlyGroupMap.get(startOfMonth).get(i).getName(),
                            ssiApp,
                            monthlyGroupMap.get(startOfMonth).get(i).getValue(), 
                            monthlyGroupMap.get(startOfMonth).get(i).getHousehold(), 
                            householdApps,
                            monthlyGroupMap.get(startOfMonth).get(i).getAfm());

                    ssiApp = filterHHAndAggregate(householdApps, ssiApp.getHouseholdComposition());
                    BigDecimal offsetPayment = calculatePayment(fullMonthDays, offsetDays.intValue(), ssiApp, monthlyGroupMap.get(startOfMonth).get(i).getDate().toLocalDate());
                    correctedPayment = correctedPayment.add(offsetPayment);
                }
                BigDecimal monthlyOffset = ph.getPayment().subtract(correctedPayment);

                monitoredCase.setOffset(monitoredCase.getOffset().add(monthlyOffset));
            }
        }
    }

    public static BigDecimal calculateCurrentPayment(Case monitoredCase, SsiApplication ssiApp, List<SsiApplication> householdApps){
        
        LocalDate startOfPayment = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        Integer monthDays = monthDays(startOfPayment);
        LocalDate endOfPayment = LocalDate.now().minusMonths(1).withDayOfMonth(monthDays);
        Long acceptedDays = monitoredCase.getHistory().entrySet().stream().filter(
                        e -> e.getKey().toLocalDate().compareTo(startOfPayment) >= 0 && e.getKey().toLocalDate().compareTo(endOfPayment) <=0 && e.getValue().equals(State.ACCEPTED)).count();
        
        SsiApplication ssiAppProjection = filterHHAndAggregate(householdApps, ssiApp.getHouseholdComposition());           
        BigDecimal projectedPayment = calculatePayment(monthDays, acceptedDays.intValue(), ssiAppProjection, LocalDate.now());

        List<PaymentCredential> changedCredentials = latestAlteredCredentials(ssiApp, householdApps);
        ssiApp = filterHHAndAggregate(householdApps, ssiApp.getHouseholdComposition());   
        
        BigDecimal correctedPayment = BigDecimal.ZERO;

        if(changedCredentials.isEmpty() || !changedCredentials.stream().anyMatch(c -> c.getDate().withDayOfMonth(1).toLocalDate().equals(startOfPayment))){
            return projectedPayment;
        }
        //sort the list of altered credentials by date
        List<PaymentCredential> changedCredentialsSorted = changedCredentials.stream().sorted(Comparator.comparing(PaymentCredential::getDate)).collect(Collectors.toList());
        
    LocalDate startOfMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
    Integer fullMonthDays = monthDays(startOfMonth);

        Long nonOffsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                e -> e.getKey().toLocalDate().compareTo(startOfMonth) >= 0 
                && e.getKey().toLocalDate().compareTo(changedCredentialsSorted.get(0).getDate().toLocalDate()) <0 
                && e.getValue().equals(State.ACCEPTED)).count();
            
        correctedPayment = calculatePayment(fullMonthDays, nonOffsetDays.intValue(), ssiApp, startOfMonth);
        for(int i = 0; i< changedCredentialsSorted.size(); i++){
            Long offsetDays = Long.valueOf(0);
            if( i+1 < changedCredentialsSorted.size() ) {
                final int innerI = i;
                offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                    e -> e.getKey().toLocalDate().compareTo(changedCredentialsSorted.get(innerI).getDate().toLocalDate()) >= 0 
                        && e.getKey().toLocalDate().compareTo(changedCredentialsSorted.get(innerI+1).getDate().toLocalDate()) <0 
                        && e.getValue().equals(State.ACCEPTED))
                        .count();

                updateAlteredCredential(changedCredentialsSorted.get(i).getName(),
                    ssiApp,
                    changedCredentialsSorted.get(i).getValue(), 
                    changedCredentialsSorted.get(i).getHousehold(), 
                    householdApps,
                    changedCredentialsSorted.get(innerI).getAfm());
            } else {
                final int innerI = i;
                offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                    e -> e.getKey().toLocalDate().compareTo(changedCredentialsSorted.get(innerI).getDate().toLocalDate()) >= 0 
                        && e.getKey().toLocalDate().compareTo(changedCredentialsSorted.get(innerI).getDate().withDayOfMonth(monthDays(changedCredentialsSorted.get(innerI).getDate().toLocalDate())).toLocalDate()) <=0 
                        && e.getValue().equals(State.ACCEPTED))
                        .count();
            }
            updateAlteredCredential(changedCredentialsSorted.get(i).getName(),
                ssiApp,
                changedCredentialsSorted.get(i).getValue(), 
                changedCredentialsSorted.get(i).getHousehold(), 
                householdApps,
                changedCredentialsSorted.get(i).getAfm());

            ssiApp = filterHHAndAggregate(householdApps, ssiApp.getHouseholdComposition());
            BigDecimal offsetPayment = calculatePayment(fullMonthDays, offsetDays.intValue(), ssiApp, changedCredentialsSorted.get(i).getDate().toLocalDate());
            correctedPayment = correctedPayment.add(offsetPayment);
        }

        return correctedPayment;
    }

    private static List<PaymentCredential> alteredCredentialsList(SsiApplication ssiApp, List<SsiApplication> householdApps){

        List<PaymentCredential> changedCredentials = new ArrayList<>();

        if(ssiApp.getHouseholdCompositionHistory()!=null){
            ssiApp.getHouseholdCompositionHistory().entrySet().stream().skip(1).forEach(p -> {
                updatePaymentCredential(p.getKey(), "household", null, p.getValue(), null,  changedCredentials);
            });
            updateSsiApplication("household", null, ssiApp, ssiApp.getHouseholdCompositionHistory().entrySet().iterator().next().getValue());
        }

        householdApps.forEach(h -> {
            Map<String, LinkedHashMap<LocalDateTime, String>> credHistoriesMap = new HashMap<>();
            groupAlteredCredentials(h, credHistoriesMap);

            //if there are altered credentials (credential history size > 1) set the credential date and value to the default (first value) and add it to the list of changed credentials
            if(!credHistoriesMap.isEmpty()){
                credHistoriesMap.entrySet().forEach(e -> {
                    if(e.getValue().size()>1){
                        e.getValue().entrySet().stream().skip(1).forEach(p -> {
                            updatePaymentCredential(p.getKey(), e.getKey(), p.getValue(), null, h.getTaxisAfm(), changedCredentials);
                        });
                        updateSsiApplication(e.getKey(), e.getValue().entrySet().iterator().next().getValue(), h, null);
                    }
                });
            }
        });
        ssiApp = filterHHAndAggregate(householdApps, ssiApp.getHouseholdComposition());
         
        return changedCredentials;
    }

    private static List<PaymentCredential> latestAlteredCredentials(SsiApplication ssiApp, List<SsiApplication> householdApps){

        List<PaymentCredential> changedCredentials = new ArrayList<>();

        householdApps.forEach(h -> {
            Map<String, LinkedHashMap<LocalDateTime, String>> credHistoriesMap = new HashMap<>();
            groupAlteredCredentials(h, credHistoriesMap);

            //if there are altered credentials (credential history size > 1) set the credential date and value to the default (first value) and add it to the list of changed credentials
            if(!credHistoriesMap.isEmpty()){
                credHistoriesMap.entrySet().forEach(e -> {
                    Optional<Entry<LocalDateTime, String>> maxEntry = e.getValue().entrySet().stream().filter(m -> m.getKey().toLocalDate().compareTo(LocalDate.now().minusMonths(1).withDayOfMonth(1)) <= 0)
                    .max((Entry<LocalDateTime, String> e1, Entry<LocalDateTime, String> e2) -> e1.getKey()
                    .compareTo(e2.getKey()));
                    if(maxEntry.isPresent()){
                        updateSsiApplication(e.getKey(), maxEntry.get().getValue(), h, null);
                    }
                    if(e.getValue().size()>1){
                        e.getValue().entrySet().stream().skip(1).forEach(p -> {
                            if(p.getKey().toLocalDate().compareTo(LocalDate.now().minusMonths(1).withDayOfMonth(1)) > 0){
                                updatePaymentCredential(p.getKey(), e.getKey(), p.getValue(), null, h.getTaxisAfm(), changedCredentials);
                            }
                        });
                    }
                });
            }
        });
        if(ssiApp.getHouseholdCompositionHistory()!=null){
            Optional<Entry<LocalDateTime, List<HouseholdMember>>> maxEntry = ssiApp.getHouseholdCompositionHistory().entrySet().stream().filter(m -> m.getKey().toLocalDate().compareTo(LocalDate.now().minusMonths(1).withDayOfMonth(1)) <= 0)
            .max((Entry<LocalDateTime, List<HouseholdMember>> e1, Entry<LocalDateTime, List<HouseholdMember>> e2) -> e1.getKey()
            .compareTo(e2.getKey()));
            if(maxEntry.isPresent()){
                updateSsiApplication("household", null, ssiApp, maxEntry.get().getValue());
            }
            if(ssiApp.getHouseholdCompositionHistory().size()>1){
                ssiApp.getHouseholdCompositionHistory().entrySet().stream().skip(1).forEach(p -> {
                    if(p.getKey().toLocalDate().compareTo(LocalDate.now().minusMonths(1).withDayOfMonth(1)) > 0){
                        updatePaymentCredential(p.getKey(), "household", null, p.getValue(), null,  changedCredentials);
                    }
                });
            }
        }

        return changedCredentials;
    }

    private static void groupAlteredCredentials(SsiApplication ssiApp, Map<String, LinkedHashMap<LocalDateTime, String>> credHistoriesMap){
        
        if(ssiApp.getPensionsRHistory()!=null){
            credHistoriesMap.put("pension", ssiApp.getPensionsRHistory());
        }
        if(ssiApp.getSalariesRHistory()!=null){
            credHistoriesMap.put("salaries", ssiApp.getSalariesRHistory());
        }
        if(ssiApp.getOtherBenefitsRHistory()!=null){
            credHistoriesMap.put("otherBnfts", ssiApp.getOtherBenefitsRHistory());
        }
        if(ssiApp.getFreelanceRHistory()!=null){
            credHistoriesMap.put("freelance", ssiApp.getFreelanceRHistory());
        }
        if(ssiApp.getDepositsAHistory()!=null){
            credHistoriesMap.put("deposits", ssiApp.getDepositsAHistory());
        }
        if(ssiApp.getDomesticRealEstateAHistory()!=null){
            credHistoriesMap.put("domesticRE", ssiApp.getDomesticRealEstateAHistory());
        }
        if(ssiApp.getForeignRealEstateAHistory()!=null){
            credHistoriesMap.put("foreignRE", ssiApp.getForeignRealEstateAHistory());
        }
    }

    private static void updatePaymentCredential(LocalDateTime date, String name, String value, List<HouseholdMember> household, String afm, List<PaymentCredential> changedCredentials){
        PaymentCredential credential = new PaymentCredential();
        credential.setDate(date); 
        credential.setName(name);
        credential.setValue(value);
        credential.setHousehold(household);
        credential.setAfm(afm);
        changedCredentials.add(credential);
    }

    private static void updateSsiApplication(String name, String value, SsiApplication ssiApp, List<HouseholdMember> household ){
        switch(name) {
            case "pension":
            ssiApp.setPensionsR(value);
            break;
            case "salaries":
            ssiApp.setSalariesR(value);
            break;
            case "otherBnfts":
            ssiApp.setOtherBenefitsR(value);
            break;
            case "freelance":
            ssiApp.setFreelanceR(value);
            break;
            case "deposits":
            ssiApp.setDepositsA(value);
            break;
            case "domesticRE":
            ssiApp.setDomesticRealEstateA(value);
            break;
            case "foreignRE":
            ssiApp.setForeignRealEstateA(value);
            break;
            case "household" :
            ssiApp.setHouseholdComposition(household);
            break;
        }
    }

    //sets the current altered credential to the new value retrieved through the history
    private static void updateAlteredCredential(String name, SsiApplication ssiApp, String value, List<HouseholdMember> household, List<SsiApplication> householdApps, String afm){
        if(name.equals("household")){
            updateSsiApplication(name, null, ssiApp, household);   
            reCalculateHousehold(householdApps, ssiApp);
        }else{
            updateSsiApplication(name, value, householdApps.stream().filter(h -> afm.equals(h.getTaxisAfm())).collect(Collectors.toList()).get(0), null);   
        }
    }

    private static void reCalculateHousehold(List<SsiApplication> householdApps, SsiApplication ssiApp ){
        List<String> newHouseholdAfms = ssiApp.getHouseholdComposition().stream().map(h -> h.getAfm()).collect(Collectors.toList());
        for(SsiApplication app:householdApps){
            if(newHouseholdAfms.contains(app.getTaxisAfm())){
                app.setHouseholdComposition(ssiApp.getHouseholdComposition());
            }
        }
    }

    private static SsiApplication filterHHAndAggregate(List<SsiApplication> householdApps/*, SsiApplication ssiApp*/, List<HouseholdMember> currentHousehold){

        List<String> currentAfms = currentHousehold.stream().map(h -> h.getAfm()).collect(Collectors.toList());

        List<SsiApplication> filteredHouseholdApps = householdApps.stream().filter(h -> currentAfms.contains(h.getTaxisAfm())).collect(Collectors.toList());
        return aggregateHouseholdValues(filteredHouseholdApps);

    }

}
