package com.example.ethereumserviceapp.utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.entities.SsiApplication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EthAppUtils {

    private static BigDecimal povertyLimit = BigDecimal.valueOf(300);

    public static BigDecimal calculatePayment(Integer days, BigDecimal offset, SsiApplication ssiApp){

        Integer numDays = monthDays(LocalDateTime.now().minusMonths(Long.valueOf(1)));

        BigDecimal totalDailyValue = (
                povertyLimit.subtract(BigDecimal.valueOf(Long.parseLong(ssiApp.getRentIncomeR())))
                .subtract(BigDecimal.valueOf(Long.parseLong(ssiApp.getOtherIncomeR() == null? "0" : ssiApp.getOtherIncomeR())))
                .subtract(BigDecimal.valueOf(Long.parseLong(ssiApp.getMonthlyIncome() == null? "0" : ssiApp.getMonthlyIncome())))
                .subtract(BigDecimal.valueOf(Long.parseLong(ssiApp.getOtherBenefitsR() == null? "0" : ssiApp.getOtherBenefitsR() )))
                .subtract(BigDecimal.valueOf(Long.parseLong(ssiApp.getUnemploymentBenefitR() == null? "0" : ssiApp.getUnemploymentBenefitR())))
                ).divide(BigDecimal.valueOf(numDays));

        BigDecimal valueToBePaid = (BigDecimal.valueOf(days).multiply(totalDailyValue)).subtract(offset);

        return valueToBePaid;
    }

    protected static Integer monthDays(LocalDateTime date) {

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
