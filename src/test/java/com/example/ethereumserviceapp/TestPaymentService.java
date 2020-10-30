package com.example.ethereumserviceapp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
public class TestPaymentService {

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
        mockList.add(generateMockSsiApp("2WiYi1"));

        Mockito.when(ethServ.getAllCaseUUID()).thenReturn(uuids);
        Mockito.when(ethServ.getCaseByUUID("2WiYi1")).thenReturn(Optional.of(generateMockCase("2WiYi1", State.ACCEPTED, false)));
        Mockito.when(mongoServ.findByUuid("2WiYi1")).thenReturn(Optional.of(generateMockSsiApp("2WiYi1")));
        Mockito.when(mongoServ.findByMeterNumber(any())).thenReturn(mockList);
        Mockito.when(mongoServ.findByHouseholdCompositionIn(any())).thenReturn(mockList);
        Mockito.when(mongoServ.findByIban(any())).thenReturn(mockList);
        doNothing().when(ethServ).addPayment(any(), any());

        paymentServ.startPayment();

        verify(ethServ, times(1)).addPayment(any(), any());
        
    }

    @Test
    public void testStartPaymentRejectedPayment(){

        PaymentService paymentServ = new PaymentServiceImpl(ethServ, mongoServ);

        List<String> uuids = new ArrayList<>();
        uuids.add("2WiYi1");
        //uuids.add("2WiYi2");
        List<SsiApplication> mockList = new ArrayList<>();
        mockList.add(generateMockSsiApp("2WiYi1"));

        Mockito.when(ethServ.getAllCaseUUID()).thenReturn(uuids);
        Mockito.when(ethServ.getCaseByUUID("2WiYi1")).thenReturn(Optional.of(generateMockCase("2WiYi1", State.REJECTED, false)));
        Mockito.when(mongoServ.findByUuid("2WiYi1")).thenReturn(Optional.of(generateMockSsiApp("2WiYi1")));
        Mockito.when(mongoServ.findByMeterNumber(any())).thenReturn(mockList);
        Mockito.when(mongoServ.findByHouseholdCompositionIn(any())).thenReturn(mockList);
        Mockito.when(mongoServ.findByIban(any())).thenReturn(mockList);
        doNothing().when(ethServ).addPayment(any(), any());

        paymentServ.startPayment();

        verify(ethServ, times(1)).addPayment(any(), any());
        
    }

    @Test
    public void testStartPaymentRejected(){

        PaymentService paymentServ = new PaymentServiceImpl(ethServ, mongoServ);

        List<String> uuids = new ArrayList<>();
        uuids.add("2WiYi1");
        //uuids.add("2WiYi2");
        List<SsiApplication> mockList = new ArrayList<>();
        mockList.add(generateMockSsiApp("2WiYi1"));

        Mockito.when(ethServ.getAllCaseUUID()).thenReturn(uuids);
        Mockito.when(ethServ.getCaseByUUID("2WiYi1")).thenReturn(Optional.of(generateMockCase("2WiYi1", State.REJECTED, true)));
        doNothing().when(ethServ).deleteCaseByUuid(any());

        paymentServ.startPayment();

        verify(ethServ, times(1)).deleteCaseByUuid(any());
        
    }
    
    private SsiApplication generateMockSsiApp(String uuid){
        SsiApplication ssiApp = new SsiApplication();
        ssiApp.setOtherBenefitsR("5");
        ssiApp.setUnemploymentBenefitR("5");
        ssiApp.setErgomeR("5");
        ssiApp.setRentIncomeR("5");
        ssiApp.setOtherIncomeR("5");
        ssiApp.setUuid(uuid);
        ssiApp.setLuxury("false");
        ssiApp.setTotalIncome("10");
        ssiApp.setHospitalized("true");
        ssiApp.setUnemployed("true");
        ssiApp.setEmploymentStatus("unemployed");
        ssiApp.setTaxisAfm("taxisAfm");
        Map<String, String> householdVal = new HashMap<>();
        householdVal.put("householdKey1", "householdVal1");
        Map[] householdArray = new Map[1];
        householdArray[0] = householdVal;
        ssiApp.setHouseholdComposition(householdArray);

        return ssiApp;
    }
    
    private Case generateMockCase(String uuid, State state, Boolean allRejected){
        
        Case monitoredCase = new Case();
        monitoredCase.setUuid(uuid);
        monitoredCase.setDate(LocalDateTime.now().minusDays(1));
        monitoredCase.setState(state);
        LinkedHashMap<LocalDateTime, State> history = new LinkedHashMap<>();
        Integer days = monthDays(LocalDateTime.now().minusMonths(1));
        if(allRejected && state.equals(State.REJECTED)){
            for(int i=0; i<days; i++){
                history.put(LocalDateTime.now().minusDays(days-i), State.REJECTED);
            }
        } else if(!allRejected && state.equals(State.REJECTED)) {
            for(int i=0; i<days; i++){
                if(i > 15){
                    history.put(LocalDateTime.now().minusDays(days-i), State.ACCEPTED);
                }else{
                    history.put(LocalDateTime.now().minusDays(days-i), State.REJECTED);
                }
            }
        } else {
            for(int i=0; i<days; i++){
                history.put(LocalDateTime.now().minusDays(days-i), State.ACCEPTED);
            }
        }
        
        monitoredCase.setHistory(history);
        monitoredCase.setOffset(BigDecimal.valueOf(3));

        return monitoredCase;

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
