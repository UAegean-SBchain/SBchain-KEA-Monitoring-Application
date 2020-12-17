/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
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
import net.bytebuddy.build.Plugin.Engine.Source.Empty;

/**
 *
 * @author nikos
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
public class TestMonitorServ extends TestUtils{

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

        monServ.startMonitoring(null);

        assertEquals(true, true);

    }

    @Test
    public void testStartMonitoringAcceptedNoOffset(){

        MonitorService monServ = new MonitorServiceImpl(mongoServ, ethServ);

        List<String> uuids = new ArrayList<>();
        uuids.add("2WiYi1");

        List<SsiApplication> mockList = new ArrayList<>();
        mockList.add(generateSsiApp1());
        mockList.add(generateSsiApp2());
        mockList.add(generateSsiApp3());

        String expDateStr = String.valueOf(LocalDateTime.now().plusDays(1).toEpochSecond(ZoneOffset.UTC));
        CredsAndExp cred = new CredsAndExp();
        cred.setExp(expDateStr);
        cred.setId("2WiYi1");
        CredsAndExp[] credIdAndExp = new CredsAndExp[]{cred};
        List<SsiApplication> oneItemList = new ArrayList<>();
        oneItemList.add(generateSsiApp1());

        Mockito.when(ethServ.getAllCaseUUID()).thenReturn(uuids);
        Mockito.when(ethServ.getCaseByUUID(anyString())).thenReturn(Optional.of(generateMockCase("2WiYi1", State.ACCEPTED, false, false)));
        Mockito.when(mongoServ.findByUuid(anyString())).thenReturn(Optional.of(generateSsiApp1()));
        Mockito.when(mongoServ.findByTaxisAfmIn(anySet())).thenReturn(mockList);
        Mockito.when(mongoServ.findCredentialIdsByUuid(anyString())).thenReturn(credIdAndExp);
        Mockito.when(ethServ.checkRevocationStatus(anyString())).thenReturn(false);
        Mockito.when(mongoServ.findByMeterNumber(anyString())).thenReturn(oneItemList);
        Mockito.when(mongoServ.findByIban(anyString())).thenReturn(oneItemList);
        doNothing().when(ethServ).updateCase(any());

        monServ.startMonitoring(null);

        verify(ethServ, times(1)).updateCase(any());
        
    }

    @Test
    public void testStartMonitoringAcceptedWithOffset(){

        MonitorService monServ = new MonitorServiceImpl(mongoServ, ethServ);

        List<String> uuids = new ArrayList<>();
        uuids.add("2WiYi1");

        Case monitoredCase = generateMockCase("2WiYi1", State.ACCEPTED, false, false);
        SsiApplication ssiApp1 = generateSsiAppAltered1();
        SsiApplication ssiApp2 = generateSsiAppAltered2();
        SsiApplication ssiApp3 = generateSsiAppAltered3();
        SsiApplication ssiApp4 = generateSsiAppAltered4();
        SsiApplication ssiApp5 = generateSsiAppAltered5();

        List<SsiApplication> ssiApps = new ArrayList<>();
        ssiApps.add(ssiApp1);
        ssiApps.add(ssiApp2);
        ssiApps.add(ssiApp3);
        ssiApps.add(ssiApp4);
        ssiApps.add(ssiApp5);

        String expDateStr = String.valueOf(LocalDateTime.now().plusDays(1).toEpochSecond(ZoneOffset.UTC));
        CredsAndExp cred = new CredsAndExp();
        cred.setExp(expDateStr);
        cred.setId("2WiYi1");
        CredsAndExp[] credIdAndExp = new CredsAndExp[]{cred};
        List<SsiApplication> oneItemList = new ArrayList<>();
        oneItemList.add(ssiApp1);

        Mockito.when(ethServ.getAllCaseUUID()).thenReturn(uuids);
        Mockito.when(ethServ.getCaseByUUID(anyString())).thenReturn(Optional.of(monitoredCase));
        Mockito.when(mongoServ.findByUuid(anyString())).thenReturn(Optional.of(ssiApp1));
        Mockito.when(mongoServ.findByTaxisAfmIn(anySet())).thenReturn(ssiApps);
        Mockito.when(mongoServ.findCredentialIdsByUuid(anyString())).thenReturn(credIdAndExp);
        Mockito.when(ethServ.checkRevocationStatus(anyString())).thenReturn(false);
        Mockito.when(mongoServ.findByMeterNumber(anyString())).thenReturn(oneItemList);
        Mockito.when(mongoServ.findByIban(anyString())).thenReturn(oneItemList);
        doNothing().when(ethServ).updateCase(any());

        monServ.startMonitoring(null);

        verify(ethServ, times(1)).updateCase(any());
        
    }

    @Test
    public void testStartMonitoringExpired(){

        MonitorService monServ = new MonitorServiceImpl(mongoServ, ethServ);

        List<String> uuids = new ArrayList<>();
        uuids.add("2WiYi1");

        Case monitoredCase = generateMockCase("2WiYi1", State.ACCEPTED, false, false);
        SsiApplication ssiApp1 = generateSsiApp1();
        SsiApplication ssiApp2 = generateSsiApp2();
        SsiApplication ssiApp3 = generateSsiApp3();

        List<SsiApplication> ssiApps = new ArrayList<>();
        ssiApps.add(ssiApp1);
        ssiApps.add(ssiApp2);
        ssiApps.add(ssiApp3);

        String expDateStr = String.valueOf(LocalDateTime.now().minusDays(1).toEpochSecond(ZoneOffset.UTC));
        CredsAndExp cred = new CredsAndExp();
        cred.setExp(expDateStr);
        cred.setId("2WiYi1");
        CredsAndExp[] credIdAndExp = new CredsAndExp[]{cred};
        List<SsiApplication> oneItemList = new ArrayList<>();
        oneItemList.add(ssiApp1);

        Mockito.when(ethServ.getAllCaseUUID()).thenReturn(uuids);
        Mockito.when(ethServ.getCaseByUUID(anyString())).thenReturn(Optional.of(monitoredCase));
        Mockito.when(mongoServ.findByUuid(anyString())).thenReturn(Optional.of(ssiApp1));
        Mockito.when(mongoServ.findByTaxisAfmIn(anySet())).thenReturn(ssiApps);
        Mockito.when(mongoServ.findCredentialIdsByUuid(anyString())).thenReturn(credIdAndExp);
        Mockito.when(ethServ.getCaseByUUID(anyString())).thenReturn(Optional.of(monitoredCase));
        Mockito.when(mongoServ.findByTaxisAfmIn(anySet())).thenReturn(ssiApps);
        doNothing().when(ethServ).updateCase(any());
        monServ.startMonitoring(null);

        verify(ethServ, times(1)).updateCase(any());
        
    }
    

    @Test
    public void testStartMonitoringRevoked(){

        MonitorService monServ = new MonitorServiceImpl(mongoServ, ethServ);

        List<String> uuids = new ArrayList<>();
        uuids.add("2WiYi1");

        Case monitoredCase = generateMockCase("2WiYi1", State.ACCEPTED, false, false);
        SsiApplication ssiApp1 = generateSsiAppAltered1();
        SsiApplication ssiApp2 = generateSsiAppAltered2();
        SsiApplication ssiApp3 = generateSsiAppAltered3();
        SsiApplication ssiApp4 = generateSsiAppAltered4();
        SsiApplication ssiApp5 = generateSsiAppAltered5();

        List<SsiApplication> ssiApps = new ArrayList<>();
        ssiApps.add(ssiApp1);
        ssiApps.add(ssiApp2);
        ssiApps.add(ssiApp3);
        //ssiApps.add(ssiApp4);
        ssiApps.add(ssiApp5);

        List<SsiApplication> allSsiApps = new ArrayList<>();
        allSsiApps.add(ssiApp1);
        allSsiApps.add(ssiApp2);
        allSsiApps.add(ssiApp3);
        allSsiApps.add(ssiApp4);
        allSsiApps.add(ssiApp5);

        String expDateStr = String.valueOf(LocalDateTime.now().plusDays(1).toEpochSecond(ZoneOffset.UTC));
        CredsAndExp cred = new CredsAndExp();
        cred.setExp(expDateStr);
        cred.setId("2WiYi1");
        CredsAndExp[] credIdAndExp = new CredsAndExp[]{cred};
        List<SsiApplication> oneItemList = new ArrayList<>();
        oneItemList.add(ssiApp1);

        Mockito.when(ethServ.getAllCaseUUID()).thenReturn(uuids);
        Mockito.when(ethServ.getCaseByUUID(anyString())).thenReturn(Optional.of(monitoredCase));
        Mockito.when(mongoServ.findByUuid(anyString())).thenReturn(Optional.of(ssiApp1));
        Mockito.when(mongoServ.findByTaxisAfmIn(anySet())).thenReturn(ssiApps);
        Mockito.when(mongoServ.findCredentialIdsByUuid(anyString())).thenReturn(credIdAndExp);
        Mockito.when(ethServ.checkRevocationStatus(anyString())).thenReturn(true);
        Mockito.when(ethServ.getCaseByUUID(anyString())).thenReturn(Optional.of(monitoredCase));
        Mockito.when(mongoServ.findByTaxisAfmIn(anySet())).thenReturn(allSsiApps);
        doNothing().when(ethServ).updateCase(any());
        monServ.startMonitoring(null);
        
        verify(ethServ, times(1)).updateCase(any());
        
    }

    @Test
    public void testStartMonitoringOlderThan6Months(){

        MonitorService monServ = new MonitorServiceImpl(mongoServ, ethServ);

        List<String> uuids = new ArrayList<>();
        uuids.add("2WiYi1");

        Case monitoredCase = generateMockCase("2WiYi1", State.ACCEPTED, false, true);
        SsiApplication ssiApp1 = generateSsiAppAltered1();
        SsiApplication ssiApp2 = generateSsiAppAltered2();
        SsiApplication ssiApp3 = generateSsiAppAltered3();
        SsiApplication ssiApp4 = generateSsiAppAltered4();
        SsiApplication ssiApp5 = generateSsiAppAltered5();

        List<SsiApplication> ssiApps = new ArrayList<>();
        ssiApps.add(ssiApp1);
        ssiApps.add(ssiApp2);
        ssiApps.add(ssiApp3);
        ssiApps.add(ssiApp4);
        ssiApps.add(ssiApp5);

        String expDateStr = String.valueOf(LocalDateTime.now().plusDays(1).toEpochSecond(ZoneOffset.UTC));
        CredsAndExp cred = new CredsAndExp();
        cred.setExp(expDateStr);
        cred.setId("2WiYi1");
        CredsAndExp[] credIdAndExp = new CredsAndExp[]{cred};
        List<SsiApplication> oneItemList = new ArrayList<>();
        oneItemList.add(ssiApp1);

        Mockito.when(ethServ.getAllCaseUUID()).thenReturn(uuids);
        Mockito.when(ethServ.getCaseByUUID(anyString())).thenReturn(Optional.of(monitoredCase));
        Mockito.when(mongoServ.findByUuid(anyString())).thenReturn(Optional.of(ssiApp1));
        Mockito.when(mongoServ.findByTaxisAfmIn(anySet())).thenReturn(ssiApps);
        Mockito.when(mongoServ.findCredentialIdsByUuid(anyString())).thenReturn(credIdAndExp);
        Mockito.when(ethServ.checkRevocationStatus(anyString())).thenReturn(false);
        doNothing().when(ethServ).updateCase(any());

        monServ.startMonitoring(null);

        verify(ethServ, times(1)).updateCase(any());
        
    }

    @Test
    public void testStartMonitoringNoApplication(){

        MonitorService monServ = new MonitorServiceImpl(mongoServ, ethServ);

        List<String> uuids = new ArrayList<>();
        uuids.add("2WiYi1");

        Case monitoredCase = generateMockCase("2WiYi1", State.ACCEPTED, false, false);
        SsiApplication ssiApp1 = generateSsiAppAltered1();
        SsiApplication ssiApp2 = generateSsiAppAltered2();
        SsiApplication ssiApp3 = generateSsiAppAltered3();
        SsiApplication ssiApp4 = generateSsiAppAltered4();
        SsiApplication ssiApp5 = generateSsiAppAltered5();

        List<SsiApplication> ssiApps = new ArrayList<>();
        ssiApps.add(ssiApp1);
        ssiApps.add(ssiApp2);
        ssiApps.add(ssiApp3);
        ssiApps.add(ssiApp4);
        ssiApps.add(ssiApp5);

        String expDateStr = String.valueOf(LocalDateTime.now().plusDays(1).toEpochSecond(ZoneOffset.UTC));
        CredsAndExp cred = new CredsAndExp();
        cred.setExp(expDateStr);
        cred.setId("2WiYi1");
        CredsAndExp[] credIdAndExp = new CredsAndExp[]{cred};
        List<SsiApplication> oneItemList = new ArrayList<>();
        oneItemList.add(ssiApp1);

        Mockito.when(ethServ.getAllCaseUUID()).thenReturn(uuids);
        Mockito.when(ethServ.getCaseByUUID(anyString())).thenReturn(Optional.of(monitoredCase));
        Mockito.when(mongoServ.findByUuid(anyString())).thenReturn(Optional.empty());
        doNothing().when(ethServ).updateCase(any());

        monServ.startMonitoring(null);

        verify(ethServ, times(1)).updateCase(any());
        
    }

    @Test
    public void testStartMonitoringCaseRejectedOld(){

        MonitorService monServ = new MonitorServiceImpl(mongoServ, ethServ);

        List<String> uuids = new ArrayList<>();
        uuids.add("2WiYi1");

        Case monitoredCase = generateMockCase("2WiYi1", State.REJECTED, true, false);
        SsiApplication ssiApp1 = generateSsiAppAltered1();
        SsiApplication ssiApp2 = generateSsiAppAltered2();
        SsiApplication ssiApp3 = generateSsiAppAltered3();
        SsiApplication ssiApp4 = generateSsiAppAltered4();
        SsiApplication ssiApp5 = generateSsiAppAltered5();

        List<SsiApplication> ssiApps = new ArrayList<>();
        ssiApps.add(ssiApp1);
        ssiApps.add(ssiApp2);
        ssiApps.add(ssiApp3);
        ssiApps.add(ssiApp4);
        ssiApps.add(ssiApp5);

        String expDateStr = String.valueOf(LocalDateTime.now().plusDays(1).toEpochSecond(ZoneOffset.UTC));
        CredsAndExp cred = new CredsAndExp();
        cred.setExp(expDateStr);
        cred.setId("2WiYi1");
        CredsAndExp[] credIdAndExp = new CredsAndExp[]{cred};
        List<SsiApplication> oneItemList = new ArrayList<>();
        oneItemList.add(ssiApp1);

        Mockito.when(ethServ.getAllCaseUUID()).thenReturn(uuids);
        Mockito.when(ethServ.getCaseByUUID(anyString())).thenReturn(Optional.of(monitoredCase));
        Mockito.when(mongoServ.findByUuid(anyString())).thenReturn(Optional.of(ssiApp1));
        Mockito.when(mongoServ.findUuidByTaxisAfmIn(anySet())).thenReturn(uuids);
        doNothing().when(mongoServ).deleteByUuid(anyString());

        monServ.startMonitoring(null);
        
    }

    @Test
    public void testStartMonitoringCaseRejected(){

        MonitorService monServ = new MonitorServiceImpl(mongoServ, ethServ);

        List<String> uuids = new ArrayList<>();
        uuids.add("2WiYi1");

        Case monitoredCase = generateMockCase("2WiYi1", State.REJECTED, false, false);
        SsiApplication ssiApp1 = generateSsiAppAltered1();
        SsiApplication ssiApp2 = generateSsiAppAltered2();
        SsiApplication ssiApp3 = generateSsiAppAltered3();
        SsiApplication ssiApp4 = generateSsiAppAltered4();
        SsiApplication ssiApp5 = generateSsiAppAltered5();

        List<SsiApplication> ssiApps = new ArrayList<>();
        ssiApps.add(ssiApp1);
        ssiApps.add(ssiApp2);
        ssiApps.add(ssiApp3);
        ssiApps.add(ssiApp4);
        ssiApps.add(ssiApp5);

        String expDateStr = String.valueOf(LocalDateTime.now().plusDays(1).toEpochSecond(ZoneOffset.UTC));
        CredsAndExp cred = new CredsAndExp();
        cred.setExp(expDateStr);
        cred.setId("2WiYi1");
        CredsAndExp[] credIdAndExp = new CredsAndExp[]{cred};
        List<SsiApplication> oneItemList = new ArrayList<>();
        oneItemList.add(ssiApp1);

        Mockito.when(ethServ.getAllCaseUUID()).thenReturn(uuids);
        Mockito.when(ethServ.getCaseByUUID(anyString())).thenReturn(Optional.of(monitoredCase));

        monServ.startMonitoring(null);
        
    }
}
