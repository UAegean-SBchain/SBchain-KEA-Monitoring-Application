/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp.utils;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.CasePayment;
import com.example.ethereumserviceapp.model.State;
import com.example.ethereumserviceapp.model.entities.SsiApplication;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author nikos
 */
@Slf4j
public class MonitorUtils {
//mock checks replace with correct ones

    public static Boolean isApplicationAccepted(SsiApplication ssiApp) {
        try {
            String employmentStatus = ssiApp.getEmploymentStatus();
            String hospitalized = ssiApp.getHospitalized();
            Long totalIncome = Long.valueOf(ssiApp.getTotalIncome());
            ssiApp.getTaxisFamilyName();
            ssiApp.getTaxisFirstName();

            if (employmentStatus.equals("unemployed") || totalIncome < Long.valueOf(10000) || hospitalized.equals("true")) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }
                
    public static Boolean isCaseOlderThanSixMonths(LocalDateTime firstAcceptedDate){

        if(LocalDateTime.now().isAfter(firstAcceptedDate.plusMonths(6))){
            return true;
        }

        return false;
    }

    //mock method, fill up when more information is available
    public static Boolean checkExternalSources(){
        return true;
    }

    

    // public static BigInteger calculateOffset(Case monitoredCase, LocalDateTime date, SsiApplication ssiApp){

    //     CasePayment paymentToUpdate = new CasePayment();

    //     for(CasePayment payment:monitoredCase.getPaymentHistory()){
    //         if(payment.getPaymentDate().getMonthValue() == date.getMonthValue()){
    //             paymentToUpdate = payment;
    //             break;
    //         }
    //     }

    //     BigInteger actualPayment = paymentToUpdate.getPayment();
       
    //     BigInteger projection = calculatePaymentProjection(ssiApp, date, monitoredCase, paymentToUpdate);

    //     return projection.subtract(actualPayment);
    // }
        
}
