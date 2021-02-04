package com.example.ethereumserviceapp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.CaseHistory;
import com.example.ethereumserviceapp.model.CasePayment;
import com.example.ethereumserviceapp.model.CredsAndExp;
import com.example.ethereumserviceapp.model.HouseholdMember;
import com.example.ethereumserviceapp.model.State;
import com.example.ethereumserviceapp.model.entities.SsiApplication;
import com.example.ethereumserviceapp.repository.SsiApplicationRepository;
import com.example.ethereumserviceapp.service.EthereumService;
import com.example.ethereumserviceapp.service.MongoService;
import com.example.ethereumserviceapp.service.MonitorService;
import com.example.ethereumserviceapp.service.impl.MonitorServiceImpl;
import com.example.ethereumserviceapp.utils.DateUtils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class ExampleRunTest extends TestUtils{
    
    @Autowired
    SsiApplicationRepository rep;
 
    @Mock
    EthereumService ethServ;
 
    @Mock
    MongoService mongoServ;

    @Test
   public void executeExample(){

        LocalDateTime runDate = LocalDateTime.of(2021, 2, 4, 12, 00, 00);

        MonitorService monServ = new MonitorServiceImpl(mongoServ, ethServ);

        List<String> uuids = new ArrayList<>();
        uuids.add("2WiYi1");

        List<SsiApplication> mockList = new ArrayList<>();
        SsiApplication exampleApp1 = generateExampleSsiApp1();
        mockList.add(exampleApp1);
        mockList.add(generateExampleSsiApp2());
        mockList.add(generateExampleSsiApp3());
        mockList.add(generateExampleSsiApp4());

        String expDateStr = String.valueOf(runDate.plusDays(1).toEpochSecond(ZoneOffset.UTC));
        CredsAndExp cred = new CredsAndExp();
        cred.setExp(expDateStr);
        cred.setId("2WiYi1");
        CredsAndExp[] credIdAndExp = new CredsAndExp[]{cred};
        List<SsiApplication> oneItemList = new ArrayList<>();
        oneItemList.add(exampleApp1);

        LocalDate startDate = exampleApp1.getTime();

        Case exampleCase = generateExampleCase("2WiYi1", State.ACCEPTED, false, false, "", LocalDate.now(), startDate);

        Mockito.when(ethServ.getAllCaseUUID()).thenReturn(uuids);
        Mockito.when(ethServ.getCaseByUUID(anyString())).thenReturn(Optional.of(exampleCase));
        Mockito.when(mongoServ.findByUuid(anyString())).thenReturn(Optional.of(exampleApp1));
        Mockito.when(mongoServ.findByTaxisAfmIn(anySet())).thenReturn(mockList);
        Mockito.when(mongoServ.findCredentialIdsByUuid(anyString())).thenReturn(credIdAndExp);
        Mockito.when(ethServ.checkRevocationStatus(anyString())).thenReturn(false);
        Mockito.when(mongoServ.findByHouseholdPrincipalIn(any())).thenReturn(oneItemList);
        Mockito.when(mongoServ.findByHouseholdComposition(any())).thenReturn(oneItemList);
        //Mockito.when(mongoServ.findByMeterNumber(anyString())).thenReturn(oneItemList);
        Mockito.when(mongoServ.findByIban(anyString())).thenReturn(oneItemList);
        //doNothing().when(ethServ).updateCase(any(), false);

        //monServ.startScheduledMonitoring();
        monServ.startMonitoring(runDate, true);

        verify(ethServ, times(1)).updateCase(any());

    }

    private SsiApplication generateExampleSsiApp1(){
        SsiApplication ssiApp = new SsiApplication();
        ssiApp.setOtherBenefitsR("500");
        LinkedHashMap<String, String> otherBenHistory = new LinkedHashMap<>();
        otherBenHistory.put(DateUtils.dateToString(LocalDateTime.of(2020, 12, 15, 00, 00, 00)), "500");
        otherBenHistory.put(DateUtils.dateToString(LocalDateTime.of(2021, 1, 17, 00, 00, 00)), "700");
        ssiApp.setOtherBenefitsRHistory(otherBenHistory);
        ssiApp.setUnemploymentBenefitR("480");
        LinkedHashMap<String , String> unmplBnftHistory = new LinkedHashMap<>();
        unmplBnftHistory.put(DateUtils.dateToString(LocalDateTime.of(2020, 12, 15, 00, 00, 00)), "480");
        unmplBnftHistory.put(DateUtils.dateToString(LocalDateTime.of(2021, 1, 10, 00, 00, 00)), "0");
        ssiApp.setUnemploymentBenefitRHistory(unmplBnftHistory);
        ssiApp.setErgomeR("0");
        ssiApp.setDepositsA("50");
        LinkedHashMap<String , String> depositsHistory = new LinkedHashMap<>();
        depositsHistory.put(DateUtils.dateToString(LocalDateTime.of(2020, 12, 15, 00, 00, 00)), "50");
        ssiApp.setDepositsAHistory(depositsHistory);
        ssiApp.setRentIncomeR("0");
        ssiApp.setOtherIncomeR("0");
        ssiApp.setUuid("2WiYi1");
        ssiApp.setLuxury("false");
        ssiApp.setTotalIncome("1030");
        ssiApp.setHospitalized("false");
        ssiApp.setUnemployed("true");
        ssiApp.setEmploymentStatus("unemployed");
        ssiApp.setTaxisAfm("123456");
        //ssiApp.setSalariesR("0.5");
        ssiApp.setPensionsR("0");
        ssiApp.setTaxisDateOfBirth(DateUtils.dateToString(LocalDate.of(1972, 5, 5)));
        ssiApp.setMeterNumber("123456789");
        ssiApp.setIban("iban123456");
        ssiApp.setTime(LocalDate.of(2020, 12, 15));

        

        HouseholdMember member1 = new HouseholdMember();
        member1.setAfm("123456");
        member1.setName("Γιώργος");
        member1.setSurname("Παπαδόπουλος");
        member1.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1972, 5, 5)));
        HouseholdMember member2 = new HouseholdMember();
        member2.setAfm("678901");
        member2.setName("Ελένη");
        member2.setSurname("Παπαδοπούλου");
        member2.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1977, 9, 12)));
        HouseholdMember member3 = new HouseholdMember();
        member3.setAfm("164582");
        member3.setName("Γιάννης");
        member3.setSurname("Παπαδόπουλος");
        member3.setDateOfBirth(DateUtils.dateToString(LocalDate.of(2006, 3, 24)));
        HouseholdMember member4 = new HouseholdMember();
        member4.setAfm("8521479");
        member4.setName("Σταύρος");
        member4.setSurname("Παπαδόπουλος");
        member4.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1948, 11, 14)));
        List<HouseholdMember> household = new ArrayList<>();
        household.add(member1);
        household.add(member2);
        household.add(member3);
        household.add(member4);

        ssiApp.setHouseholdPrincipal(member1);
        ssiApp.setHouseholdComposition(household);

        LinkedHashMap<String, List<HouseholdMember>> housholdHistory = new LinkedHashMap<>();

        housholdHistory.put(DateUtils.dateToString(LocalDateTime.of(2020, 12, 15, 00, 00, 00)), household);

        ssiApp.setHouseholdCompositionHistory(housholdHistory);

        return ssiApp;
    }

    private SsiApplication generateExampleSsiApp2(){
        SsiApplication ssiApp = new SsiApplication();
        ssiApp.setOtherBenefitsR("0");
        
        ssiApp.setUnemploymentBenefitR("0");
        ssiApp.setErgomeR("0");
        ssiApp.setRentIncomeR("0");
        ssiApp.setOtherIncomeR("0");
        ssiApp.setUuid("2WiYi2");
        ssiApp.setLuxury("false");
        ssiApp.setTotalIncome("2000");
        ssiApp.setHospitalized("false");
        ssiApp.setUnemployed("true");
        ssiApp.setEmploymentStatus("employed");
        ssiApp.setTaxisAfm("678901");
        ssiApp.setSalariesR("2000");
        LinkedHashMap<String, String> salariesHistory = new LinkedHashMap<>();
        salariesHistory.put(DateUtils.dateToString(LocalDateTime.of(2020, 12, 15, 00, 00, 00)), "2000");
        ssiApp.setSalariesRHistory(salariesHistory);
        ssiApp.setPensionsR("0");
        ssiApp.setTaxisDateOfBirth(DateUtils.dateToString(LocalDate.of(1977, 9, 12)));
        ssiApp.setMeterNumber("123456789");
        ssiApp.setIban("iban678901");
        ssiApp.setTime(LocalDate.of(2020, 12, 15));

        LinkedHashMap<String , String> unmplBnftHistory = new LinkedHashMap<>();
        unmplBnftHistory.put(DateUtils.dateToString(LocalDateTime.of(2020, 12, 15, 00, 00, 00)), "0");
        ssiApp.setUnemploymentBenefitRHistory(unmplBnftHistory);

        HouseholdMember member1 = new HouseholdMember();
        member1.setAfm("123456");
        member1.setName("Γιώργος");
        member1.setSurname("Παπαδόπουλος");
        member1.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1972, 5, 5)));
        HouseholdMember member2 = new HouseholdMember();
        member2.setAfm("678901");
        member2.setName("Ελένη");
        member2.setSurname("Παπαδοπούλου");
        member2.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1977, 9, 12)));
        HouseholdMember member3 = new HouseholdMember();
        member3.setAfm("164582");
        member3.setName("Γιάννης");
        member3.setSurname("Παπαδόπουλος");
        member3.setDateOfBirth(DateUtils.dateToString(LocalDate.of(2006, 3, 24)));
        HouseholdMember member4 = new HouseholdMember();
        member4.setAfm("8521479");
        member4.setName("Σταύρος");
        member4.setSurname("Παπαδόπουλος");
        member4.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1948, 11, 14)));
        List<HouseholdMember> household = new ArrayList<>();
        household.add(member1);
        household.add(member2);
        household.add(member3);
        household.add(member4);

        ssiApp.setHouseholdPrincipal(member1);
        ssiApp.setHouseholdComposition(household);

        LinkedHashMap<String, List<HouseholdMember>> housholdHistory = new LinkedHashMap<>();

        housholdHistory.put(DateUtils.dateToString(LocalDateTime.of(2020, 12, 15, 00, 00, 00)), household);

        ssiApp.setHouseholdCompositionHistory(housholdHistory);

        return ssiApp;
    }

    private SsiApplication generateExampleSsiApp3(){
        SsiApplication ssiApp = new SsiApplication();
        ssiApp.setOtherBenefitsR("0");
        LinkedHashMap<String, String> otherBenHistory = new LinkedHashMap<>();
        otherBenHistory.put(DateUtils.dateToString(LocalDateTime.of(2020, 12, 15, 00, 00, 00)), "0");
        ssiApp.setOtherBenefitsRHistory(otherBenHistory);
        ssiApp.setUnemploymentBenefitR("0");
        ssiApp.setErgomeR("0");
        ssiApp.setRentIncomeR("0");
        ssiApp.setOtherIncomeR("0");
        ssiApp.setUuid("2WiYi3");
        ssiApp.setLuxury("false");
        ssiApp.setTotalIncome("0");
        ssiApp.setHospitalized("false");
        ssiApp.setUnemployed("true");
        ssiApp.setEmploymentStatus("unemployed");
        ssiApp.setTaxisAfm("164582");
        //ssiApp.setSalariesR("0.5");
        ssiApp.setPensionsR("0");
        ssiApp.setTaxisDateOfBirth(DateUtils.dateToString(LocalDate.of(2006, 3, 24)));
        ssiApp.setMeterNumber("123456789");
        ssiApp.setIban("iban164582");
        ssiApp.setTime(LocalDate.of(2020, 12, 15));

        LinkedHashMap<String , String> unmplBnftHistory = new LinkedHashMap<>();
        unmplBnftHistory.put(DateUtils.dateToString(LocalDateTime.of(2020, 12, 15, 00, 00, 00)), "480");
        ssiApp.setUnemploymentBenefitRHistory(unmplBnftHistory);

        HouseholdMember member1 = new HouseholdMember();
        member1.setAfm("123456");
        member1.setName("Γιώργος");
        member1.setSurname("Παπαδόπουλος");
        member1.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1972, 5, 5)));
        HouseholdMember member2 = new HouseholdMember();
        member2.setAfm("678901");
        member2.setName("Ελένη");
        member2.setSurname("Παπαδοπούλου");
        member2.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1977, 9, 12)));
        HouseholdMember member3 = new HouseholdMember();
        member3.setAfm("164582");
        member3.setName("Γιάννης");
        member3.setSurname("Παπαδόπουλος");
        member3.setDateOfBirth(DateUtils.dateToString(LocalDate.of(2006, 3, 24)));
        HouseholdMember member4 = new HouseholdMember();
        member4.setAfm("8521479");
        member4.setName("Σταύρος");
        member4.setSurname("Παπαδόπουλος");
        member4.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1948, 11, 14)));
        List<HouseholdMember> household = new ArrayList<>();
        household.add(member1);
        household.add(member2);
        household.add(member3);
        household.add(member4);

        ssiApp.setHouseholdPrincipal(member1);
        ssiApp.setHouseholdComposition(household);

        LinkedHashMap<String, List<HouseholdMember>> housholdHistory = new LinkedHashMap<>();

        housholdHistory.put(DateUtils.dateToString(LocalDateTime.of(2020, 12, 15, 00, 00, 00)), household);

        ssiApp.setHouseholdCompositionHistory(housholdHistory);

        return ssiApp;
    }

    private SsiApplication generateExampleSsiApp4(){
        SsiApplication ssiApp = new SsiApplication();
        ssiApp.setOtherBenefitsR("0");
        LinkedHashMap<String, String> otherBenHistory = new LinkedHashMap<>();
        otherBenHistory.put(DateUtils.dateToString(LocalDateTime.of(2020, 12, 15, 00, 00, 00)), "0");
        ssiApp.setOtherBenefitsRHistory(otherBenHistory);
        ssiApp.setErgomeR("0");
        ssiApp.setRentIncomeR("0");
        ssiApp.setOtherIncomeR("0");
        ssiApp.setUuid("2WiYi4");
        ssiApp.setLuxury("false");
        ssiApp.setTotalIncome("2760");
        ssiApp.setHospitalized("false");
        ssiApp.setUnemployed("true");
        ssiApp.setEmploymentStatus("unemployed");
        ssiApp.setTaxisAfm("8521479");
        //ssiApp.setSalariesR("0.5");
        ssiApp.setPensionsR("2760");
        LinkedHashMap<String, String> pensionsHistory = new LinkedHashMap<>();
        pensionsHistory.put(DateUtils.dateToString(LocalDateTime.of(2020, 12, 15, 00, 00, 00)), "0");
        ssiApp.setPensionsRHistory(pensionsHistory);
        ssiApp.setTaxisDateOfBirth(DateUtils.dateToString(LocalDate.of(1948, 11, 14)));
        ssiApp.setMeterNumber("123456789");
        ssiApp.setIban("iban8521479");
        ssiApp.setTime(LocalDate.of(2020, 12, 15));
        ssiApp.setUnemploymentBenefitR("0");
        LinkedHashMap<String , String> unmplBnftHistory = new LinkedHashMap<>();
        unmplBnftHistory.put(DateUtils.dateToString(LocalDateTime.of(2020, 12, 15, 00, 00, 00)), "0");
        ssiApp.setUnemploymentBenefitRHistory(unmplBnftHistory);

        HouseholdMember member1 = new HouseholdMember();
        member1.setAfm("123456");
        member1.setName("Γιώργος");
        member1.setSurname("Παπαδόπουλος");
        member1.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1972, 5, 5)));
        HouseholdMember member2 = new HouseholdMember();
        member2.setAfm("678901");
        member2.setName("Ελένη");
        member2.setSurname("Παπαδοπούλου");
        member2.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1977, 9, 12)));
        HouseholdMember member3 = new HouseholdMember();
        member3.setAfm("164582");
        member3.setName("Γιάννης");
        member3.setSurname("Παπαδόπουλος");
        member3.setDateOfBirth(DateUtils.dateToString(LocalDate.of(2006, 3, 24)));
        HouseholdMember member4 = new HouseholdMember();
        member4.setAfm("8521479");
        member4.setName("Σταύρος");
        member4.setSurname("Παπαδόπουλος");
        member4.setDateOfBirth(DateUtils.dateToString(LocalDate.of(1948, 11, 14)));
        List<HouseholdMember> household = new ArrayList<>();
        household.add(member1);
        household.add(member2);
        household.add(member3);
        household.add(member4);

        ssiApp.setHouseholdPrincipal(member1);
        ssiApp.setHouseholdComposition(household);

        LinkedHashMap<String, List<HouseholdMember>> housholdHistory = new LinkedHashMap<>();

        housholdHistory.put(DateUtils.dateToString(LocalDateTime.of(2020, 12, 15, 00, 00, 00)), household);

        ssiApp.setHouseholdCompositionHistory(housholdHistory);

        return ssiApp;
    }

    private Case generateExampleCase(String uuid, State state, Boolean allRejected, Boolean isOld, String asyncRejectedDate, LocalDate currentDate, LocalDate startDate){
        
        LocalDateTime currentDateTime = LocalDateTime.of(currentDate, LocalTime.of(00, 00, 00));
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.of(00, 00, 00));
        Case monitoredCase = new Case();
        monitoredCase.setUuid(uuid);
        monitoredCase.setDate(currentDateTime.minusDays(1));
        monitoredCase.setState(state);
        LinkedHashMap<LocalDateTime, State> history = new LinkedHashMap<>();
        LinkedHashMap<LocalDateTime, BigDecimal> dailyValue = new LinkedHashMap<>();
        Integer daysOfCurrentPayment = monthDays(currentDateTime.minusMonths(1));
        Integer daysOfMinus2 = monthDays(startDateTime);
        //Integer daysOfMinus3 = monthDays(LocalDateTime.now().minusMonths(3));
        List<CaseHistory> caseHistory = new ArrayList<>();
        

        if(asyncRejectedDate != null && !"".equals(asyncRejectedDate)){
            monitoredCase.setRejectionDate(asyncRejectedDate);
        } else {
            monitoredCase.setRejectionDate("");
        }

        for(int i=startDateTime.getDayOfMonth(); i<=daysOfMinus2; i++){
            BigDecimal dailySum = new BigDecimal(0.03).multiply(BigDecimal.valueOf(i));
            CaseHistory ch = new CaseHistory();
            ch.setDailyBenefit(new BigDecimal(0.03));
            ch.setDailySum(dailySum);
            ch.setDate(startDateTime.withDayOfMonth(i));
            ch.setState(State.ACCEPTED);
            caseHistory.add(ch);
            history.put(startDateTime.withDayOfMonth(i), State.ACCEPTED);
            dailyValue.put(startDateTime.withDayOfMonth(i), BigDecimal.valueOf(0.03));
        }

        // for(int i=1; i<=daysOfMinus2; i++){
        //     BigDecimal dailySum = new BigDecimal(0.03).multiply(BigDecimal.valueOf(i));
        //     CaseHistory ch = new CaseHistory();
        //     ch.setDailyBenefit(new BigDecimal(0.03));
        //     ch.setDailySum(dailySum);
        //     ch.setDate(date);
        //     history.put(currentDateTime.minusMonths(2).withDayOfMonth(i), State.ACCEPTED);
        //     dailyValue.put(currentDateTime.minusMonths(2).withDayOfMonth(i), BigDecimal.valueOf(0.03));
        // }
        for(int i=1; i<=daysOfCurrentPayment; i++){
            BigDecimal dailySum = new BigDecimal(0.03).multiply(BigDecimal.valueOf(i));
            CaseHistory ch = new CaseHistory();
            ch.setDailyBenefit(new BigDecimal(0.03));
            ch.setDailySum(dailySum);
            ch.setDate(currentDateTime.minusMonths(1).withDayOfMonth(i));
            ch.setState(State.ACCEPTED);
            caseHistory.add(ch);
            history.put(currentDateTime.minusMonths(1).withDayOfMonth(i), State.ACCEPTED);
            dailyValue.put(currentDateTime.minusMonths(1).withDayOfMonth(i), BigDecimal.valueOf(0.03));
        }
        for(int i=1; i<currentDateTime.getDayOfMonth(); i++){
            BigDecimal dailySum = new BigDecimal(0.03).multiply(BigDecimal.valueOf(i));
            CaseHistory ch = new CaseHistory();
            ch.setDailyBenefit(new BigDecimal(0.03));
            ch.setDailySum(dailySum);
            ch.setDate(currentDateTime.withDayOfMonth(i));
            ch.setState(State.ACCEPTED);
            caseHistory.add(ch);
            history.put(currentDateTime.withDayOfMonth(i), State.ACCEPTED);
            dailyValue.put(currentDateTime.withDayOfMonth(i), BigDecimal.valueOf(0.03));

        }
        
        monitoredCase.setHistory(history);
        List<CasePayment> paymentHistory = new ArrayList<>();
        CasePayment payment1 = new CasePayment();  
        payment1.setPayment(BigDecimal.valueOf(0.51));
        payment1.setPaymentDate(currentDateTime.minusMonths(1).withDayOfMonth(1));
        payment1.setState(State.PAID);
        CasePayment payment2 = new CasePayment();    
        payment2.setPayment(BigDecimal.valueOf(0.93));
        payment2.setPaymentDate(currentDateTime.withDayOfMonth(1));
        payment2.setState(State.PAID);
        // CasePayment payment3 = new CasePayment();    
        // payment3.setPayment(BigDecimal.valueOf(150.00));
        // payment3.setPaymentDate(LocalDateTime.of(2020, 10, 1, 0, 0, 2));
        // payment3.setState(State.PAID);
        paymentHistory.add(payment1);
        paymentHistory.add(payment2);
        //paymentHistory.add(payment3);
        monitoredCase.setPaymentHistory(paymentHistory);

        monitoredCase.setCaseHistory(caseHistory);

        monitoredCase.setOffset(BigDecimal.ZERO);

        return monitoredCase;

    }
}
