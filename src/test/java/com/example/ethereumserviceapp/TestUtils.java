package com.example.ethereumserviceapp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.CasePayment;
import com.example.ethereumserviceapp.model.HouseholdMember;
import com.example.ethereumserviceapp.model.State;
import com.example.ethereumserviceapp.model.entities.SsiApplication;

import com.example.ethereumserviceapp.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestUtils {

    public Case generateMockCase(String uuid, State state, Boolean allRejected, Boolean isOld, String asyncRejectedDate){
        
        Case monitoredCase = new Case();
        monitoredCase.setUuid(uuid);
        monitoredCase.setDate(LocalDateTime.now().withDayOfMonth(1));
        monitoredCase.setState(state);
        LinkedHashMap<LocalDateTime, State> history = new LinkedHashMap<>();
        LinkedHashMap<LocalDateTime, BigDecimal> dailyValue = new LinkedHashMap<>();
        Integer daysOfCurrentPayment = monthDays(LocalDateTime.now().minusMonths(1));
        Integer daysOfMinus2 = monthDays(LocalDateTime.now().minusMonths(2));
        Integer daysOfMinus3 = monthDays(LocalDateTime.now().minusMonths(3));
        LocalDateTime currentDate = LocalDateTime.now();

        if(asyncRejectedDate != null && !"".equals(asyncRejectedDate)){
            monitoredCase.setRejectionDate(asyncRejectedDate);
        } else {
            monitoredCase.setRejectionDate("");
        }

        if(isOld){
            history.put(LocalDateTime.now().minusMonths(6).minusDays(1), State.ACCEPTED);
        }

        if(allRejected && state.equals(State.REJECTED)){
            for(int i=1; i<=daysOfMinus3; i++){
                history.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(i), State.REJECTED);
            }
            for(int i=1; i<=daysOfMinus2; i++){
                history.put(LocalDateTime.now().minusMonths(2).withDayOfMonth(i), State.REJECTED);
            }
            for(int i=1; i<=daysOfCurrentPayment; i++){
                history.put(LocalDateTime.now().minusMonths(1).withDayOfMonth(i), State.REJECTED);
            }
        } else if(!allRejected && state.equals(State.REJECTED)) {
            for(int i=1; i<=daysOfMinus3; i++){
               
                history.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(i), State.ACCEPTED);
            }
            for(int i=1; i<=daysOfMinus2; i++){
                history.put(LocalDateTime.now().minusMonths(2).withDayOfMonth(i), State.ACCEPTED);
            }
            for(int i=1; i<=daysOfCurrentPayment; i++){
                if(i > 27){
                    history.put(LocalDateTime.now().minusMonths(1).withDayOfMonth(i), State.REJECTED);
                }else{
                    history.put(LocalDateTime.now().minusMonths(1).withDayOfMonth(i), State.ACCEPTED);
                }
            }
        } else {
            for(int i=1; i<=daysOfMinus3; i++){
                history.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(i), State.ACCEPTED);
                dailyValue.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(i), BigDecimal.valueOf(15));
            }
            for(int i=1; i<=daysOfMinus2; i++){
                history.put(LocalDateTime.now().minusMonths(2).withDayOfMonth(i), State.ACCEPTED);
                dailyValue.put(LocalDateTime.now().minusMonths(2).withDayOfMonth(i), BigDecimal.valueOf(15));
            }
            for(int i=1; i<=daysOfCurrentPayment; i++){
                history.put(LocalDateTime.now().minusMonths(1).withDayOfMonth(i), State.ACCEPTED);
                dailyValue.put(LocalDateTime.now().minusMonths(1).withDayOfMonth(i), BigDecimal.valueOf(15));
            }
            for(int i=1; i<currentDate.getDayOfMonth(); i++){
                history.put(currentDate.withDayOfMonth(i), State.ACCEPTED);
                dailyValue.put(currentDate.withDayOfMonth(i), BigDecimal.valueOf(15));

            }
        }
        
        monitoredCase.setHistory(history);
        List<CasePayment> paymentHistory = new ArrayList<>();
        CasePayment payment1 = new CasePayment();  
        payment1.setPayment(BigDecimal.valueOf(150.00));
        payment1.setPaymentDate(LocalDateTime.now().minusMonths(2).withDayOfMonth(1));
        payment1.setState(State.PAID);
        CasePayment payment2 = new CasePayment();    
        payment2.setPayment(BigDecimal.valueOf(150.00));
        payment2.setPaymentDate(LocalDateTime.now().minusMonths(1).withDayOfMonth(1));
        payment2.setState(State.PAID);
        // CasePayment payment3 = new CasePayment();    
        // payment3.setPayment(BigDecimal.valueOf(150.00));
        // payment3.setPaymentDate(LocalDateTime.of(2020, 10, 1, 0, 0, 2));
        // payment3.setState(State.PAID);
        paymentHistory.add(payment1);
        paymentHistory.add(payment2);
        //paymentHistory.add(payment3);
        monitoredCase.setPaymentHistory(paymentHistory);
        monitoredCase.setOffset(BigDecimal.ZERO);

        return monitoredCase;

    }

    public SsiApplication generateSsiAppAltered1(){
        SsiApplication ssiApp = new SsiApplication();
        ssiApp.setOtherBenefitsR("150");
        LinkedHashMap<String, String> otherBenHistory = new LinkedHashMap<>();
        otherBenHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(3).withDayOfMonth(1)), "100");
        //otherBenHistory.put(LocalDateTime.of(2020, 6, 15, 1, 1, 1), "550");
        otherBenHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(2).withDayOfMonth(7)), "150");
        ssiApp.setOtherBenefitsRHistory(otherBenHistory);
        // ssiApp.setUnemploymentBenefitR("5");
        // ssiApp.setErgomeR("5");
        // ssiApp.setRentIncomeR("5");
        // ssiApp.setOtherIncomeR("5");
        ssiApp.setUuid("2WiYi1");
        ssiApp.setLuxury("false");
        ssiApp.setTotalIncome("10");
        ssiApp.setHospitalized("true");
        ssiApp.setUnemployed("true");
        ssiApp.setEmploymentStatus("unemployed");
        ssiApp.setTaxisAfm("123456");
        //ssiApp.setSalariesR("0.5");
        ssiApp.setPensionsR("650");
        ssiApp.setTaxisDateOfBirth(DateUtils.dateToString(LocalDate.of(1953, 5, 5)));
        ssiApp.setMeterNumber("123456789");
        ssiApp.setIban("iban123456");

        LinkedHashMap<String, String> pensionHistory = new LinkedHashMap<>();
        pensionHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(3).withDayOfMonth(1)), "500");
        pensionHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(2).withDayOfMonth(12)), "600");
        pensionHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(1).withDayOfMonth(12)), "650");
        ssiApp.setPensionsRHistory(pensionHistory);
        
        HouseholdMember member1 = new HouseholdMember();
        member1.setAfm("123456");
        member1.setName("Jimmy");
        member1.setSurname("Page");
        member1.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1953, 5, 5)));
        HouseholdMember member2 = new HouseholdMember();
        member2.setAfm("678901");
        member2.setName("Richie");
        member2.setSurname("Blackmore");
        member2.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1960, 8, 12)));
        HouseholdMember member3 = new HouseholdMember();
        member3.setAfm("164582");
        member3.setName("James");
        member3.setSurname("Hetfield");
        member3.setDateOfBirth(DateUtils.dateToString(LocalDate.now().minusYears(18).minusMonths(2).withDayOfMonth(19)));
        HouseholdMember member4 = new HouseholdMember();
        member4.setAfm("789456");
        member4.setName("Rory");
        member4.setSurname("Gallagher");
        member4.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1970, 10, 24)));
        HouseholdMember member5 = new HouseholdMember();
        member5.setAfm("456789");
        member5.setName("Jimmy");
        member5.setSurname("Hendrix");
        member5.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1956, 5, 14)));
        List<HouseholdMember> household = new ArrayList<>();
        household.add(member1);
        household.add(member2);
        household.add(member3);
        //household.add(member4);
        List<HouseholdMember> householdLatest = new ArrayList<>();
        householdLatest.add(member1);
        householdLatest.add(member2);
        householdLatest.add(member3);
        householdLatest.add(member5);

        ssiApp.setHouseholdPrincipal(member1);
        ssiApp.setHouseholdComposition(householdLatest);

        LinkedHashMap<String, List<HouseholdMember>> housholdHistory = new LinkedHashMap<>();
        List<HouseholdMember> householdH = new ArrayList<>();
        householdH.add(member1);
        householdH.add(member2);
        householdH.add(member3);
        householdH.add(member4);
        housholdHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(3).withDayOfMonth(1)), householdH);
        housholdHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(2).withDayOfMonth(11)), household);
        housholdHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(1).withDayOfMonth(16)), householdLatest);

        ssiApp.setHouseholdCompositionHistory(housholdHistory);

        return ssiApp;
    }

    public SsiApplication generateSsiAppAltered2(){
        SsiApplication ssiApp = new SsiApplication();
        ssiApp.setOtherBenefitsR("120");
        LinkedHashMap<String, String> otherBenHistory = new LinkedHashMap<>();
        otherBenHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(3).withDayOfMonth(1)), "150");
        //otherBenHistory.put(LocalDateTime.of(2020, 6, 15, 1, 1, 1), "550");
        otherBenHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(3).withDayOfMonth(15)), "120");
        ssiApp.setOtherBenefitsRHistory(otherBenHistory);
        ssiApp.setLuxury("false");
        ssiApp.setTotalIncome("10");
        ssiApp.setHospitalized("true");
        ssiApp.setUnemployed("true");
        ssiApp.setEmploymentStatus("unemployed");
        ssiApp.setTaxisAfm("678901");
        //ssiApp.setSalariesR("2000");
        ssiApp.setPensionsR("700");
        ssiApp.setTaxisDateOfBirth(DateUtils.dateToString(LocalDate.of(1960, 8, 12)));

        LinkedHashMap<String, String> pensionHistory = new LinkedHashMap<>();
        pensionHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(3).withDayOfMonth(1)), "600");
        pensionHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(2).withDayOfMonth(5)), "700");
        ssiApp.setPensionsRHistory(pensionHistory);
        
        HouseholdMember member1 = new HouseholdMember();
        member1.setAfm("123456");
        member1.setName("Jimmy");
        member1.setSurname("Page");
        member1.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1953, 5, 5)));
        HouseholdMember member2 = new HouseholdMember();
        member2.setAfm("678901");
        member2.setName("Richie");
        member2.setSurname("Blackmore");
        member2.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1960, 8, 12)));
        HouseholdMember member3 = new HouseholdMember();
        member3.setAfm("164582");
        member3.setName("James");
        member3.setSurname("Hetfield");
        member3.setDateOfBirth(DateUtils.dateToString(LocalDate.now().minusYears(18).minusMonths(2).withDayOfMonth(19)));
        HouseholdMember member4 = new HouseholdMember();
        member4.setAfm("789456");
        member4.setName("Rory");
        member4.setSurname("Gallagher");
        member4.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1970, 10, 24)));
        HouseholdMember member5 = new HouseholdMember();
        member5.setAfm("456789");
        member5.setName("Jimmy");
        member5.setSurname("Hendrix");
        member5.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1956, 5, 14)));
        List<HouseholdMember> household = new ArrayList<>();
        household.add(member1);
        household.add(member2);
        household.add(member3);
        //household.add(member4);
        List<HouseholdMember> householdLatest = new ArrayList<>();
        householdLatest.add(member1);
        householdLatest.add(member2);
        householdLatest.add(member3);
        householdLatest.add(member5);

        ssiApp.setHouseholdPrincipal(member1);
        ssiApp.setHouseholdComposition(householdLatest);

        LinkedHashMap<String, List<HouseholdMember>> housholdHistory = new LinkedHashMap<>();
        List<HouseholdMember> householdH = new ArrayList<>();
        householdH.add(member1);
        householdH.add(member2);
        householdH.add(member3);
        householdH.add(member4);
        housholdHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(3).withDayOfMonth(1)), householdH);
        housholdHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(2).withDayOfMonth(11)), household);
        housholdHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(1).withDayOfMonth(16)), householdLatest);

        ssiApp.setHouseholdCompositionHistory(housholdHistory);

        return ssiApp;
    }

    public SsiApplication generateSsiAppAltered3(){
        SsiApplication ssiApp = new SsiApplication();
        ssiApp.setOtherBenefitsR("200");
        LinkedHashMap<String, String> otherBenHistory = new LinkedHashMap<>();
        otherBenHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(3).withDayOfMonth(1)), "150");
        //otherBenHistory.put(LocalDateTime.of(2020, 6, 15, 1, 1, 1), "550");
        otherBenHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(2).withDayOfMonth(7)), "200");
        ssiApp.setOtherBenefitsRHistory(otherBenHistory);
        ssiApp.setLuxury("false");
        ssiApp.setTotalIncome("10");
        ssiApp.setHospitalized("true");
        ssiApp.setUnemployed("true");
        ssiApp.setEmploymentStatus("unemployed");
        ssiApp.setTaxisAfm("164582");
        //ssiApp.setSalariesR("2000");
        ssiApp.setPensionsR("0");
        ssiApp.setTaxisDateOfBirth(DateUtils.dateToString(LocalDate.now().minusYears(18).minusMonths(2).withDayOfMonth(19)));

        LinkedHashMap<String, String> pensionHistory = new LinkedHashMap<>();
        pensionHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(3).withDayOfMonth(1)), "0");
        ssiApp.setPensionsRHistory(pensionHistory);
        
        HouseholdMember member1 = new HouseholdMember();
        member1.setAfm("123456");
        member1.setName("Jimmy");
        member1.setSurname("Page");
        member1.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1953, 5, 5)));
        HouseholdMember member2 = new HouseholdMember();
        member2.setAfm("678901");
        member2.setName("Richie");
        member2.setSurname("Blackmore");
        member2.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1960, 8, 12)));
        HouseholdMember member3 = new HouseholdMember();
        member3.setAfm("164582");
        member3.setName("James");
        member3.setSurname("Hetfield");
        member3.setDateOfBirth(DateUtils.dateToString(LocalDate.now().minusYears(18).minusMonths(2).withDayOfMonth(19)));
        HouseholdMember member4 = new HouseholdMember();
        member4.setAfm("789456");
        member4.setName("Rory");
        member4.setSurname("Gallagher");
        member4.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1970, 10, 24)));
        HouseholdMember member5 = new HouseholdMember();
        member5.setAfm("456789");
        member5.setName("Jimmy");
        member5.setSurname("Hendrix");
        member5.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1956, 5, 14)));
        List<HouseholdMember> household = new ArrayList<>();
        household.add(member1);
        household.add(member2);
        household.add(member3);
        //household.add(member4);
        List<HouseholdMember> householdLatest = new ArrayList<>();
        householdLatest.add(member1);
        householdLatest.add(member2);
        householdLatest.add(member3);
        householdLatest.add(member5);

        ssiApp.setHouseholdPrincipal(member1);
        ssiApp.setHouseholdComposition(householdLatest);

        LinkedHashMap<String, List<HouseholdMember>> housholdHistory = new LinkedHashMap<>();
        List<HouseholdMember> householdH = new ArrayList<>();
        householdH.add(member1);
        householdH.add(member2);
        householdH.add(member3);
        householdH.add(member4);
        housholdHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(3).withDayOfMonth(1)), householdH);
        housholdHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(2).withDayOfMonth(11)), household);
        housholdHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(1).withDayOfMonth(16)), householdLatest);

        ssiApp.setHouseholdCompositionHistory(housholdHistory);

        return ssiApp;
    }

    public SsiApplication generateSsiAppAltered4(){
        SsiApplication ssiApp = new SsiApplication();
        ssiApp.setOtherBenefitsR("150");
        LinkedHashMap<String, String> otherBenHistory = new LinkedHashMap<>();
        otherBenHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(3).withDayOfMonth(1)), "150");
        //otherBenHistory.put(LocalDateTime.of(2020, 6, 15, 1, 1, 1), "550");
        ssiApp.setOtherBenefitsRHistory(otherBenHistory);
        ssiApp.setLuxury("false");
        ssiApp.setTotalIncome("10");
        ssiApp.setHospitalized("true");
        ssiApp.setUnemployed("true");
        ssiApp.setEmploymentStatus("unemployed");
        ssiApp.setTaxisAfm("789456");
        //ssiApp.setSalariesR("2000");
        ssiApp.setPensionsR("200");
        ssiApp.setTaxisDateOfBirth(DateUtils.dateToString(LocalDate.of(1970, 10, 24)));

        LinkedHashMap<String, String> pensionHistory = new LinkedHashMap<>();
        pensionHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(3).withDayOfMonth(1)), "200");
        ssiApp.setPensionsRHistory(pensionHistory);
        
        HouseholdMember member1 = new HouseholdMember();
        member1.setAfm("123456");
        member1.setName("Jimmy");
        member1.setSurname("Page");
        member1.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1953, 5, 5)));
        HouseholdMember member2 = new HouseholdMember();
        member2.setAfm("678901");
        member2.setName("Richie");
        member2.setSurname("Blackmore");
        member2.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1960, 8, 12)));
        HouseholdMember member3 = new HouseholdMember();
        member3.setAfm("164582");
        member3.setName("James");
        member3.setSurname("Hetfield");
        member3.setDateOfBirth(DateUtils.dateToString(LocalDate.now().minusYears(18).minusMonths(2).withDayOfMonth(19)));
        HouseholdMember member4 = new HouseholdMember();
        member4.setAfm("789456");
        member4.setName("Rory");
        member4.setSurname("Gallagher");
        member4.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1970, 10, 24)));
        HouseholdMember member5 = new HouseholdMember();
        member5.setAfm("456789");
        member5.setName("Jimmy");
        member5.setSurname("Hendrix");
        member5.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1956, 5, 14)));
        List<HouseholdMember> household = new ArrayList<>();
        household.add(member1);
        household.add(member2);
        household.add(member3);
        //household.add(member4);
        List<HouseholdMember> householdLatest = new ArrayList<>();
        householdLatest.add(member1);
        householdLatest.add(member2);
        householdLatest.add(member3);
        householdLatest.add(member5);

        ssiApp.setHouseholdPrincipal(member1);
        ssiApp.setHouseholdComposition(householdLatest);

        LinkedHashMap<String, List<HouseholdMember>> housholdHistory = new LinkedHashMap<>();
        List<HouseholdMember> householdH = new ArrayList<>();
        householdH.add(member1);
        householdH.add(member2);
        householdH.add(member3);
        householdH.add(member4);
        housholdHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(3).withDayOfMonth(1)), householdH);
        housholdHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(2).withDayOfMonth(11)), household);
        housholdHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(1).withDayOfMonth(16)), householdLatest);

        ssiApp.setHouseholdCompositionHistory(housholdHistory);

        return ssiApp;
    }

    public SsiApplication generateSsiAppAltered5(){
        SsiApplication ssiApp = new SsiApplication();
        ssiApp.setOtherBenefitsR("100");
        LinkedHashMap<String , String> otherBenHistory = new LinkedHashMap<>();
        otherBenHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(3).withDayOfMonth(1)), "100");
        //otherBenHistory.put(LocalDateTime.of(2020, 6, 15, 1, 1, 1), "550");
        ssiApp.setOtherBenefitsRHistory(otherBenHistory);
        ssiApp.setLuxury("false");
        ssiApp.setTotalIncome("10");
        ssiApp.setHospitalized("true");
        ssiApp.setUnemployed("true");
        ssiApp.setEmploymentStatus("unemployed");
        ssiApp.setTaxisAfm("456789");
        //ssiApp.setSalariesR("2000");
        ssiApp.setPensionsR("100");
        ssiApp.setTaxisDateOfBirth(DateUtils.dateToString(LocalDate.of(1956, 5, 14)));

        LinkedHashMap<String, String> pensionHistory = new LinkedHashMap<>();
        pensionHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(3).withDayOfMonth(1)), "100");
        ssiApp.setPensionsRHistory(pensionHistory);
        
        HouseholdMember member1 = new HouseholdMember();
        member1.setAfm("123456");
        member1.setName("Jimmy");
        member1.setSurname("Page");
        member1.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1953, 5, 5)));
        HouseholdMember member2 = new HouseholdMember();
        member2.setAfm("678901");
        member2.setName("Richie");
        member2.setSurname("Blackmore");
        member2.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1960, 8, 12)));
        HouseholdMember member3 = new HouseholdMember();
        member3.setAfm("164582");
        member3.setName("James");
        member3.setSurname("Hetfield");
        member3.setDateOfBirth(DateUtils.dateToString(LocalDate.now().minusYears(18).minusMonths(2).withDayOfMonth(19)));
        HouseholdMember member4 = new HouseholdMember();
        member4.setAfm("789456");
        member4.setName("Rory");
        member4.setSurname("Gallagher");
        member4.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1970, 10, 24)));
        HouseholdMember member5 = new HouseholdMember();
        member5.setAfm("456789");
        member5.setName("Jimmy");
        member5.setSurname("Hendrix");
        member5.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1956, 5, 14)));
        List<HouseholdMember> household = new ArrayList<>();
        household.add(member1);
        household.add(member2);
        household.add(member3);
        //household.add(member4);
        List<HouseholdMember> householdLatest = new ArrayList<>();
        householdLatest.add(member1);
        householdLatest.add(member2);
        householdLatest.add(member3);
        householdLatest.add(member5);

        ssiApp.setHouseholdPrincipal(member1);
        ssiApp.setHouseholdComposition(householdLatest);

        LinkedHashMap<String , List<HouseholdMember>> housholdHistory = new LinkedHashMap<>();
        List<HouseholdMember> householdH = new ArrayList<>();
        householdH.add(member1);
        householdH.add(member2);
        householdH.add(member3);
        householdH.add(member4);
        housholdHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(3).withDayOfMonth(1)), householdH);
        housholdHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(2).withDayOfMonth(11)), household);
        housholdHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(1).withDayOfMonth(16)), householdLatest);

        ssiApp.setHouseholdCompositionHistory(housholdHistory);

        return ssiApp;
    }

    public SsiApplication generateSsiApp1(){
        SsiApplication ssiApp = new SsiApplication();
        ssiApp.setOtherBenefitsR("150");
        LinkedHashMap<String, String> otherBenHistory = new LinkedHashMap<>();
        otherBenHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(3).withDayOfMonth(1)), "150");
        ssiApp.setOtherBenefitsRHistory(otherBenHistory);
        // ssiApp.setUnemploymentBenefitR("5");
        // ssiApp.setErgomeR("5");
        // ssiApp.setRentIncomeR("5");
        // ssiApp.setOtherIncomeR("5");
        ssiApp.setUuid("2WiYi1");
        ssiApp.setLuxury("false");
        ssiApp.setTotalIncome("10");
        ssiApp.setHospitalized("true");
        ssiApp.setUnemployed("true");
        ssiApp.setEmploymentStatus("unemployed");
        ssiApp.setTaxisAfm("123456");
        //ssiApp.setSalariesR("0.5");
        ssiApp.setPensionsR("500");
        ssiApp.setTaxisDateOfBirth(DateUtils.dateToString(LocalDate.of(1953, 5, 5)));
        ssiApp.setMeterNumber("123456789");
        ssiApp.setIban("iban123456");
        ssiApp.setTime(LocalDate.now().minusMonths(3));

        LinkedHashMap<String , String> pensionHistory = new LinkedHashMap<>();
        pensionHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(3).withDayOfMonth(1)), "500");
        ssiApp.setPensionsRHistory(pensionHistory);
        
        HouseholdMember member1 = new HouseholdMember();
        member1.setAfm("123456");
        member1.setName("Jimmy");
        member1.setSurname("Page");
        member1.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1953, 5, 5)));
        HouseholdMember member2 = new HouseholdMember();
        member2.setAfm("678901");
        member2.setName("Richie");
        member2.setSurname("Blackmore");
        member2.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1960, 9, 12)));
        HouseholdMember member3 = new HouseholdMember();
        member3.setAfm("164582");
        member3.setName("James");
        member3.setSurname("Hetfield");
        member3.setDateOfBirth(DateUtils.dateToString(LocalDate.now().minusYears(18).minusMonths(2).withDayOfMonth(19)));
        List<HouseholdMember> household = new ArrayList<>();
        household.add(member1);
        household.add(member2);
        household.add(member3);

        ssiApp.setHouseholdPrincipal(member1);
        ssiApp.setHouseholdComposition(household);

        LinkedHashMap<String, List<HouseholdMember>> housholdHistory = new LinkedHashMap<>();

        housholdHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(3).withDayOfMonth(1)), household);

        ssiApp.setHouseholdCompositionHistory(housholdHistory);

        return ssiApp;
    }

    public SsiApplication generateSsiApp2(){
        SsiApplication ssiApp = new SsiApplication();
        ssiApp.setOtherBenefitsR("120");
        LinkedHashMap<String, String> otherBenHistory = new LinkedHashMap<>();
        otherBenHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(3).withDayOfMonth(1)), "120");
        ssiApp.setOtherBenefitsRHistory(otherBenHistory);
        ssiApp.setLuxury("false");
        ssiApp.setTotalIncome("10");
        ssiApp.setHospitalized("true");
        ssiApp.setUnemployed("true");
        ssiApp.setEmploymentStatus("unemployed");
        ssiApp.setTaxisAfm("678901");
        //ssiApp.setSalariesR("2000");
        ssiApp.setPensionsR("700");
        ssiApp.setTaxisDateOfBirth(DateUtils.dateToString(LocalDate.of(1960, 9, 12)));
        ssiApp.setTime(LocalDate.now().minusMonths(3));

        LinkedHashMap<String, String> pensionHistory = new LinkedHashMap<>();
        pensionHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(3).withDayOfMonth(1)), "700");
        ssiApp.setPensionsRHistory(pensionHistory);
        
        HouseholdMember member1 = new HouseholdMember();
        member1.setAfm("123456");
        member1.setName("Jimmy");
        member1.setSurname("Page");
        member1.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1953, 5, 5)));
        HouseholdMember member2 = new HouseholdMember();
        member2.setAfm("678901");
        member2.setName("Richie");
        member2.setSurname("Blackmore");
        member2.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1960, 9, 12)));
        HouseholdMember member3 = new HouseholdMember();
        member3.setAfm("164582");
        member3.setName("James");
        member3.setSurname("Hetfield");
        member3.setDateOfBirth(DateUtils.dateToString(LocalDate.now().minusYears(18).minusMonths(2).withDayOfMonth(19)));
        List<HouseholdMember> household = new ArrayList<>();
        household.add(member1);
        household.add(member2);
        household.add(member3);

        ssiApp.setHouseholdPrincipal(member1);
        ssiApp.setHouseholdComposition(household);

        LinkedHashMap<String, List<HouseholdMember>> housholdHistory = new LinkedHashMap<>();
        housholdHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(3).withDayOfMonth(1)), household);

        ssiApp.setHouseholdCompositionHistory(housholdHistory);

        return ssiApp;
    }

    public SsiApplication generateSsiApp3(){
        SsiApplication ssiApp = new SsiApplication();
        ssiApp.setOtherBenefitsR("200");
        LinkedHashMap<String , String> otherBenHistory = new LinkedHashMap<>();
        otherBenHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(3).withDayOfMonth(1)), "200");
        ssiApp.setOtherBenefitsRHistory(otherBenHistory);
        ssiApp.setLuxury("false");
        ssiApp.setTotalIncome("10");
        ssiApp.setHospitalized("true");
        ssiApp.setUnemployed("true");
        ssiApp.setEmploymentStatus("unemployed");
        ssiApp.setTaxisAfm("164582");
        //ssiApp.setSalariesR("2000");
        ssiApp.setPensionsR("0");
        ssiApp.setTaxisDateOfBirth(DateUtils.dateToString(LocalDate.now().minusYears(18).minusMonths(2).withDayOfMonth(19)));
        ssiApp.setTime(LocalDate.now().minusMonths(3));

        LinkedHashMap<String, String> pensionHistory = new LinkedHashMap<>();
        pensionHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(3).withDayOfMonth(1)), "0");
        ssiApp.setPensionsRHistory(pensionHistory);
        
        HouseholdMember member1 = new HouseholdMember();
        member1.setAfm("123456");
        member1.setName("Jimmy");
        member1.setSurname("Page");
        member1.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1953, 5, 5)));
        HouseholdMember member2 = new HouseholdMember();
        member2.setAfm("678901");
        member2.setName("Richie");
        member2.setSurname("Blackmore");
        member2.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1960, 9, 12)));
        HouseholdMember member3 = new HouseholdMember();
        member3.setAfm("164582");
        member3.setName("James");
        member3.setSurname("Hetfield");
        member3.setDateOfBirth(DateUtils.dateToString(LocalDate.now().minusYears(18).minusMonths(2).withDayOfMonth(19)));
        List<HouseholdMember> household = new ArrayList<>();
        household.add(member1);
        household.add(member2);
        household.add(member3);

        ssiApp.setHouseholdPrincipal(member1);
        ssiApp.setHouseholdComposition(household);

        LinkedHashMap<String, List<HouseholdMember>> housholdHistory = new LinkedHashMap<>();
        List<HouseholdMember> householdH = new ArrayList<>();
        householdH.add(member1);
        householdH.add(member2);
        householdH.add(member3);
        housholdHistory.put(DateUtils.dateToString(LocalDateTime.now().minusMonths(3).withDayOfMonth(1)), household);

        ssiApp.setHouseholdCompositionHistory(housholdHistory);

        return ssiApp;
    }

    public Integer monthDays(LocalDateTime date) {

        int month = date.getMonthValue();
        int year = date.getYear();
        int numDays = 0;

        switch (month) {
            case 1: case 3: case 5:
            case 7: case 8: case 10:
            case 12:
                numDays = 31;
                break;
            case 4: case 6:
            case 9: case 11:
                numDays = 30;
                break;
            case 2:
                if (((year % 4 == 0) && 
                     !(year % 100 == 0))
                     || (year % 400 == 0))
                    numDays = 29;
                else
                    numDays = 28;
                break;
            default:
                log.error("Invalid month.");
                break;
        }
        return numDays;
    }
    
}
