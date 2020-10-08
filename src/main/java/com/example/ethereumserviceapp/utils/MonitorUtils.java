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

    // mock projection
    public void updateOffset(Case monitoredCase){

        //Mock credential date, this illustrates a date at which a credential has been modified prior to being updated in the system
        LocalDateTime date = LocalDateTime.of(2020, 8, 12, 10, 23 ,1);
        LocalDateTime startOfMonth = date.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        
        // this should probably call the payment calculation method and return the value that should have been paid for all paid days with the new credentials
        BigInteger fullMonthProjection = BigInteger.valueOf(0);

        BigInteger totalPayment = BigInteger.valueOf(0);
        BigInteger projectedPayment = BigInteger.valueOf(0);

        for(CasePayment payment:monitoredCase.getPaymentHistory()){

            totalPayment.add(payment.getPayment());
            // update iff the month of the date is equal or after the date of the modified credential 
            if(payment.getPaymentDate().isBefore(startOfMonth)){
                projectedPayment.add(payment.getPayment());
                continue; 
            }
           BigInteger projection = fullMonthProjection;

           // if the date of the payment is on the same month as the altered credential, calculate the payment only of the offset days
           if(payment.getPaymentDate().isBefore(date.withDayOfMonth(monthDays(date)).withHour(23).withMinute(59).withSecond(59))){
           
                Long paidDays = monitoredCase.getHistory().entrySet().stream().filter(
                    e -> e.getKey().getMonthValue() == date.getMonthValue() && e.getKey().isAfter(startOfMonth) && e.getValue().equals(State.ACCEPTED)).count();

                Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                    e -> e.getKey().getMonthValue() == date.getMonthValue() && e.getKey().getYear() == date.getYear() && e.getKey().getDayOfMonth()>=date.getDayOfMonth() && e.getValue().equals(State.ACCEPTED)).count();
           
                BigInteger paymentPerDayActual = payment.getPayment().divide(BigInteger.valueOf(paidDays));

                projectedPayment.add((BigInteger.valueOf(paidDays - offsetDays ).multiply(paymentPerDayActual))
                .add(BigInteger.valueOf(offsetDays).multiply(fullMonthProjection.divide(BigInteger.valueOf(paidDays)))));
            } else{
                projectedPayment.add(projection);
            } 
        }
        BigInteger newOffset = totalPayment.subtract(projectedPayment);

        // if the offset in the case is diffrenent than the calculated offset the update the offset 
        if(newOffset.compareTo(monitoredCase.getOffset()) != 0){
            monitoredCase.setOffset(newOffset);
        }
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
