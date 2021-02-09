package com.example.ethereumserviceapp.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.entities.SsiApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HelperService {
    
    @Autowired
    private EthereumService ethService;

    @Autowired
    private MongoService mongoService;

    @Autowired
    private MonitorService monitorService;

    @Autowired
    private PaymentService paymentService;

    public void addTestCase(String uuid){
        Optional<SsiApplication> ssiApp = mongoService.findByUuid(uuid);
        if(ssiApp.isPresent()){
            Case caseToAdd = new Case();
            caseToAdd.setDate(LocalDateTime.of(ssiApp.get().getTime(), LocalTime.of(00, 00, 00)));
            caseToAdd.setUuid(uuid);
            ethService.addCase(caseToAdd);
        }
    }

    public void runMonitoring(String startDate, String numDays, double pValue){
        
        String startDateFixed = startDate.replace("T", " ");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime currentDate = LocalDateTime.parse(startDateFixed, formatter);
        LocalDateTime endDate = currentDate.plusDays(Long.valueOf(numDays));

        while(currentDate.compareTo(endDate) <0){
            monitorService.startMonitoring(currentDate, false, pValue, true);
                if(currentDate.getDayOfMonth() == 1){
                    paymentService.startPayment(currentDate, false);
                }
            currentDate = currentDate.plusDays(1);
        }

    }

    // public void runMonitoringOnCase(String uuid){
    //     Optional<Case> monitoredCase = ethService.getCaseByUUID(uuid);

    //     if(!monitoredCase.isPresent()){
    //         return;
    //     }

    //     LocalDateTime startDate = monitoredCase.get().getDate();
    //     LocalDateTime endDate = startDate.plusMonths(6).isBefore(LocalDateTime.now())? startDate.plusMonths(6) : LocalDateTime.now();

    //     LocalDateTime currentDate = startDate;
    //     while(currentDate.compareTo(endDate) <=0){
    //         monitorService.startMonitoring(currentDate, uuid, true);
    //         if(currentDate.getDayOfMonth() == 1){
    //             paymentService.startPayment(currentDate, uuid, true);
    //         }
    //         currentDate = currentDate.plusDays(1);
    //     }

    // }
}
