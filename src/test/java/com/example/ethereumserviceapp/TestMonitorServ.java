/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.CasePayment;
import com.example.ethereumserviceapp.model.CredsAndExp;
import com.example.ethereumserviceapp.model.State;
import com.example.ethereumserviceapp.model.entities.SsiApplication;
import com.example.ethereumserviceapp.repository.SsiApplicationRepository;
import com.example.ethereumserviceapp.service.EthereumService;
import com.example.ethereumserviceapp.service.MongoService;
import com.example.ethereumserviceapp.service.MonitorService;
import com.example.ethereumserviceapp.service.impl.EthereumServiceImpl;
import com.example.ethereumserviceapp.service.impl.MongoServiceImpl;
import com.example.ethereumserviceapp.service.impl.MonitorServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author nikos
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
public class TestMonitorServ {

    @Autowired
    SsiApplicationRepository rep;

    @Mock
    EthereumService ethServ;

    @Mock
    MongoService mongoServ;

    @Test
    public void testGetBallance() throws IOException {
        MongoService monogServ = new MongoServiceImpl(rep);
        EthereumService ethServ = new EthereumServiceImpl();
        MonitorService monServ = new MonitorServiceImpl(monogServ, ethServ);

        monServ.startMonitoring();

        assertEquals(true, true);

    }

    @Test
    public void testStartMonitoringAccepted(){

        MonitorService monServ = new MonitorServiceImpl(mongoServ, ethServ);

        List<String> uuids = new ArrayList<>();
        uuids.add("2WiYi1");
        //uuids.add("2WiYi2");

        String expDateStr = String.valueOf(LocalDateTime.now().plusDays(1).toEpochSecond(ZoneOffset.UTC));
        CredsAndExp cred = new CredsAndExp();
        cred.setExp(expDateStr);
        cred.setId("2WiYi1");
        CredsAndExp[] credIdAndExp = new CredsAndExp[]{cred};

        Mockito.when(ethServ.getAllCaseUUID()).thenReturn(uuids);
        Mockito.when(ethServ.getCaseByUUID("2WiYi1")).thenReturn(Optional.of(generateMockCase("2WiYi1", State.ACCEPTED, false)));
        //Mockito.when(ethServ.getCaseByUUID("2WiYi2")).thenReturn(Optional.of(generateMockCase("2WiYi2", State.ACCEPTED)));
        //Mockito.when(mongoServ.deleteByUuid("2WiYi1")).thenReturn(null);
        Mockito.when(mongoServ.findCredentialIdsByUuid("2WiYi1")).thenReturn(credIdAndExp);
        Mockito.when(ethServ.checkRevocationStatus(anyString())).thenReturn(false);
        Mockito.when(mongoServ.findByUuid("2WiYi1")).thenReturn(Optional.of(generateMockSsiApp("2WiYi1")));
        doNothing().when(ethServ).updateCase(any());

        monServ.startMonitoring();

        verify(ethServ, times(1)).updateCase(any());
        
    }

    @Test
    public void testStartMonitoringExpired(){

        MonitorService monServ = new MonitorServiceImpl(mongoServ, ethServ);

        List<String> uuids = new ArrayList<>();
        uuids.add("2WiYi1");
        //uuids.add("2WiYi2");
        
        String expDateStr = String.valueOf(LocalDateTime.of(2020, 8, 5, 1, 1, 1).toEpochSecond(ZoneOffset.UTC));
        CredsAndExp cred = new CredsAndExp();
        cred.setExp(expDateStr);
        cred.setId("2WiYi1");

        CredsAndExp[] credIdAndExp = new CredsAndExp[]{cred};

        Mockito.when(ethServ.getAllCaseUUID()).thenReturn(uuids);
        Mockito.when(ethServ.getCaseByUUID("2WiYi1")).thenReturn(Optional.of(generateMockCase("2WiYi1", State.ACCEPTED, false)));
        Mockito.when(mongoServ.findCredentialIdsByUuid("2WiYi1")).thenReturn(credIdAndExp);
        doNothing().when(ethServ).updateCase(any());
        monServ.startMonitoring();

        verify(ethServ, times(1)).updateCase(any());
        
    }

    @Test
    public void testStartMonitoringRevoked(){

        MonitorService monServ = new MonitorServiceImpl(mongoServ, ethServ);

        List<String> uuids = new ArrayList<>();
        uuids.add("2WiYi1");
        //uuids.add("2WiYi2");
        
        String expDateStr = String.valueOf(LocalDateTime.now().plusDays(1).toEpochSecond(ZoneOffset.UTC));
        CredsAndExp cred = new CredsAndExp();
        cred.setExp(expDateStr);
        cred.setId("2WiYi1");

        CredsAndExp[] credIdAndExp = new CredsAndExp[]{cred};

        Mockito.when(ethServ.getAllCaseUUID()).thenReturn(uuids);
        Mockito.when(ethServ.getCaseByUUID("2WiYi1")).thenReturn(Optional.of(generateMockCase("2WiYi1", State.ACCEPTED, false)));
       
        Mockito.when(mongoServ.findCredentialIdsByUuid("2WiYi1")).thenReturn(credIdAndExp);
        Mockito.when(ethServ.checkRevocationStatus(anyString())).thenReturn(true);
       
        doNothing().when(ethServ).updateCase(any());
        monServ.startMonitoring();
        
        verify(ethServ, times(1)).updateCase(any());
        
    }

