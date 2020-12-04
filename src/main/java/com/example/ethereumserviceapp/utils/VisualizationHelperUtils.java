package com.example.ethereumserviceapp.utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.example.ethereumserviceapp.model.HouseholdMember;
import com.example.ethereumserviceapp.model.entities.SsiApplication;

public class VisualizationHelperUtils {

    public static SsiApplication generateSsiAppAltered1(){
        SsiApplication ssiApp = new SsiApplication();
        ssiApp.setOtherBenefitsR("150");
        LinkedHashMap<LocalDateTime, String> otherBenHistory = new LinkedHashMap<>();
        otherBenHistory.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(1).withNano(0), "100");
        //otherBenHistory.put(LocalDateTime.of(2020, 6, 15, 1, 1, 1), "550");
        otherBenHistory.put(LocalDateTime.now().minusMonths(2).withDayOfMonth(7).withNano(0), "150");
        ssiApp.setOtherBenefitsRHistory(otherBenHistory);
        // ssiApp.setUnemploymentBenefitR("5");
        // ssiApp.setErgomeR("5");
        // ssiApp.setRentIncomeR("5");
        // ssiApp.setOtherIncomeR("5");
        ssiApp.setUuid("1SiYd6");
        ssiApp.setLuxury("false");
        ssiApp.setTotalIncome("10");
        ssiApp.setHospitalized("true");
        ssiApp.setUnemployed("true");
        ssiApp.setEmploymentStatus("unemployed");
        ssiApp.setTaxisAfm("123456");
        //ssiApp.setSalariesR("0.5");
        ssiApp.setPensionsR("650");
        ssiApp.setTaxisDateOfBirth("05/05/1953");
        ssiApp.setMeterNumber("123456789");
        ssiApp.setIban("iban123456");

