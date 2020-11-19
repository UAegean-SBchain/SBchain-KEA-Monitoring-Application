package com.example.ethereumserviceapp;

import static org.mockito.ArgumentMatchers.anySet;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.CasePayment;
import com.example.ethereumserviceapp.model.HouseholdMember;
import com.example.ethereumserviceapp.model.State;
import com.example.ethereumserviceapp.model.entities.SsiApplication;
import com.example.ethereumserviceapp.service.MongoService;
import com.example.ethereumserviceapp.utils.MonitorUtils;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class TestMonitorUtils {

    @Mock
    MongoService mongoServ;

    @Test
    public void testOffsetPayment(){

        Case monitoredCase = generateMockCase("2WiYi1", State.ACCEPTED);
        SsiApplication ssiApp1 = generateSsiApp1();
        SsiApplication ssiApp2 = generateSsiApp2();
        SsiApplication ssiApp3 = generateSsiApp3();
        SsiApplication ssiApp4 = generateSsiApp4();
        SsiApplication ssiApp5 = generateSsiApp5();

        List<SsiApplication> ssiApps = new ArrayList<>();
        ssiApps.add(ssiApp1);
        ssiApps.add(ssiApp2);
        ssiApps.add(ssiApp3);
        ssiApps.add(ssiApp4);
        ssiApps.add(ssiApp5);


        //Mockito.when(mongoServ.findByTaxisAfmIn(anySet())).thenReturn(ssiApps);

        //log.info("ssiApp before :{}", ssiApp);

        MonitorUtils.calculateOffset(monitoredCase, ssiApp1, ssiApps);

        log.info("xxxxxxxxxxxxxxxxxxx offset :{}", monitoredCase.getOffset());
        //log.info("ssiApp after :{}", ssiApp);

    }

    @Test
    public void testCalcualtePaymentWithoutOffset(){

        Case monitoredCase = generateMockCase("2WiYi1", State.ACCEPTED);
        SsiApplication ssiApp1 = generateSsiApp1();
        SsiApplication ssiApp2 = generateSsiApp2();
        SsiApplication ssiApp3 = generateSsiApp3();
        SsiApplication ssiApp4 = generateSsiApp4();
        SsiApplication ssiApp5 = generateSsiApp5();

        List<SsiApplication> ssiApps = new ArrayList<>();
        ssiApps.add(ssiApp1);
        ssiApps.add(ssiApp2);
        ssiApps.add(ssiApp3);
        ssiApps.add(ssiApp4);
        ssiApps.add(ssiApp5);

        BigDecimal payment = MonitorUtils.calculateCurrentPayment(monitoredCase, ssiApp1, ssiApps);

        log.info("xxxxxxxxxxxxxxxxxxx payment :{}", payment);
        //log.info("ssiApp after :{}", ssiApp);

    }

    private Case generateMockCase(String uuid, State state){
        
        Case monitoredCase = new Case();
        monitoredCase.setUuid(uuid);
        monitoredCase.setDate(LocalDateTime.now().minusDays(1));
        monitoredCase.setState(state);
        LinkedHashMap<LocalDateTime, State> history = new LinkedHashMap<>();
        Integer daysOfCurrentPayment = monthDays(LocalDateTime.now().minusMonths(1));
        Integer daysOfMinus2 = monthDays(LocalDateTime.now().minusMonths(2));
        Integer daysOfMinus3 = monthDays(LocalDateTime.now().minusMonths(3));

        // if(allRejected && state.equals(State.REJECTED)){
        //     for(int i=0; i<days; i++){
        //         history.put(LocalDateTime.now().minusDays(days-i), State.REJECTED);
        //     }
        // } else if(!allRejected && state.equals(State.REJECTED)) {
        //     for(int i=0; i<days; i++){
        //         if(i > 15){
        //             history.put(LocalDateTime.now().minusDays(days-i), State.ACCEPTED);
        //         }else{
        //             history.put(LocalDateTime.now().minusDays(days-i), State.REJECTED);
        //         }
        //     }
        // } else {
        //}
        for(int i=1; i<=daysOfMinus3; i++){
            history.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(i), State.ACCEPTED);
        }
        for(int i=1; i<=daysOfMinus2; i++){
            history.put(LocalDateTime.now().minusMonths(2).withDayOfMonth(i), State.ACCEPTED);
        }
        for(int i=1; i<=daysOfCurrentPayment; i++){
            history.put(LocalDateTime.now().minusMonths(1).withDayOfMonth(i), State.ACCEPTED);
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

    private SsiApplication generateSsiApp1(){
        SsiApplication ssiApp = new SsiApplication();
        ssiApp.setOtherBenefitsR("150");
        LinkedHashMap<LocalDateTime, String> otherBenHistory = new LinkedHashMap<>();
        otherBenHistory.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(1), "100");
        //otherBenHistory.put(LocalDateTime.of(2020, 6, 15, 1, 1, 1), "550");
        otherBenHistory.put(LocalDateTime.now().minusMonths(2).withDayOfMonth(7), "150");
        ssiApp.setOtherBenefitsRHistory(otherBenHistory);
        // ssiApp.setUnemploymentBenefitR("5");
        // ssiApp.setErgomeR("5");
        // ssiApp.setRentIncomeR("5");
        // ssiApp.setOtherIncomeR("5");
        //ssiApp.setUuid(uuid);
        ssiApp.setLuxury("false");
        ssiApp.setTotalIncome("10");
        ssiApp.setHospitalized("true");
        ssiApp.setUnemployed("true");
        ssiApp.setEmploymentStatus("unemployed");
        ssiApp.setTaxisAfm("123456");
        //ssiApp.setSalariesR("0.5");
        ssiApp.setPensionsR("650");

        LinkedHashMap<LocalDateTime, String> pensionHistory = new LinkedHashMap<>();
        pensionHistory.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(1), "500");
        pensionHistory.put(LocalDateTime.now().minusMonths(2).withDayOfMonth(12), "600");
        pensionHistory.put(LocalDateTime.now().minusMonths(1).withDayOfMonth(12), "650");
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
        member3.setDateOfBirth("19/10/2002");
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
        housholdHistory.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(1), householdH);
        housholdHistory.put(LocalDateTime.now().minusMonths(2).withDayOfMonth(11), household);
        housholdHistory.put(LocalDateTime.now().minusMonths(1).withDayOfMonth(16), householdLatest);

        ssiApp.setHouseholdCompositionHistory(housholdHistory);
        //ssiApp.setFreelanceR("500");
        //ssiApp.setDepositsA("50");
        //ssiApp.setOtherBenefitsR("480");
        // Map<String, String> householdVal1 = new HashMap<>();//34.01
        // householdVal1.put("adult1", "35");
        // Map<String, String> householdVal2 = new HashMap<>();
        // householdVal2.put("adult2", "28");
        // Map<String, String> householdVal3 = new HashMap<>();
        // householdVal3.put("adult3", "18");
        // Map<String, String> householdVal4 = new HashMap<>();
        // householdVal4.put("minor1", "10");
        // Map[] householdArray = new Map[3];
        // householdArray[0] = householdVal1;
        // householdArray[1] = householdVal2;
        // householdArray[2] = householdVal4;
        //householdArray[3] = householdVal4;
        //ssiApp.setHouseholdComposition(householdArray);

        return ssiApp;
    }

    private SsiApplication generateSsiApp2(){
        SsiApplication ssiApp = new SsiApplication();
        ssiApp.setOtherBenefitsR("120");
        LinkedHashMap<LocalDateTime, String> otherBenHistory = new LinkedHashMap<>();
        otherBenHistory.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(1), "150");
        //otherBenHistory.put(LocalDateTime.of(2020, 6, 15, 1, 1, 1), "550");
        otherBenHistory.put(LocalDateTime.now().minusMonths(2).withDayOfMonth(7), "120");
        ssiApp.setOtherBenefitsRHistory(otherBenHistory);
        ssiApp.setLuxury("false");
        ssiApp.setTotalIncome("10");
        ssiApp.setHospitalized("true");
        ssiApp.setUnemployed("true");
        ssiApp.setEmploymentStatus("unemployed");
        ssiApp.setTaxisAfm("678901");
        //ssiApp.setSalariesR("2000");
        ssiApp.setPensionsR("700");

        LinkedHashMap<LocalDateTime, String> pensionHistory = new LinkedHashMap<>();
        pensionHistory.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(1), "600");
        pensionHistory.put(LocalDateTime.now().minusMonths(2).withDayOfMonth(5), "700");
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
        member3.setDateOfBirth("19/10/2002");
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
        housholdHistory.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(1), householdH);
        housholdHistory.put(LocalDateTime.now().minusMonths(2).withDayOfMonth(11), household);
        housholdHistory.put(LocalDateTime.now().minusMonths(1).withDayOfMonth(16), householdLatest);

        ssiApp.setHouseholdCompositionHistory(housholdHistory);

        return ssiApp;
    }

    private SsiApplication generateSsiApp3(){
        SsiApplication ssiApp = new SsiApplication();
        ssiApp.setOtherBenefitsR("200");
        LinkedHashMap<LocalDateTime, String> otherBenHistory = new LinkedHashMap<>();
        otherBenHistory.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(1), "150");
        //otherBenHistory.put(LocalDateTime.of(2020, 6, 15, 1, 1, 1), "550");
        otherBenHistory.put(LocalDateTime.now().minusMonths(2).withDayOfMonth(7), "200");
        ssiApp.setOtherBenefitsRHistory(otherBenHistory);
        ssiApp.setLuxury("false");
        ssiApp.setTotalIncome("10");
        ssiApp.setHospitalized("true");
        ssiApp.setUnemployed("true");
        ssiApp.setEmploymentStatus("unemployed");
        ssiApp.setTaxisAfm("164582");
        //ssiApp.setSalariesR("2000");
        ssiApp.setPensionsR("0");

        LinkedHashMap<LocalDateTime, String> pensionHistory = new LinkedHashMap<>();
        pensionHistory.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(1), "0");
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
        member3.setDateOfBirth("19/10/2002");
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
        housholdHistory.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(1), householdH);
        housholdHistory.put(LocalDateTime.now().minusMonths(2).withDayOfMonth(11), household);
        housholdHistory.put(LocalDateTime.now().minusMonths(1).withDayOfMonth(16), householdLatest);

        ssiApp.setHouseholdCompositionHistory(housholdHistory);

        return ssiApp;
    }

    private SsiApplication generateSsiApp4(){
        SsiApplication ssiApp = new SsiApplication();
        ssiApp.setOtherBenefitsR("150");
        LinkedHashMap<LocalDateTime, String> otherBenHistory = new LinkedHashMap<>();
        otherBenHistory.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(1), "150");
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

        LinkedHashMap<LocalDateTime, String> pensionHistory = new LinkedHashMap<>();
        pensionHistory.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(1), "200");
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
        member3.setDateOfBirth("19/10/2002");
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
        housholdHistory.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(1), householdH);
        housholdHistory.put(LocalDateTime.now().minusMonths(2).withDayOfMonth(11), household);
        housholdHistory.put(LocalDateTime.now().minusMonths(1).withDayOfMonth(16), householdLatest);

        ssiApp.setHouseholdCompositionHistory(housholdHistory);

        return ssiApp;
    }

    private SsiApplication generateSsiApp5(){
        SsiApplication ssiApp = new SsiApplication();
        ssiApp.setOtherBenefitsR("100");
        LinkedHashMap<LocalDateTime, String> otherBenHistory = new LinkedHashMap<>();
        otherBenHistory.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(1), "100");
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

        LinkedHashMap<LocalDateTime, String> pensionHistory = new LinkedHashMap<>();
        pensionHistory.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(1), "100");
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
        member3.setDateOfBirth("19/10/2002");
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
        housholdHistory.put(LocalDateTime.now().minusMonths(3).withDayOfMonth(1), householdH);
        housholdHistory.put(LocalDateTime.now().minusMonths(2).withDayOfMonth(11), household);
        housholdHistory.put(LocalDateTime.now().minusMonths(1).withDayOfMonth(16), householdLatest);

        ssiApp.setHouseholdCompositionHistory(housholdHistory);

        return ssiApp;
    }

    private Integer monthDays(LocalDateTime date) {

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
