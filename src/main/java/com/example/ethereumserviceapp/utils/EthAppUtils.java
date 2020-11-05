package com.example.ethereumserviceapp.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;

import com.example.ethereumserviceapp.model.entities.SsiApplication;
import com.example.ethereumserviceapp.model.entities.SsiApplicationTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EthAppUtils {

    public static BigDecimal calculatePayment(Integer days, BigDecimal offset, SsiApplication ssiApp){

        // BigDecimal paymentThresshold = BigDecimal.valueOf(0);
        // Map<String, String>[] houseHold = ssiApp.getHouseholdComposition();
        // Integer adultCount = 0;
        // Integer minorCount = 0;

        // for(int i=0; i<houseHold.length; i++){
        //     if(Integer.valueOf(houseHold[i].entrySet().iterator().next().getValue()) >= 18){
        //         adultCount++;
        //     } else{
        //         minorCount++;
        //     }
        // }

        // // remove one adult because the first one has a fixed payment value of 200
        // if(adultCount == 0 && minorCount > 0){
        //     minorCount = adultCount - 1;
        // } else if(adultCount == 1 && minorCount > 0){
        //     minorCount--;
        // } else if ((adultCount == 1  && minorCount == 0) || adultCount >=2 ){
        //     adultCount--;
        // }
        // paymentThresshold = BigDecimal.valueOf(6).multiply(BigDecimal.valueOf(200)
        //     .add((BigDecimal.valueOf(adultCount).multiply(BigDecimal.valueOf(100))
        //     .add(BigDecimal.valueOf(minorCount).multiply(BigDecimal.valueOf(50))))));

        Integer numDays = monthDays(LocalDate.now().minusMonths(1));

        // BigDecimal totalIncome = (//salaries
        //         BigDecimal.valueOf(Long.parseLong(ssiApp.getSalariesR()== null? "0" : ssiApp.getSalariesR())).subtract(BigDecimal.valueOf(Long.parseLong(ssiApp.getSalariesR()== null? "0" : ssiApp.getSalariesR())).multiply(BigDecimal.valueOf(0.2)))
        //     .add( //pensions
        //         BigDecimal.valueOf(Long.parseLong(ssiApp.getPensionsR() == null? "0" : ssiApp.getPensionsR()))
        //     ).add(//farming
        //         BigDecimal.valueOf(Long.parseLong(ssiApp.getFarmingR() == null? "0" : ssiApp.getFarmingR()))
        //     ).add(//freelance
        //         BigDecimal.valueOf(Long.parseLong(ssiApp.getFreelanceR() == null? "0" : ssiApp.getFreelanceR()))
        //     ).add(//other benefits
        //         BigDecimal.valueOf(Long.parseLong(ssiApp.getOtherBenefitsR() == null? "0" : ssiApp.getOtherBenefitsR()))
        //     ).add(//deposits
        //         BigDecimal.valueOf(Long.parseLong(ssiApp.getDepositsA() == null? "0" : ssiApp.getDepositsA()))
        //     ).add(//domestic real estate
        //         BigDecimal.valueOf(Long.parseLong(ssiApp.getDomesticRealEstateA() == null? "0" : ssiApp.getDomesticRealEstateA()))
        //     ).add(//foreign real estate
        //         BigDecimal.valueOf(Long.parseLong(ssiApp.getForeignRealEstateA() == null? "0" : ssiApp.getForeignRealEstateA()))
        //     )
        //     ).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

        // if(paymentThresshold.compareTo(totalIncome)<= 0){
        //     return BigDecimal.valueOf(0);
        // }
        // BigDecimal totalMonthlyValue = (paymentThresshold.subtract(totalIncome)).divide(BigDecimal.valueOf(6), 2, RoundingMode.HALF_UP);

        BigDecimal totalMonthlyValue = getTotalMonthlyValue(ssiApp);

        //maximum monthly allowance is 900
        if(totalMonthlyValue.compareTo(BigDecimal.valueOf(900)) > 0){
            totalMonthlyValue = BigDecimal.valueOf(900);
        }

        if(numDays == days){
            return totalMonthlyValue;
            //return totalMonthlyValue.subtract(offset);
        }
        BigDecimal totalDailyValue = totalMonthlyValue.divide(BigDecimal.valueOf(numDays), 2, RoundingMode.HALF_UP);

        //BigDecimal valueToBePaid = (BigDecimal.valueOf(days).multiply(totalDailyValue)).subtract(offset);
        BigDecimal valueToBePaid = BigDecimal.valueOf(days).multiply(totalDailyValue);

        return valueToBePaid;
    }

    public static BigDecimal getTotalMonthlyValue(SsiApplication ssiApp){
        BigDecimal paymentThresshold = BigDecimal.valueOf(0);
        Map<String, String>[] houseHold = ssiApp.getHouseholdComposition();
        Integer adultCount = 0;
        Integer minorCount = 0;

        for(int i=0; i<houseHold.length; i++){
            if(Integer.valueOf(houseHold[i].entrySet().iterator().next().getValue()) >= 18){
                adultCount++;
            } else{
                minorCount++;
            }
        }

        // remove one adult because the first one has a fixed payment value of 200
        if(adultCount == 0 && minorCount > 0){
            adultCount = minorCount - 1;
        } else if(adultCount == 1 && ssiApp.getParenthood().equals("single") && minorCount > 0){
            minorCount--;
        } else if ((adultCount == 1  && minorCount == 0) || adultCount >=2 ){
            adultCount--;
        } else if(adultCount == 1 && !ssiApp.getParenthood().equals("single")){
            adultCount = adultCount + minorCount -1;
        }
        paymentThresshold = BigDecimal.valueOf(6).multiply(BigDecimal.valueOf(200)
            .add((BigDecimal.valueOf(adultCount).multiply(BigDecimal.valueOf(100))
            .add(BigDecimal.valueOf(minorCount).multiply(BigDecimal.valueOf(50))))));

        BigDecimal totalIncome = (//salaries
                BigDecimal.valueOf(Long.parseLong(ssiApp.getSalariesR()== null? "0" : ssiApp.getSalariesR())).subtract(BigDecimal.valueOf(Long.parseLong(ssiApp.getSalariesR()== null? "0" : ssiApp.getSalariesR())).multiply(BigDecimal.valueOf(0.2)))
            .add( //pensions
                BigDecimal.valueOf(Long.parseLong(ssiApp.getPensionsR() == null? "0" : ssiApp.getPensionsR()))
            ).add(//farming
                BigDecimal.valueOf(Long.parseLong(ssiApp.getFarmingR() == null? "0" : ssiApp.getFarmingR()))
            ).add(//freelance
                BigDecimal.valueOf(Long.parseLong(ssiApp.getFreelanceR() == null? "0" : ssiApp.getFreelanceR()))
            ).add(//other benefits
                BigDecimal.valueOf(Long.parseLong(ssiApp.getOtherBenefitsR() == null? "0" : ssiApp.getOtherBenefitsR()))
            ).add(//deposits
                BigDecimal.valueOf(Long.parseLong(ssiApp.getDepositsA() == null? "0" : ssiApp.getDepositsA()))
            ).add(//domestic real estate
                BigDecimal.valueOf(Long.parseLong(ssiApp.getDomesticRealEstateA() == null? "0" : ssiApp.getDomesticRealEstateA()))
            ).add(//foreign real estate
                BigDecimal.valueOf(Long.parseLong(ssiApp.getForeignRealEstateA() == null? "0" : ssiApp.getForeignRealEstateA()))
            )
            ).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

        if(paymentThresshold.compareTo(totalIncome)<= 0){
            return BigDecimal.valueOf(0);
        }
        return (paymentThresshold.subtract(totalIncome)).divide(BigDecimal.valueOf(6), 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculatePayment2(Integer numDays, Integer days, SsiApplicationTest ssiApp, BigDecimal offset){

        //Integer numDays = monthDays(LocalDate.now().minusMonths(1));

        BigDecimal totalMonthlyValue = getTotalMonthlyValue2(ssiApp);

        //maximum monthly allowance is 900
        if(totalMonthlyValue.compareTo(BigDecimal.valueOf(900)) > 0){
            totalMonthlyValue = BigDecimal.valueOf(900);
        }

        if(numDays == days){
            return totalMonthlyValue;
            //return totalMonthlyValue.subtract(offset);
        }
        BigDecimal totalDailyValue = totalMonthlyValue.divide(BigDecimal.valueOf(numDays), 2, RoundingMode.HALF_UP);

        //BigDecimal valueToBePaid = (BigDecimal.valueOf(days).multiply(totalDailyValue)).subtract(offset);
        BigDecimal valueToBePaid = BigDecimal.valueOf(days).multiply(totalDailyValue);

        return valueToBePaid;
    }

    public static BigDecimal getTotalMonthlyValue2(SsiApplicationTest ssiApp){
        BigDecimal paymentThresshold = BigDecimal.valueOf(0);
        Map<String, String>[] houseHold = ssiApp.getHouseholdComposition();
        Integer adultCount = 0;
        Integer minorCount = 0;

        for(int i=0; i<houseHold.length; i++){
            if(Integer.valueOf(houseHold[i].entrySet().iterator().next().getValue()) >= 18){
                adultCount++;
            } else{
                minorCount++;
            }
        }

        // remove one adult because the first one has a fixed payment value of 200
        if(adultCount == 0 && minorCount > 0){
            adultCount = minorCount - 1;
        } else if(adultCount == 1 && ssiApp.getParenthood().equals("single") && minorCount > 0){
            minorCount--;
        } else if ((adultCount == 1  && minorCount == 0) || adultCount >=2 ){
            adultCount--;
        } else if(adultCount == 1 && !ssiApp.getParenthood().equals("single")){
            adultCount = adultCount + minorCount -1;
        }
        paymentThresshold = BigDecimal.valueOf(6).multiply(BigDecimal.valueOf(200)
            .add((BigDecimal.valueOf(adultCount).multiply(BigDecimal.valueOf(100))
            .add(BigDecimal.valueOf(minorCount).multiply(BigDecimal.valueOf(50))))));

        BigDecimal totalIncome = (//salaries
                BigDecimal.valueOf(Long.parseLong(ssiApp.getSalariesR()== null? "0" : ssiApp.getSalariesR())).subtract(BigDecimal.valueOf(Long.parseLong(ssiApp.getSalariesR()== null? "0" : ssiApp.getSalariesR())).multiply(BigDecimal.valueOf(0.2)))
            .add( //pensions
                BigDecimal.valueOf(Long.parseLong(ssiApp.getPensionsR() == null? "0" : ssiApp.getPensionsR()))
            ).add(//farming
                BigDecimal.valueOf(Long.parseLong(ssiApp.getFarmingR() == null? "0" : ssiApp.getFarmingR()))
            ).add(//freelance
                BigDecimal.valueOf(Long.parseLong(ssiApp.getFreelanceR() == null? "0" : ssiApp.getFreelanceR()))
            ).add(//other benefits
                BigDecimal.valueOf(Long.parseLong(ssiApp.getOtherBenefitsR() == null? "0" : ssiApp.getOtherBenefitsR()))
            ).add(//deposits
                BigDecimal.valueOf(Long.parseLong(ssiApp.getDepositsA() == null? "0" : ssiApp.getDepositsA()))
            ).add(//domestic real estate
                BigDecimal.valueOf(Long.parseLong(ssiApp.getDomesticRealEstateA() == null? "0" : ssiApp.getDomesticRealEstateA()))
            ).add(//foreign real estate
                BigDecimal.valueOf(Long.parseLong(ssiApp.getForeignRealEstateA() == null? "0" : ssiApp.getForeignRealEstateA()))
            )
            ).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

        if(paymentThresshold.compareTo(totalIncome)<= 0){
            return BigDecimal.valueOf(0);
        }
        return (paymentThresshold.subtract(totalIncome)).divide(BigDecimal.valueOf(6), 2, RoundingMode.HALF_UP);
    }

    protected static Integer monthDays(LocalDate date) {

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
