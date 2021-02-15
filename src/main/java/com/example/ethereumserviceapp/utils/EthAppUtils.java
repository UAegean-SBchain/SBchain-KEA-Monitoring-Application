package com.example.ethereumserviceapp.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.ethereumserviceapp.model.HouseholdMember;
import com.example.ethereumserviceapp.model.entities.SsiApplication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EthAppUtils {

    public static SsiApplication aggregateHouseholdValues(List<SsiApplication> householdApps){

        SsiApplication principalApp = new SsiApplication();

        if(householdApps.size() > 1){
            principalApp = householdApps.stream().filter(h -> h.getTaxisAfm().equals(h.getHouseholdPrincipal().getAfm())).collect(Collectors.toList()).get(0);
        } else {
            principalApp = householdApps.get(0);
        }
        //SsiApplication principalApp = householdApps.stream().filter(h -> h.getTaxisAfm().equals(h.getHouseholdPrincipal().getAfm())).collect(Collectors.toList()).get(0);
        
        SsiApplication ssiApp = new SsiApplication();

        ssiApp.setHouseholdComposition(principalApp.getHouseholdComposition());
        ssiApp.setTaxisAfm(principalApp.getTaxisAfm());
        BigDecimal salaries = BigDecimal.ZERO;
        BigDecimal pensions = BigDecimal.ZERO;
        BigDecimal farming = BigDecimal.ZERO;
        BigDecimal freelance = BigDecimal.ZERO;
        BigDecimal otherBnfts = BigDecimal.ZERO;
        BigDecimal deposits = BigDecimal.ZERO;
        BigDecimal domesticRe = BigDecimal.ZERO;
        BigDecimal foreignRe = BigDecimal.ZERO;
        BigDecimal unemplBenefit = BigDecimal.ZERO;
        BigDecimal ergome = BigDecimal.ZERO;
        
        for(SsiApplication hhApp:householdApps){
             //salaries = salaries.add(new BigDecimal(hhApp.getSalariesR()== null? "0" : hhApp.getSalariesR()).subtract(new BigDecimal(hhApp.getSalariesR()== null? "0" : hhApp.getSalariesR()).multiply(BigDecimal.valueOf(0.2))));
             salaries = salaries.add(new BigDecimal(hhApp.getSalariesR() == null? "0" : hhApp.getSalariesR()));
             pensions = pensions.add(new BigDecimal(hhApp.getPensionsR() == null? "0" : hhApp.getPensionsR()));
             farming = farming.add(new BigDecimal(hhApp.getFarmingR() == null? "0" : hhApp.getFarmingR()));
             freelance = freelance.add(new BigDecimal(hhApp.getFreelanceR() == null? "0" : hhApp.getFreelanceR()));
             otherBnfts = otherBnfts.add(new BigDecimal(hhApp.getOtherBenefitsR() == null? "0" : hhApp.getOtherBenefitsR()));
             deposits = deposits.add(new BigDecimal(hhApp.getDepositsA() == null? "0" : hhApp.getDepositsA()));
             domesticRe = domesticRe.add(new BigDecimal(hhApp.getDomesticRealEstateA() == null? "0" : hhApp.getDomesticRealEstateA()));
             foreignRe = foreignRe.add(new BigDecimal(hhApp.getForeignRealEstateA() == null? "0" : hhApp.getForeignRealEstateA()));
             unemplBenefit = unemplBenefit.add(new BigDecimal(hhApp.getUnemploymentBenefitR() == null? "0" : hhApp.getUnemploymentBenefitR()));
             ergome = ergome.add(new BigDecimal(hhApp.getErgomeR() == null? "0" : hhApp.getErgomeR()));
        }

        salaries = salaries.subtract(salaries.multiply(BigDecimal.valueOf(0.2)));

        ssiApp.setSalariesR(String.valueOf(salaries));
        ssiApp.setPensionsR(String.valueOf(pensions));
        ssiApp.setFarmingR(String.valueOf(farming));
        ssiApp.setFreelanceR(String.valueOf(freelance));
        ssiApp.setOtherBenefitsR(String.valueOf(otherBnfts));
        ssiApp.setDepositsA(String.valueOf(deposits));
        ssiApp.setDomesticRealEstateA(String.valueOf(domesticRe));
        ssiApp.setForeignRealEstateA(String.valueOf(foreignRe));
        ssiApp.setUnemploymentBenefitR(String.valueOf(unemplBenefit));
        ssiApp.setErgomeR(String.valueOf(ergome));

        
        // ssiApp.setSalariesR(String.valueOf(householdApps.stream().map(x -> new BigDecimal(x.getSalariesR()== null? "0" : x.getSalariesR()).subtract(new BigDecimal(x.getSalariesR()== null? "0" : x.getSalariesR()).multiply(BigDecimal.valueOf(0.2))))
        //         .reduce(BigDecimal.ZERO, BigDecimal::add)));

        // ssiApp.setPensionsR(String.valueOf(householdApps.stream().map(x -> new BigDecimal(x.getPensionsR() == null? "0" : x.getPensionsR()))
        // .reduce(BigDecimal.ZERO, BigDecimal::add)));

        // ssiApp.setFarmingR(String.valueOf(householdApps.stream().map(x -> new BigDecimal(x.getFarmingR() == null? "0" : x.getFarmingR()))
        //         .reduce(BigDecimal.ZERO, BigDecimal::add)));

        // ssiApp.setFreelanceR(String.valueOf(householdApps.stream().map(x -> new BigDecimal(x.getFreelanceR() == null? "0" : x.getFreelanceR()))
        //         .reduce(BigDecimal.ZERO, BigDecimal::add)));

        // ssiApp.setOtherBenefitsR(String.valueOf(householdApps.stream().map(x -> new BigDecimal(x.getOtherBenefitsR() == null? "0" : x.getOtherBenefitsR()))
        //         .reduce(BigDecimal.ZERO, BigDecimal::add)));

        // ssiApp.setDepositsA(String.valueOf(householdApps.stream().map(x -> new BigDecimal(x.getDepositsA() == null? "0" : x.getDepositsA()))
        //         .reduce(BigDecimal.ZERO, BigDecimal::add)));

        // ssiApp.setDomesticRealEstateA(String.valueOf(householdApps.stream().map(x -> new BigDecimal(x.getDomesticRealEstateA() == null? "0" : x.getDomesticRealEstateA()))
        //         .reduce(BigDecimal.ZERO, BigDecimal::add)));

        // ssiApp.setForeignRealEstateA(String.valueOf(householdApps.stream().map(x -> new BigDecimal(x.getForeignRealEstateA() == null? "0" : x.getForeignRealEstateA()))
        //         .reduce(BigDecimal.ZERO, BigDecimal::add)));

        // ssiApp.setUnemploymentBenefitR(String.valueOf(householdApps.stream().map(x -> new BigDecimal(x.getUnemploymentBenefitR() == null? "0" : x.getUnemploymentBenefitR()))
        //         .reduce(BigDecimal.ZERO, BigDecimal::add)));

        return ssiApp;
    }

    public static BigDecimal calculatePayment(Integer numDays, Integer days, SsiApplication ssiApp, LocalDate referenceDate){

        //if the total days are the same as the calculated days then return the total ammount
        if(numDays == days){
            BigDecimal totalMonthlyValue = getTotalMonthlyValue(ssiApp, referenceDate);
    
            //maximum monthly allowance is 900
            if(totalMonthlyValue.compareTo(BigDecimal.valueOf(900)) > 0){
                totalMonthlyValue = BigDecimal.valueOf(900);
            }
            return totalMonthlyValue;
        }
        BigDecimal valueToBePaid = BigDecimal.valueOf(days).multiply(calculateDailyPayment(numDays, days, ssiApp, referenceDate));

        return valueToBePaid;
    }

    public static BigDecimal calculateDailyPayment(Integer numDays, Integer days, SsiApplication ssiApp, LocalDate referenceDate){

        BigDecimal totalMonthlyValue = getTotalMonthlyValue(ssiApp, referenceDate);

        //maximum monthly allowance is 900
        if(totalMonthlyValue.compareTo(BigDecimal.valueOf(900)) > 0){
            totalMonthlyValue = BigDecimal.valueOf(900);
        }

        BigDecimal totalDailyValue = totalMonthlyValue.divide(BigDecimal.valueOf(numDays), 2, RoundingMode.HALF_UP);

        return totalDailyValue;
    }

    public static BigDecimal getTotalMonthlyValue(SsiApplication ssiApp, LocalDate date){
        BigDecimal paymentThresshold = BigDecimal.ZERO;
        List<HouseholdMember> household = ssiApp.getHouseholdComposition();
        final LocalDate referenceDate = date == null? LocalDate.now(): date;

        Long adults = household.stream().filter(h -> calculateAge(DateUtils.dateStringToLD(h.getDateOfBirth()), referenceDate) >= 18).count();
        Integer adultCount = adults.intValue();
        Integer minorCount = household.size() - adultCount;

        // remove one adult because the first one has a fixed payment value of 200
        if(adultCount == 0 && minorCount > 0){
            adultCount = minorCount - 1;
        } else if(adultCount == 1 && ssiApp.getParenthood() != null && ssiApp.getParenthood().equals("single") && minorCount > 0){
            minorCount--;
        } else if ((adultCount == 1  && minorCount == 0) || adultCount >=2 ){
            adultCount--;
        } else if(adultCount == 1 && !ssiApp.getParenthood().equals("single")){
            adultCount = adultCount + minorCount -1;
        }
//        log.info("adult count :{}, minor count :{}", adultCount, minorCount);

        paymentThresshold = BigDecimal.valueOf(6).multiply(BigDecimal.valueOf(200)
            .add((BigDecimal.valueOf(adultCount).multiply(BigDecimal.valueOf(100))
            .add(BigDecimal.valueOf(minorCount).multiply(BigDecimal.valueOf(50))))));

            BigDecimal salaries = new BigDecimal(ssiApp.getSalariesR()== null? "0" : ssiApp.getSalariesR());//.subtract(new BigDecimal(ssiApp.getSalariesR()== null? "0" : ssiApp.getSalariesR()).multiply(BigDecimal.valueOf(0.2)));
            BigDecimal pensions = new BigDecimal(ssiApp.getPensionsR() == null? "0" : ssiApp.getPensionsR());
            BigDecimal farming = new BigDecimal(ssiApp.getFarmingR() == null? "0" : ssiApp.getFarmingR());
            BigDecimal freelance = new BigDecimal(ssiApp.getFreelanceR() == null? "0" : ssiApp.getFreelanceR());
            BigDecimal otherBnfts = new BigDecimal(ssiApp.getOtherBenefitsR() == null? "0" : ssiApp.getOtherBenefitsR());
            BigDecimal deposits = new BigDecimal(ssiApp.getDepositsA() == null? "0" : ssiApp.getDepositsA());
            BigDecimal domesticRe = new BigDecimal(ssiApp.getDomesticRealEstateA() == null? "0" : ssiApp.getDomesticRealEstateA());
            BigDecimal foreignRe = new BigDecimal(ssiApp.getForeignRealEstateA() == null? "0" : ssiApp.getForeignRealEstateA());
            BigDecimal unemplBenefit = new BigDecimal(ssiApp.getUnemploymentBenefitR() == null? "0" : ssiApp.getUnemploymentBenefitR());
            BigDecimal ergome = new BigDecimal(ssiApp.getUnemploymentBenefitR() == null? "0" : ssiApp.getUnemploymentBenefitR());
            
        //log.info("salaries :{}, pensions :{}, farming :{}, freelance :{}, otherBnfts :{}, deposits :{}, domesticRe :{}, foreignRe :{}, unemplBenefit :{}", salaries, pensions, farming, freelance, otherBnfts, deposits, domesticRe, foreignRe, unemplBenefit);

            BigDecimal totalIncome = (salaries.add( 
                pensions).add(
                farming).add(
                freelance).add(
                otherBnfts).add(
                deposits).add(
                domesticRe).add(
                foreignRe).add(
                unemplBenefit).add(
                ergome)).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

        log.info("payment check payment thresshold :{}, total income :{}", paymentThresshold, totalIncome);

        if(paymentThresshold.compareTo(totalIncome)<= 0){
            return BigDecimal.ZERO;
        }
        return (paymentThresshold.subtract(totalIncome)).divide(BigDecimal.valueOf(6), 2, RoundingMode.HALF_UP);
    }

    public static Set<String> fetchAllHouseholdAfms(SsiApplication ssiApp){
        Set<String> allAfms = new HashSet<>();
        for (Map.Entry<String, List<HouseholdMember>> app : ssiApp.getHouseholdCompositionHistory().entrySet()) {
            for(HouseholdMember member : app.getValue()){
                allAfms.add(member.getAfm());
            }
        }

        //for (HouseholdMember app : ssiApp.getHouseholdComposition()) {
            // for(HouseholdMember member : ssiApp.getHouseholdComposition()){
            //     allAfms.add(member.getAfm());
            // }
       // }
        return allAfms;
    }

    // public static Boolean areAppHouseholdAfmsTheSame(List<SsiApplication> householdApps, SsiApplication ssiApp){
    //     List<HouseholdMember> household = ssiApp.getHouseholdComposition();
    //     //check if by the end of the month all the members of the household have submitted an application
    //     List<String> appAfms = householdApps.stream().map(a -> a.getTaxisAfm()).collect(Collectors.toList());
    //     List<String> householdAfms = household.stream().map(m -> m.getAfm()).collect(Collectors.toList());
    //     if(!appAfms.containsAll(householdAfms)){
    //         return false;
    //     }

    //     return true;
    // }

    public static Integer monthDays(LocalDate date) {

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

    public static Integer calculateAge(LocalDate dateOfBirth, LocalDate referenceDate){
        if ((dateOfBirth != null) && (referenceDate != null)) {
            return Period.between(dateOfBirth, referenceDate).getYears();
        } else {
            return 0;
        }
    }
    
}
