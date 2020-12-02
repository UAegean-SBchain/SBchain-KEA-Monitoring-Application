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
            List<LocalDate> monthDates =  monitoredCase.getHistory().entrySet().stream().filter(
                    e -> e.getKey().toLocalDate().compareTo(startOfMonth) >= 0 && e.getKey().toLocalDate().compareTo(endOfMonth) <=0 && e.getValue().equals(State.ACCEPTED)).map(e -> e.getKey().toLocalDate()).collect(Collectors.toList());

            //find all the dates that minors become adults during this month, get the taxis date of birth of all the applications of the household history
            List<LocalDate> ageOffsetDates = findOffsetAgeDates(householdApps.stream().map(h -> h.getTaxisDateOfBirth()).collect(Collectors.toList()), monthDates).stream().sorted().collect(Collectors.toList());
            // if there is no credential change during this month then calculate the payment with the last credentials
            if(monthlyGroupMap.get(startOfMonth) == null && startOfMonth.isAfter(firstAcceptedDate)){
                BigDecimal correctedPayment = BigDecimal.ZERO;
                List<LocalDate> offsetDates = monitoredCase.getHistory().entrySet().stream().filter(
                    e -> e.getKey().toLocalDate().compareTo(startOfMonth) >= 0 && e.getKey().toLocalDate().compareTo(endOfMonth) <=0 && e.getValue().equals(State.ACCEPTED)).map(x -> x.getKey().toLocalDate()).collect(Collectors.toList());
               
                List<LocalDate> ageList =  new ArrayList<>();
                if(!ageOffsetDates.isEmpty()){
                    for(LocalDate ageOffset:ageOffsetDates){
                        if(!offsetDates.isEmpty() && ageOffset.compareTo(offsetDates.get(0)) >= 0 && ageOffset.isBefore(offsetDates.get(offsetDates.size()-1))){
                            ageList.add(ageOffset);
                        }
                    }
                }
                
                if(ageList.isEmpty()){
                    
                    BigDecimal offsetPayment = calculatePayment(fullMonthDays, offsetDates.size(), ssiApp, startOfMonth);
                    correctedPayment = correctedPayment.add(offsetPayment);
                } else {
                    BigDecimal offsetPayment = calculateAges(ageList, monitoredCase, offsetDates.get(0), offsetDates.get(offsetDates.size()-1), fullMonthDays, ssiApp);
                    correctedPayment = correctedPayment.add(offsetPayment);
                }
                BigDecimal monthlyOffset = ph.getPayment().subtract(correctedPayment);
                monitoredCase.setOffset(monitoredCase.getOffset().add(monthlyOffset));
            }

            if(monthlyGroupMap.get(startOfMonth)!= null){
                BigDecimal correctedPayment = BigDecimal.ZERO;
                if(!ageOffsetDates.isEmpty() && ageOffsetDates.get(0).isBefore(monthlyGroupMap.get(startOfMonth).get(0).getDate().toLocalDate()) ){
                    List<LocalDate> ageList = ageOffsetDates.stream().filter(a -> a.isBefore(monthlyGroupMap.get(startOfMonth).get(0).getDate().toLocalDate())).collect(Collectors.toList());
                    BigDecimal offsetPayment = calculateAges(ageList, monitoredCase, startOfMonth, monthlyGroupMap.get(startOfMonth).get(0).getDate().toLocalDate(), fullMonthDays, ssiApp);
                    correctedPayment = correctedPayment.add(offsetPayment);
                } else{
                //calculate the starting days of the month with a payment value of the lastly updated credentials
                    Long nonOffsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                            e -> e.getKey().toLocalDate().compareTo(startOfMonth) >= 0 && e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(0).getDate().toLocalDate()) <0 && e.getValue().equals(State.ACCEPTED)).count();
                        
                    ssiApp = filterHHAndAggregate(householdApps, ssiApp.getHouseholdComposition());
                    correctedPayment = calculatePayment(fullMonthDays, nonOffsetDays.intValue(), ssiApp, startOfMonth);
                }
                for(int i = 0; i< monthlyGroupMap.get(startOfMonth).size(); i++){
                    List<LocalDate> offsetDates = new ArrayList<>();
                    final int innerI = i;
                    //if there are more credentials in the history list for this month then calculate the offset days and payment value for each period
                    if( i+1 <  monthlyGroupMap.get(startOfMonth).size() ) {
                        offsetDates = monitoredCase.getHistory().entrySet().stream().filter(
                            e -> e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().toLocalDate()) >= 0
                                && e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI+1).getDate().toLocalDate()) <0 
                                && e.getValue().equals(State.ACCEPTED)).map(m -> m.getKey().toLocalDate())
                                .collect(Collectors.toList());
                                
                    } else {
                        // last credential change in the history list for this month
                        offsetDates = monitoredCase.getHistory().entrySet().stream().filter(
                            e -> e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().toLocalDate()) >= 0 
                                && e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().withDayOfMonth(monthDays(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().toLocalDate())).toLocalDate()) <=0 
                                && e.getValue().equals(State.ACCEPTED)).map(m -> m.getKey().toLocalDate())
                                .collect(Collectors.toList());
                        
                    }
                    updateAlteredCredential(monthlyGroupMap.get(startOfMonth).get(i).getName(),
                            ssiApp,
                            monthlyGroupMap.get(startOfMonth).get(i).getValue(), 
                            monthlyGroupMap.get(startOfMonth).get(i).getHousehold(), 
                            householdApps,
                            monthlyGroupMap.get(startOfMonth).get(i).getAfm());

                    ssiApp = filterHHAndAggregate(householdApps, ssiApp.getHouseholdComposition());

                    List<LocalDate> ageList =  new ArrayList<>();
                    if(!ageOffsetDates.isEmpty()){
                        for(LocalDate ageOffset:ageOffsetDates){
                            if(!offsetDates.isEmpty() && ageOffset.compareTo(offsetDates.get(0)) >= 0 && ageOffset.isBefore(offsetDates.get(offsetDates.size()-1))){
                                ageList.add(ageOffset);
                            }
                        }
                    }

                    if(ageList.isEmpty()){
                        BigDecimal offsetPayment = calculatePayment(fullMonthDays, offsetDates.size(), ssiApp, monthlyGroupMap.get(startOfMonth).get(i).getDate().toLocalDate());
                        correctedPayment = correctedPayment.add(offsetPayment);
                    } else {
                        BigDecimal offsetPayment = calculateAges(ageList, monitoredCase, offsetDates.get(0), offsetDates.get(offsetDates.size()-1), fullMonthDays, ssiApp);
                        correctedPayment = correctedPayment.add(offsetPayment);
                    }
                }
                BigDecimal monthlyOffset = ph.getPayment().subtract(correctedPayment);

                monitoredCase.setOffset(monitoredCase.getOffset().add(monthlyOffset));
            }
        }
    }

    public static BigDecimal calculateCurrentPayment(Case monitoredCase, SsiApplication ssiApp, List<SsiApplication> householdApps){
        
        LocalDate startOfPayment = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        Integer fullMonthDays = monthDays(startOfPayment);
        LocalDate endOfPayment = LocalDate.now().minusMonths(1).withDayOfMonth(fullMonthDays);

        List<LocalDate> acceptedDates = monitoredCase.getHistory().entrySet().stream().filter(
            e -> e.getKey().toLocalDate().compareTo(startOfPayment) >= 0 
            && e.getKey().toLocalDate().compareTo(endOfPayment) <=0 
            && e.getValue().equals(State.ACCEPTED))
            .map(x -> x.getKey().toLocalDate()).collect(Collectors.toList());

        SsiApplication ssiAppProjection = filterHHAndAggregate(householdApps, ssiApp.getHouseholdComposition());           
        BigDecimal projectedPayment = calculatePayment(fullMonthDays, acceptedDates.size(), ssiAppProjection, LocalDate.now());

        List<PaymentCredential> changedCredentials = latestAlteredCredentials(ssiApp, householdApps);
        ssiApp = filterHHAndAggregate(householdApps, ssiApp.getHouseholdComposition());   
        
        BigDecimal correctedPayment = BigDecimal.ZERO;
        //find all the dates that minors become adults during this month, get the taxis date of birth of all the applications of the household history
        List<LocalDate> ageOffsetDates = findOffsetAgeDates(householdApps.stream().map(h -> h.getTaxisDateOfBirth()).collect(Collectors.toList()), acceptedDates).stream().sorted().collect(Collectors.toList());
        if((changedCredentials.isEmpty() || !changedCredentials.stream().anyMatch(c -> c.getDate().withDayOfMonth(1).toLocalDate().equals(startOfPayment))) && ageOffsetDates.isEmpty()){
            return projectedPayment;
        }
        //sort the list of altered credentials by date
        List<PaymentCredential> changedCredentialsSorted = changedCredentials.stream().sorted(Comparator.comparing(PaymentCredential::getDate)).collect(Collectors.toList());
        
        List<LocalDate> nonOffsetDates = monitoredCase.getHistory().entrySet().stream().filter(
                e -> e.getKey().toLocalDate().compareTo(startOfPayment) >= 0 
                && e.getKey().toLocalDate().compareTo(changedCredentialsSorted.get(0).getDate().toLocalDate()) <0 
                && e.getValue().equals(State.ACCEPTED))
                .map(x -> x.getKey().toLocalDate()).collect(Collectors.toList());
        
        List<LocalDate> ageList =  new ArrayList<>();

        if(!ageOffsetDates.isEmpty()){
            for(LocalDate ageOffset:ageOffsetDates){
                if(!nonOffsetDates.isEmpty() && ageOffset.compareTo(nonOffsetDates.get(0)) >= 0 && ageOffset.isBefore(nonOffsetDates.get(nonOffsetDates.size()-1))){
                    ageList.add(ageOffset);
                }
            }
        }

        if(ageList.isEmpty()){
            correctedPayment = calculatePayment(fullMonthDays, nonOffsetDates.size(), ssiApp, startOfPayment);
        } else {
            BigDecimal offsetPayment = calculateAges(ageList, monitoredCase, nonOffsetDates.get(0), nonOffsetDates.get(nonOffsetDates.size()-1), fullMonthDays, ssiApp);
            correctedPayment = correctedPayment.add(offsetPayment);
        }

        for(int i = 0; i< changedCredentialsSorted.size(); i++){
            List<LocalDate> offsetDates = new ArrayList<>();
            if( i+1 < changedCredentialsSorted.size() ) {
                final int innerI = i;
                offsetDates = monitoredCase.getHistory().entrySet().stream().filter(
                    e -> e.getKey().toLocalDate().compareTo(changedCredentialsSorted.get(innerI).getDate().toLocalDate()) >= 0 
                        && e.getKey().toLocalDate().compareTo(changedCredentialsSorted.get(innerI+1).getDate().toLocalDate()) <0 
                        && e.getValue().equals(State.ACCEPTED))
                        .map(x -> x.getKey().toLocalDate()).collect(Collectors.toList());
            } else {
                final int innerI = i;
                offsetDates = monitoredCase.getHistory().entrySet().stream().filter(
                    e -> e.getKey().toLocalDate().compareTo(changedCredentialsSorted.get(innerI).getDate().toLocalDate()) >= 0 
                        && e.getKey().toLocalDate().compareTo(changedCredentialsSorted.get(innerI).getDate().withDayOfMonth(monthDays(changedCredentialsSorted.get(innerI).getDate().toLocalDate())).toLocalDate()) <=0 
                        && e.getValue().equals(State.ACCEPTED))
                        .map(x -> x.getKey().toLocalDate()).collect(Collectors.toList());
            }
            updateAlteredCredential(changedCredentialsSorted.get(i).getName(),
                ssiApp,
                changedCredentialsSorted.get(i).getValue(), 
                changedCredentialsSorted.get(i).getHousehold(), 
                householdApps,
                changedCredentialsSorted.get(i).getAfm());

            ssiApp = filterHHAndAggregate(householdApps, ssiApp.getHouseholdComposition());
            
            if(!ageOffsetDates.isEmpty()){
                for(LocalDate ageOffset:ageOffsetDates){
                    if(!offsetDates.isEmpty() && ageOffset.compareTo(offsetDates.get(0)) >= 0 && ageOffset.isBefore(offsetDates.get(offsetDates.size()-1))){
                        ageList.add(ageOffset);
                    }
                }
            }
            
            if(ageList.isEmpty()){
                BigDecimal offsetPayment = calculatePayment(fullMonthDays, offsetDates.size(), ssiApp, changedCredentialsSorted.get(i).getDate().toLocalDate());
                correctedPayment = correctedPayment.add(offsetPayment);
            } else {
                BigDecimal offsetPayment = calculateAges(ageList, monitoredCase, offsetDates.get(0), offsetDates.get(offsetDates.size()-1), fullMonthDays, ssiApp);
                correctedPayment = correctedPayment.add(offsetPayment);
            }
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

    private static List<LocalDate> findOffsetAgeDates(List<String> birthDates, List<LocalDate> offsetDates){
        List<LocalDate> ageOffsetDates = new ArrayList<>();
        for(String birthDate:birthDates){
            Set<Integer> ages = new HashSet<>();
            for(LocalDate referenceDate:offsetDates){
                Integer age = calculateAge(LocalDate.parse(birthDate, formatter), referenceDate);
                
                ages.add(age);
                if(!ages.contains(17)){
                    break;
                }
                if(ages.contains(17) && ages.contains(18)){
                    ageOffsetDates.add(referenceDate);
                    break;
                }
            }
        }
        return ageOffsetDates;
    }

    private static BigDecimal calculateAges(List<LocalDate> ageOffsetDates, Case monitoredCase, LocalDate startDate, LocalDate endDate, Integer fullMonthDays, SsiApplication ssiApp){
        
        Long nonOffsetDays = monitoredCase.getHistory().entrySet().stream().filter(
            e -> e.getKey().toLocalDate().compareTo(startDate) >= 0
                && e.getKey().toLocalDate().compareTo(ageOffsetDates.get(0)) <0 
                && e.getValue().equals(State.ACCEPTED)).count();

        BigDecimal correctedPayment = calculatePayment(fullMonthDays, nonOffsetDays.intValue(), ssiApp, startDate);
        Long offsetDays = Long.valueOf(0);
        
        for(int i=0; i<ageOffsetDates.size(); i++){
            final int innerI = i;
            if(i+1 < ageOffsetDates.size()){
                offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                    e -> e.getKey().toLocalDate().compareTo(ageOffsetDates.get(innerI)) >= 0 
                    && e.getKey().toLocalDate().compareTo(ageOffsetDates.get(innerI+1)) < 0 
                    && e.getValue().equals(State.ACCEPTED)).count();
            } else {
                offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                    e -> e.getKey().toLocalDate().compareTo(ageOffsetDates.get(innerI)) >= 0 
                    && e.getKey().toLocalDate().compareTo(endDate) <= 0 
                    && e.getValue().equals(State.ACCEPTED)).count();
            }
            BigDecimal offsetPayment = calculatePayment(fullMonthDays, offsetDays.intValue(), ssiApp, ageOffsetDates.get(i));
            correctedPayment = correctedPayment.add(offsetPayment);
        }

        return correctedPayment;
    } 

}
