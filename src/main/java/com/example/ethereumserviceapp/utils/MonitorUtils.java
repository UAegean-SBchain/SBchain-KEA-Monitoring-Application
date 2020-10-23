/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.CasePayment;
import com.example.ethereumserviceapp.model.State;
import com.example.ethereumserviceapp.model.entities.SsiApplication;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author nikos
 */
@Slf4j
//@Service
public class MonitorUtils extends EthAppUtils{
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

    // mock projection
    public static void updateOffset(Case monitoredCase, SsiApplication ssiApp){

        //Mock credential date, this illustrates a date at which a credential has been modified prior to being updated in the system
        LocalDateTime date = LocalDateTime.of(2020, 8, 12, 10, 23 ,1);
        LocalDateTime startOfMonth = date.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(1);
        
        // this should probably call the payment calculation method and return the value that should have been paid for all paid days with the new credentials
        //BigDecimal fullMonthProjection = BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalPayment = BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP);
        BigDecimal projectedPayment = BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP);

        for(CasePayment payment:monitoredCase.getPaymentHistory()){
            //sum the payment of each month to calculate the total paid amount 
            totalPayment = totalPayment.add(payment.getPayment());
            // update iff the month of the date is equal or after the date of the modified credential 
            if(payment.getPaymentDate().minusMonths(1).isBefore(startOfMonth)){
                projectedPayment = projectedPayment.add(payment.getPayment());
                continue; 
            }
            BigDecimal fullMonthProjection = calculatePayment(monthDays(payment.getPaymentDate().minusMonths(1)), monitoredCase, ssiApp);
            BigDecimal projection = fullMonthProjection;

            // if the date of the payment is on the same month as the altered credential, calculate the payment only of the offset days
            if(payment.getPaymentDate().minusMonths(1).isBefore(date.withDayOfMonth(monthDays(date)).withHour(23).withMinute(59).withSecond(59))){
           
                Long paidDays = monitoredCase.getHistory().entrySet().stream().filter(
                    e -> e.getKey().getMonthValue() == date.getMonthValue() && e.getKey().isAfter(startOfMonth) && e.getValue().equals(State.ACCEPTED)).count();

                Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                    e -> e.getKey().getMonthValue() == date.getMonthValue() && e.getKey().getYear() == date.getYear() && e.getKey().getDayOfMonth()>=date.getDayOfMonth() && e.getValue().equals(State.ACCEPTED)).count();
               
                BigDecimal paymentPerDayActual = payment.getPayment().divide(BigDecimal.valueOf(paidDays), 2, RoundingMode.HALF_UP);

                projectedPayment = projectedPayment.add((BigDecimal.valueOf(paidDays - offsetDays ).multiply(paymentPerDayActual))
                .add(BigDecimal.valueOf(offsetDays).multiply(fullMonthProjection.divide(BigDecimal.valueOf(paidDays), 2, RoundingMode.HALF_UP))));
                
            } else{
                projectedPayment = projectedPayment.add(projection);
            } 
        }
        BigDecimal newOffset = totalPayment.subtract(projectedPayment);

        // if the offset in the case is diffrenent than the calculated offset the update the offset 
        if(newOffset.compareTo(monitoredCase.getOffset()) != 0){
            monitoredCase.setOffset(newOffset);
        }
    }
        
}