        LinkedHashMap<LocalDateTime, String> pensionHistory = new LinkedHashMap<>();
        pensionHistory.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(1).withNano(0), "500");
        pensionHistory.put(LocalDateTime.now().minusMonths(2).withDayOfMonth(12).withNano(0), "600");
        pensionHistory.put(LocalDateTime.now().minusMonths(1).withDayOfMonth(12).withNano(0), "650");
        ssiApp.setPensionsRHistory(pensionHistory);
        
        HouseholdMember member1 = new HouseholdMember();
        member1.setAfm("123456");
        member1.setName("Jimmy");
        member1.setSurname("Page");
        member1.setDateOfBirth("05/05/1953");
        HouseholdMember member2 = new HouseholdMember();
        member2.setAfm("678901");
        member2.setName("Richie");
        member2.setSurname("Blackmore");
        member2.setDateOfBirth("12/08/1960");
        HouseholdMember member3 = new HouseholdMember();
        member3.setAfm("164582");
        member3.setName("James");
        member3.setSurname("Hetfield");
        member3.setDateOfBirth("19/09/2002");
        HouseholdMember member4 = new HouseholdMember();
        member4.setAfm("789456");
        member4.setName("Rory");
        member4.setSurname("Gallagher");
        member4.setDateOfBirth("24/10/1970");
        HouseholdMember member5 = new HouseholdMember();
        member5.setAfm("456789");
        member5.setName("Jimmy");
        member5.setSurname("Hendrix");
        member5.setDateOfBirth("14/05/1956");
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

        LinkedHashMap<LocalDateTime, List<HouseholdMember>> housholdHistory = new LinkedHashMap<>();
        List<HouseholdMember> householdH = new ArrayList<>();
        householdH.add(member1);
        householdH.add(member2);
        householdH.add(member3);
        householdH.add(member4);
        housholdHistory.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(1).withNano(0), householdH);
        housholdHistory.put(LocalDateTime.now().minusMonths(2).withDayOfMonth(11).withNano(0), household);
        housholdHistory.put(LocalDateTime.now().minusMonths(1).withDayOfMonth(16).withNano(0), householdLatest);

        ssiApp.setHouseholdCompositionHistory(housholdHistory);

        return ssiApp;
    }

    public static SsiApplication generateSsiAppAltered2(){
        SsiApplication ssiApp = new SsiApplication();
        ssiApp.setOtherBenefitsR("120");
        LinkedHashMap<LocalDateTime, String> otherBenHistory = new LinkedHashMap<>();
        otherBenHistory.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(1).withNano(0), "150");
        //otherBenHistory.put(LocalDateTime.of(2020, 6, 15, 1, 1, 1), "550");
        otherBenHistory.put(LocalDateTime.now().minusMonths(2).withDayOfMonth(7).withNano(0), "120");
        ssiApp.setOtherBenefitsRHistory(otherBenHistory);
        ssiApp.setLuxury("false");
        ssiApp.setTotalIncome("10");
        ssiApp.setHospitalized("true");
        ssiApp.setUnemployed("true");
        ssiApp.setEmploymentStatus("unemployed");
        ssiApp.setTaxisAfm("678901");
        //ssiApp.setSalariesR("2000");
        ssiApp.setPensionsR("700");
        ssiApp.setTaxisDateOfBirth("12/08/1960");
        ssiApp.setUuid("2WiYi1");

        LinkedHashMap<LocalDateTime, String> pensionHistory = new LinkedHashMap<>();
        pensionHistory.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(1).withNano(0), "600");
        pensionHistory.put(LocalDateTime.now().minusMonths(2).withDayOfMonth(5).withNano(0), "700");
        ssiApp.setPensionsRHistory(pensionHistory);
        
        HouseholdMember member1 = new HouseholdMember();
        member1.setAfm("123456");
        member1.setName("Jimmy");
        member1.setSurname("Page");
        member1.setDateOfBirth("05/05/1953");
        HouseholdMember member2 = new HouseholdMember();
        member2.setAfm("678901");
        member2.setName("Richie");
        member2.setSurname("Blackmore");
        member2.setDateOfBirth("12/08/1960");
        HouseholdMember member3 = new HouseholdMember();
        member3.setAfm("164582");
        member3.setName("James");
        member3.setSurname("Hetfield");
        member3.setDateOfBirth("19/09/2002");
        HouseholdMember member4 = new HouseholdMember();
        member4.setAfm("789456");
        member4.setName("Rory");
        member4.setSurname("Gallagher");
        member4.setDateOfBirth("24/10/1970");
        HouseholdMember member5 = new HouseholdMember();
        member5.setAfm("456789");
        member5.setName("Jimmy");
        member5.setSurname("Hendrix");
        member5.setDateOfBirth("14/05/1956");
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

        LinkedHashMap<LocalDateTime, List<HouseholdMember>> housholdHistory = new LinkedHashMap<>();
        List<HouseholdMember> householdH = new ArrayList<>();
        householdH.add(member1);
        householdH.add(member2);
        householdH.add(member3);
        householdH.add(member4);
        housholdHistory.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(1).withNano(0), householdH);
        housholdHistory.put(LocalDateTime.now().minusMonths(2).withDayOfMonth(11).withNano(0), household);
        housholdHistory.put(LocalDateTime.now().minusMonths(1).withDayOfMonth(16).withNano(0), householdLatest);

        ssiApp.setHouseholdCompositionHistory(housholdHistory);

        return ssiApp;
    }

    public static SsiApplication generateSsiAppAltered3(){
        SsiApplication ssiApp = new SsiApplication();
        ssiApp.setOtherBenefitsR("200");
        LinkedHashMap<LocalDateTime, String> otherBenHistory = new LinkedHashMap<>();
        otherBenHistory.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(1).withNano(0), "150");
        //otherBenHistory.put(LocalDateTime.of(2020, 6, 15, 1, 1, 1), "550");
        otherBenHistory.put(LocalDateTime.now().minusMonths(2).withDayOfMonth(7).withNano(0), "200");
        ssiApp.setOtherBenefitsRHistory(otherBenHistory);
        ssiApp.setLuxury("false");
        ssiApp.setTotalIncome("10");
        ssiApp.setHospitalized("true");
        ssiApp.setUnemployed("true");
        ssiApp.setEmploymentStatus("unemployed");
        ssiApp.setTaxisAfm("164582");
        //ssiApp.setSalariesR("2000");
        ssiApp.setPensionsR("0");
        ssiApp.setTaxisDateOfBirth("19/09/2002");
        ssiApp.setUuid("2WiYi2");

        LinkedHashMap<LocalDateTime, String> pensionHistory = new LinkedHashMap<>();
        pensionHistory.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(1).withNano(0), "0");
        ssiApp.setPensionsRHistory(pensionHistory);
        
        HouseholdMember member1 = new HouseholdMember();
        member1.setAfm("123456");
        member1.setName("Jimmy");
        member1.setSurname("Page");
        member1.setDateOfBirth("05/05/1953");
        HouseholdMember member2 = new HouseholdMember();
        member2.setAfm("678901");
        member2.setName("Richie");
        member2.setSurname("Blackmore");
        member2.setDateOfBirth("12/08/1960");
        HouseholdMember member3 = new HouseholdMember();
        member3.setAfm("164582");
        member3.setName("James");
        member3.setSurname("Hetfield");
        member3.setDateOfBirth("19/09/2002");
        HouseholdMember member4 = new HouseholdMember();
        member4.setAfm("789456");
        member4.setName("Rory");
        member4.setSurname("Gallagher");
        member4.setDateOfBirth("24/10/1970");
        HouseholdMember member5 = new HouseholdMember();
        member5.setAfm("456789");
        member5.setName("Jimmy");
        member5.setSurname("Hendrix");
        member5.setDateOfBirth("14/05/1956");
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

        LinkedHashMap<LocalDateTime, List<HouseholdMember>> housholdHistory = new LinkedHashMap<>();
        List<HouseholdMember> householdH = new ArrayList<>();
        householdH.add(member1);
        householdH.add(member2);
        householdH.add(member3);
        householdH.add(member4);
        housholdHistory.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(1).withNano(0), householdH);
        housholdHistory.put(LocalDateTime.now().minusMonths(2).withDayOfMonth(11).withNano(0), household);
        housholdHistory.put(LocalDateTime.now().minusMonths(1).withDayOfMonth(16).withNano(0), householdLatest);

        ssiApp.setHouseholdCompositionHistory(housholdHistory);

        return ssiApp;
    }

    public static SsiApplication generateSsiAppAltered4(){
        SsiApplication ssiApp = new SsiApplication();
        ssiApp.setOtherBenefitsR("150");
        LinkedHashMap<LocalDateTime, String> otherBenHistory = new LinkedHashMap<>();
        otherBenHistory.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(1).withNano(0), "150");
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
        ssiApp.setTaxisDateOfBirth("24/10/1970");
        ssiApp.setUuid("2WiYi3");

        LinkedHashMap<LocalDateTime, String> pensionHistory = new LinkedHashMap<>();
        pensionHistory.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(1).withNano(0), "200");
        ssiApp.setPensionsRHistory(pensionHistory);
        
        HouseholdMember member1 = new HouseholdMember();
        member1.setAfm("123456");
        member1.setName("Jimmy");
        member1.setSurname("Page");
        member1.setDateOfBirth("05/05/1953");
        HouseholdMember member2 = new HouseholdMember();
        member2.setAfm("678901");
        member2.setName("Richie");
        member2.setSurname("Blackmore");
        member2.setDateOfBirth("12/08/1960");
        HouseholdMember member3 = new HouseholdMember();
        member3.setAfm("164582");
        member3.setName("James");
        member3.setSurname("Hetfield");
        member3.setDateOfBirth("19/09/2002");
        HouseholdMember member4 = new HouseholdMember();
        member4.setAfm("789456");
        member4.setName("Rory");
        member4.setSurname("Gallagher");
        member4.setDateOfBirth("24/10/1970");
        HouseholdMember member5 = new HouseholdMember();
        member5.setAfm("456789");
        member5.setName("Jimmy");
        member5.setSurname("Hendrix");
        member5.setDateOfBirth("14/05/1956");
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

        LinkedHashMap<LocalDateTime, List<HouseholdMember>> housholdHistory = new LinkedHashMap<>();
        List<HouseholdMember> householdH = new ArrayList<>();
        householdH.add(member1);
        householdH.add(member2);
        householdH.add(member3);
        householdH.add(member4);
        housholdHistory.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(1).withNano(0), householdH);
        housholdHistory.put(LocalDateTime.now().minusMonths(2).withDayOfMonth(11).withNano(0), household);
        housholdHistory.put(LocalDateTime.now().minusMonths(1).withDayOfMonth(16).withNano(0), householdLatest);

        ssiApp.setHouseholdCompositionHistory(housholdHistory);

        return ssiApp;
    }

    public static SsiApplication generateSsiAppAltered5(){
        SsiApplication ssiApp = new SsiApplication();
        ssiApp.setOtherBenefitsR("100");
        LinkedHashMap<LocalDateTime, String> otherBenHistory = new LinkedHashMap<>();
        otherBenHistory.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(1).withNano(0), "100");
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
        ssiApp.setTaxisDateOfBirth("14/05/1956");
        ssiApp.setUuid("2WiYi4");

        LinkedHashMap<LocalDateTime, String> pensionHistory = new LinkedHashMap<>();
        pensionHistory.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(1).withNano(0), "100");
        ssiApp.setPensionsRHistory(pensionHistory);
        
        HouseholdMember member1 = new HouseholdMember();
        member1.setAfm("123456");
        member1.setName("Jimmy");
        member1.setSurname("Page");
        member1.setDateOfBirth("05/05/1953");
        HouseholdMember member2 = new HouseholdMember();
        member2.setAfm("678901");
        member2.setName("Richie");
        member2.setSurname("Blackmore");
        member2.setDateOfBirth("12/08/1960");
        HouseholdMember member3 = new HouseholdMember();
        member3.setAfm("164582");
        member3.setName("James");
        member3.setSurname("Hetfield");
        member3.setDateOfBirth("19/09/2002");
        HouseholdMember member4 = new HouseholdMember();
        member4.setAfm("789456");
        member4.setName("Rory");
        member4.setSurname("Gallagher");
        member4.setDateOfBirth("24/10/1970");
        HouseholdMember member5 = new HouseholdMember();
        member5.setAfm("456789");
        member5.setName("Jimmy");
        member5.setSurname("Hendrix");
        member5.setDateOfBirth("14/05/1956");
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

        LinkedHashMap<LocalDateTime, List<HouseholdMember>> housholdHistory = new LinkedHashMap<>();
        List<HouseholdMember> householdH = new ArrayList<>();
        householdH.add(member1);
        householdH.add(member2);
        householdH.add(member3);
        householdH.add(member4);
        housholdHistory.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(1).withNano(0), householdH);
        housholdHistory.put(LocalDateTime.now().minusMonths(2).withDayOfMonth(11).withNano(0), household);
        housholdHistory.put(LocalDateTime.now().minusMonths(1).withDayOfMonth(16).withNano(0), householdLatest);

        ssiApp.setHouseholdCompositionHistory(housholdHistory);

        return ssiApp;
    }
    
}