    @Test
    public void testStartMonitoringOlderThan6Months(){

        MonitorService monServ = new MonitorServiceImpl(mongoServ, ethServ);

        List<String> uuids = new ArrayList<>();
        uuids.add("2WiYi1");
        //uuids.add("2WiYi2");

        String expDateStr = String.valueOf(LocalDateTime.now().plusDays(1).toEpochSecond(ZoneOffset.UTC));
        CredsAndExp cred = new CredsAndExp();
        cred.setExp(expDateStr);
        cred.setId("2WiYi1");
        CredsAndExp[] credIdAndExp = new CredsAndExp[]{cred};

        Mockito.when(ethServ.getAllCaseUUID()).thenReturn(uuids);
        Mockito.when(ethServ.getCaseByUUID("2WiYi1")).thenReturn(Optional.of(generateMockCase("2WiYi1", State.ACCEPTED, true)));

        Mockito.when(mongoServ.findCredentialIdsByUuid("2WiYi1")).thenReturn(credIdAndExp);
        Mockito.when(ethServ.checkRevocationStatus(anyString())).thenReturn(false);
        Mockito.when(mongoServ.findByUuid("2WiYi1")).thenReturn(Optional.of(generateMockSsiApp("2WiYi1")));
        doNothing().when(ethServ).updateCase(any());

        monServ.startMonitoring();

        verify(ethServ, times(1)).updateCase(any());
        
    }

    @Test
    public void testStartMonitoringNoApplication(){

        MonitorService monServ = new MonitorServiceImpl(mongoServ, ethServ);

        List<String> uuids = new ArrayList<>();
        uuids.add("2WiYi1");
        //uuids.add("2WiYi2");

        String expDateStr = String.valueOf(LocalDateTime.now().plusDays(1).toEpochSecond(ZoneOffset.UTC));
        CredsAndExp cred = new CredsAndExp();
        cred.setExp(expDateStr);
        cred.setId("2WiYi1");
        CredsAndExp[] credIdAndExp = new CredsAndExp[]{cred};

        SsiApplication ssiApp = generateMockSsiApp("2WiYi1");
        ssiApp.setHospitalized("false");

        Mockito.when(ethServ.getAllCaseUUID()).thenReturn(uuids);
        Mockito.when(ethServ.getCaseByUUID("2WiYi1")).thenReturn(Optional.of(generateMockCase("2WiYi1", State.ACCEPTED, false)));

        Mockito.when(mongoServ.findCredentialIdsByUuid("2WiYi1")).thenReturn(credIdAndExp);
        Mockito.when(ethServ.checkRevocationStatus(anyString())).thenReturn(false);
        Mockito.when(mongoServ.findByUuid("2WiYi1")).thenReturn(Optional.of(ssiApp));
        doNothing().when(ethServ).updateCase(any());

        monServ.startMonitoring();

        verify(ethServ, times(1)).updateCase(any());
        
    }

    @Test
    public void testStartMonitoringAppRejected(){

        MonitorService monServ = new MonitorServiceImpl(mongoServ, ethServ);

        List<String> uuids = new ArrayList<>();
        uuids.add("2WiYi1");
        //uuids.add("2WiYi2");

        String expDateStr = String.valueOf(LocalDateTime.now().plusDays(1).toEpochSecond(ZoneOffset.UTC));
        CredsAndExp cred = new CredsAndExp();
        cred.setExp(expDateStr);
        cred.setId("2WiYi1");
        CredsAndExp[] credIdAndExp = new CredsAndExp[]{cred};
        

        Mockito.when(ethServ.getAllCaseUUID()).thenReturn(uuids);
        Mockito.when(ethServ.getCaseByUUID("2WiYi1")).thenReturn(Optional.of(generateMockCase("2WiYi1", State.ACCEPTED, false)));

        Mockito.when(mongoServ.findCredentialIdsByUuid("2WiYi1")).thenReturn(credIdAndExp);
        Mockito.when(ethServ.checkRevocationStatus(anyString())).thenReturn(false);
        Mockito.when(mongoServ.findByUuid("2WiYi1")).thenReturn(Optional.empty());
        doNothing().when(ethServ).updateCase(any());

        monServ.startMonitoring();

        verify(ethServ, times(1)).updateCase(any());
        
    }

    private SsiApplication generateMockSsiApp(String uuid){
        SsiApplication ssiApp = new SsiApplication();
        ssiApp.setOtherBenefitsR("5");
        ssiApp.setRentIncomeR("5");
        ssiApp.setOtherIncomeR("5");
        ssiApp.setUuid(uuid);
        ssiApp.setTotalIncome("10");
        ssiApp.setHospitalized("true");
        ssiApp.setUnemployed("true");
        ssiApp.setEmploymentStatus("unemployed");

        return ssiApp;
    }

    private Case generateMockCase(String uuid, State state, Boolean isOld){
        Case monitoredCase = new Case();

        monitoredCase.setDate(LocalDateTime.of(2020, 9, 10, 1, 1, 1));
        monitoredCase.setState(state);
        monitoredCase.setUuid(uuid);

        LinkedHashMap<LocalDateTime, State> history = new LinkedHashMap<>();
        if(isOld){
            history.put(LocalDateTime.of(2020, 1, 1, 1, 1), State.ACCEPTED);
        }
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
        payment1.setPayment(BigDecimal.valueOf(300));
        payment1.setPaymentDate(LocalDateTime.of(2020, 8, 1, 0, 0, 2));
        payment1.setState(State.PAID);
        CasePayment payment2 = new CasePayment();    
        payment2.setPayment(BigDecimal.valueOf(300));
        payment2.setPaymentDate(LocalDateTime.of(2020, 9, 1, 0, 0, 2));
        payment2.setState(State.PAID);
        paymentHistory.add(payment1);
        paymentHistory.add(payment2);
        monitoredCase.setPaymentHistory(paymentHistory);
        monitoredCase.setOffset(BigDecimal.valueOf(3));

        return monitoredCase;
    }


}
