package com.example.ethereumserviceapp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.CasePayment;
import com.example.ethereumserviceapp.model.State;
import com.example.ethereumserviceapp.model.entities.SsiApplication;
import com.example.ethereumserviceapp.utils.MonitorUtils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class TestMonitorUtils {

    @Test
    public void testOffsetPayment(){
        Case monitoredCase = new Case();
        LinkedHashMap<LocalDateTime, State> history = new LinkedHashMap<>();
        history.put(LocalDateTime.of(2020, 7, 1, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 7, 2, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 7, 3, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 7, 4, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 7, 5, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 7, 6, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 7, 7, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 7, 8, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 7, 9, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 7, 10, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 7, 11, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 7, 12, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 7, 13, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 7, 14, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 7, 15, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 7, 16, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 7, 17, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 7, 18, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 7, 19, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 7, 20, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 7, 21, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 7, 22, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 7, 23, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 7, 24, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 7, 25, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 7, 26, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 7, 27, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 7, 28, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 7, 29, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 7, 30, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 7, 31, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 1, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 2, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 3, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 4, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 5, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 6, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 7, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 8, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 9, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 10, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 11, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 12, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 13, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 14, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 15, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 16, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 17, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 18, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 19, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 20, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 21, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 22, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 23, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 24, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 25, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 26, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 27, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 28, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 29, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 30, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 8, 31, 1, 1), State.ACCEPTED);
        history.put(LocalDateTime.of(2020, 9, 1, 1, 1), State.ACCEPTED);

        monitoredCase.setHistory(history);

        List<CasePayment> paymentHistory = new ArrayList<>();
        CasePayment payment1 = new CasePayment();  
        payment1.setPayment(BigDecimal.valueOf(150.00));
        payment1.setPaymentDate(LocalDateTime.of(2020, 8, 1, 0, 0, 2));
        CasePayment payment2 = new CasePayment();    
        payment2.setPayment(BigDecimal.valueOf(150.00));
        payment2.setPaymentDate(LocalDateTime.of(2020, 9, 1, 0, 0, 2));
        paymentHistory.add(payment1);
        paymentHistory.add(payment2);
        monitoredCase.setPaymentHistory(paymentHistory);
        monitoredCase.setOffset(BigDecimal.valueOf(0));

        SsiApplication ssiApp = new SsiApplication();
        // ssiApp.setOtherBenefitsR("5");
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
        ssiApp.setTaxisAfm("taxisAfm");
        //ssiApp.setSalariesR("2000");
        ssiApp.setPensionsR("2400");
        //ssiApp.setFreelanceR("500");
        //ssiApp.setDepositsA("50");
        //ssiApp.setOtherBenefitsR("480");
        Map<String, String> householdVal1 = new HashMap<>();
        householdVal1.put("adult1", "35");
        Map<String, String> householdVal2 = new HashMap<>();
        householdVal2.put("adult2", "28");
        Map<String, String> householdVal3 = new HashMap<>();
        householdVal3.put("adult3", "18");
        Map<String, String> householdVal4 = new HashMap<>();
        householdVal4.put("minor1", "10");
        Map[] householdArray = new Map[3];
        householdArray[0] = householdVal1;
        householdArray[1] = householdVal2;
        householdArray[2] = householdVal4;
        //householdArray[3] = householdVal4;
        ssiApp.setHouseholdComposition(householdArray);
        // BigDecimal salaries = BigDecimal.valueOf(Long.parseLong(ssiApp.getSalariesR())).subtract(BigDecimal.valueOf(Long.parseLong(ssiApp.getSalariesR())).multiply(BigDecimal.valueOf(0.2)));
        
        // BigDecimal pensions = BigDecimal.valueOf(Long.parseLong(ssiApp.getPensionsR()));

        // BigDecimal farming = BigDecimal.valueOf(Long.parseLong(ssiApp.getFarmingR()));
        // BigDecimal freelance = BigDecimal.valueOf(Long.parseLong(ssiApp.getFreelanceR()));
        // BigDecimal other = BigDecimal.valueOf(Long.parseLong(ssiApp.getOtherBenefitsR()));
        // BigDecimal deposits = BigDecimal.valueOf(Long.parseLong(ssiApp.getDepositsA()));
        // BigDecimal domRealEstate = BigDecimal.valueOf(Long.parseLong(ssiApp.getDomesticRealEstateA()));
        // BigDecimal foreignRealEstate = BigDecimal.valueOf(Long.parseLong(ssiApp.getForeignRealEstateA()));
        MonitorUtils.updateOffset(LocalDate.of(2020, 8, 12), monitoredCase, ssiApp);

        log.info("offset :{}", monitoredCase.getOffset());

    }
    
}
