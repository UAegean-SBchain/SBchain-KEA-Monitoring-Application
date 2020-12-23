package com.example.ethereumserviceapp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.State;
import com.example.ethereumserviceapp.model.entities.SsiApplication;
import com.example.ethereumserviceapp.service.EthereumService;
import com.example.ethereumserviceapp.service.MongoService;
import com.example.ethereumserviceapp.service.PaymentService;
import com.example.ethereumserviceapp.service.impl.PaymentServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class TestPaymentService extends TestUtils{

    @Mock
    EthereumService ethServ;

    @Mock
    MongoService mongoServ;

    @Test
    public void testStartPaymentAccepted(){

        PaymentService paymentServ = new PaymentServiceImpl(ethServ, mongoServ);

        List<String> uuids = new ArrayList<>();
        uuids.add("2WiYi1");
        //uuids.add("2WiYi2");
        List<SsiApplication> mockList = new ArrayList<>();
        mockList.add(generateSsiApp1());
        mockList.add(generateSsiApp2());
        mockList.add(generateSsiApp3());

        Mockito.when(ethServ.getAllCaseUUID()).thenReturn(uuids);
        Mockito.when(ethServ.getCaseByUUID(anyString())).thenReturn(Optional.of(generateMockCase("2WiYi1", State.ACCEPTED, false, false)));
        Mockito.when(mongoServ.findByUuid(anyString())).thenReturn(Optional.of(generateSsiApp1()));
        Mockito.when(mongoServ.findByTaxisAfmIn(anySet())).thenReturn(mockList);
        Mockito.when(mongoServ.findByTaxisAfmIn(anySet())).thenReturn(mockList);
        // Mockito.when(mongoServ.findByMeterNumber(any())).thenReturn(mockList);
        // Mockito.when(mongoServ.findByHouseholdCompositionIn(any())).thenReturn(mockList);
        // Mockito.when(mongoServ.findByIban(any())).thenReturn(mockList);
        doNothing().when(ethServ).addPayment(any(), any(), false);

        paymentServ.startScheduledPayment();

        verify(ethServ, times(1)).addPayment(any(), any(), false);
        
    }

    @Test
    public void testStartPaymentRejectedPayment(){

        PaymentService paymentServ = new PaymentServiceImpl(ethServ, mongoServ);

        List<String> uuids = new ArrayList<>();
        uuids.add("2WiYi1");
        //uuids.add("2WiYi2");
        List<SsiApplication> mockList = new ArrayList<>();
        mockList.add(generateSsiApp1());
        mockList.add(generateSsiApp2());
        mockList.add(generateSsiApp3());

        Mockito.when(ethServ.getAllCaseUUID()).thenReturn(uuids);
        Mockito.when(ethServ.getCaseByUUID(anyString())).thenReturn(Optional.of(generateMockCase("2WiYi1", State.REJECTED, false, false)));
        Mockito.when(mongoServ.findByUuid(anyString())).thenReturn(Optional.of(generateSsiApp1()));
        Mockito.when(mongoServ.findByTaxisAfmIn(anySet())).thenReturn(mockList);
        Mockito.when(mongoServ.findByTaxisAfmIn(anySet())).thenReturn(mockList);
        doNothing().when(ethServ).addPayment(any(), any(), false);

        paymentServ.startScheduledPayment();

        verify(ethServ, times(1)).addPayment(any(), any(), false);
    }

    @Test
    public void testStartPaymentRejected(){

        PaymentService paymentServ = new PaymentServiceImpl(ethServ, mongoServ);

        List<String> uuids = new ArrayList<>();
        uuids.add("2WiYi1");
        //uuids.add("2WiYi2");
        List<SsiApplication> mockList = new ArrayList<>();
        mockList.add(generateSsiApp1());
        mockList.add(generateSsiApp2());
        mockList.add(generateSsiApp3());

        Mockito.when(ethServ.getAllCaseUUID()).thenReturn(uuids);
        Mockito.when(ethServ.getCaseByUUID(anyString())).thenReturn(Optional.of(generateMockCase("2WiYi1", State.REJECTED, true, false)));
        Mockito.when(mongoServ.findByUuid(anyString())).thenReturn(Optional.of(generateSsiApp1()));
        Mockito.when(mongoServ.findByTaxisAfmIn(anySet())).thenReturn(mockList);
        doNothing().when(ethServ).deleteCaseByUuid(any());

        paymentServ.startScheduledPayment();

        verify(ethServ, times(1)).deleteCaseByUuid(any());
        
    }
    
    // private Case generateMockCase(String uuid, State state, Boolean allRejected){
        
    //     Case monitoredCase = new Case();
    //     monitoredCase.setUuid(uuid);
    //     monitoredCase.setDate(LocalDateTime.now().withDayOfMonth(1));
    //     monitoredCase.setState(state);
    //     LinkedHashMap<LocalDateTime, State> history = new LinkedHashMap<>();
    //     Integer days = monthDays(LocalDateTime.now().minusMonths(1));
    //     if(allRejected && state.equals(State.REJECTED)){
    //         for(int i=1; i<=days; i++){
    //             history.put(LocalDateTime.now().minusMonths(1).withDayOfMonth(i), State.REJECTED);
    //         }
    //     } else if(!allRejected && state.equals(State.REJECTED)) {
    //         for(int i=1; i<=days; i++){
    //             if(i > 15){
    //                 history.put(LocalDateTime.now().minusMonths(1).withDayOfMonth(i), State.ACCEPTED);
    //             }else{
    //                 history.put(LocalDateTime.now().minusMonths(1).withDayOfMonth(i), State.REJECTED);
    //             }
    //         }
    //     } else {
    //         for(int i=1; i<=days; i++){
    //             history.put(LocalDateTime.now().minusMonths(1).withDayOfMonth(i), State.ACCEPTED);
    //         }
    //     }
        
    //     monitoredCase.setHistory(history);
    //     monitoredCase.setOffset(BigDecimal.valueOf(0));

    //     return monitoredCase;

    // }
}
