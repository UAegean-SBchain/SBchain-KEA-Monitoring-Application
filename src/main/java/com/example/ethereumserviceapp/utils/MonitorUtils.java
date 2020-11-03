/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

            if (employmentStatus.equals("unemployed") && totalIncome < Long.valueOf(10000) && hospitalized.equals("true")) {
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

    public static Boolean hasGreenCard(String uuid){
        return true;
    }

    // mock projection
    public static void updateOffset(LocalDate credentialDate, Case monitoredCase, SsiApplication ssiApp){

        //Mock credential date, this illustrates a date at which a credential has been modified prior to being updated in the system
        //LocalDate credentialDate = LocalDate.of(2020, 8, 12);
        LocalDate startOfCredMonth = credentialDate.withDayOfMonth(1);
        
        // this should probably call the payment calculation method and return the value that should have been paid for all paid days with the new credentials
        //BigDecimal fullMonthProjection = BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalPayment = BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP);
        BigDecimal projectedPayment = BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP);

        for(CasePayment payment:monitoredCase.getPaymentHistory()){
            //sum the payment of each month to calculate the total paid amount 
            totalPayment = totalPayment.add(payment.getPayment());

            // payment concerns the previous month of the actual date of payment
            LocalDate paymentDate = payment.getPaymentDate().minusMonths(1).toLocalDate();

            // update iff the month of the date is equal or after the date of the modified credential 
            if(paymentDate.isBefore(startOfCredMonth)){
                projectedPayment = projectedPayment.add(payment.getPayment());
                continue; 
            }
            BigDecimal fullMonthProjection = calculatePayment(monthDays(paymentDate), BigDecimal.valueOf(0), ssiApp);
            if(fullMonthProjection == payment.getPayment()){
                projectedPayment = projectedPayment.add(payment.getPayment());
                continue; 
            }

            LocalDate endOfCredMonth = credentialDate.withDayOfMonth(monthDays(credentialDate));

            // if the date of the payment is on the same month as the altered credential, calculate the payment only of the offset days
            if(paymentDate.isBefore(endOfCredMonth)){
           
                Long paidDays = monitoredCase.getHistory().entrySet().stream().filter(
                    e -> e.getKey().toLocalDate().compareTo(startOfCredMonth) >=0 && e.getKey().toLocalDate().compareTo(endOfCredMonth) <=0 && e.getValue().equals(State.ACCEPTED)).count();

                Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                e -> e.getKey().toLocalDate().compareTo(credentialDate) >= 0 && e.getKey().toLocalDate().compareTo(endOfCredMonth) <=0 && e.getValue().equals(State.ACCEPTED)).count();
        
                // Long paidDays = monitoredCase.getHistory().entrySet().stream().filter(
                //     e -> e.getKey().getMonthValue() == credentialDate.getMonthValue() && e.getKey().isAfter(startOfCredMonth) && e.getValue().equals(State.ACCEPTED)).count();

                // Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                //     e -> e.getKey().getMonthValue() == credentialDate.getMonthValue() && e.getKey().getYear() == credentialDate.getYear() && e.getKey().getDayOfMonth()>=credentialDate.getDayOfMonth() && e.getValue().equals(State.ACCEPTED)).count();
               
                BigDecimal paymentPerDayActual = payment.getPayment().divide(BigDecimal.valueOf(paidDays), 2, RoundingMode.HALF_UP);

                projectedPayment = projectedPayment.add((BigDecimal.valueOf(paidDays - offsetDays ).multiply(paymentPerDayActual))
                .add(BigDecimal.valueOf(offsetDays).multiply(fullMonthProjection.divide(BigDecimal.valueOf(paidDays), 2, RoundingMode.HALF_UP))));
                
            } else{
                Long paidDays = monitoredCase.getHistory().entrySet().stream().filter(
                    e -> e.getKey().toLocalDate().compareTo(paymentDate.withDayOfMonth(1)) >=0 && e.getKey().toLocalDate().compareTo(paymentDate.withDayOfMonth(monthDays(paymentDate))) <=0 && e.getValue().equals(State.ACCEPTED)).count();

                // add the projected payment for all accepted dates during this month
                projectedPayment = projectedPayment.add(fullMonthProjection.divide(BigDecimal.valueOf(monthDays(paymentDate)), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(paidDays)));
            } 
        }
        BigDecimal newOffset = totalPayment.subtract(projectedPayment);
        monitoredCase.setOffset(monitoredCase.getOffset().add(newOffset));
        // if the offset in the case is diffrenent than the calculated offset the update the offset 
        // if(newOffset.compareTo(monitoredCase.getOffset()) != 0){
        //     monitoredCase.setOffset(newOffset);
        // }
    }
        
}
