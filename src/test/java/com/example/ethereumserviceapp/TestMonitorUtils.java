package com.example.ethereumserviceapp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.CasePayment;
import com.example.ethereumserviceapp.model.State;
import com.example.ethereumserviceapp.utils.MonitorUtils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
        payment1.setPayment(BigDecimal.valueOf(124));
        payment1.setPaymentDate(LocalDateTime.of(2020, 8, 1, 0, 0, 2));
        CasePayment payment2 = new CasePayment();    
        payment2.setPayment(BigDecimal.valueOf(124));
        payment2.setPaymentDate(LocalDateTime.of(2020, 9, 1, 0, 0, 2));
        paymentHistory.add(payment1);
        paymentHistory.add(payment2);
        monitoredCase.setPaymentHistory(paymentHistory);
        monitoredCase.setOffset(BigDecimal.valueOf(0));

        MonitorUtils.updateOffset(monitoredCase);

        log.info("offset :{}", monitoredCase.getOffset());

    }
    
}
