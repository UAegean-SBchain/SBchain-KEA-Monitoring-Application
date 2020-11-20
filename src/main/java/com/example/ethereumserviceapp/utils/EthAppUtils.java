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

        SsiApplication principalApp = householdApps.stream().filter(h -> h.getTaxisAfm().equals(h.getHouseholdPrincipal().getAfm())).collect(Collectors.toList()).get(0);
        
        SsiApplication ssiApp = new SsiApplication();

        ssiApp.setHouseholdComposition(principalApp.getHouseholdComposition());
        ssiApp.setTaxisAfm(principalApp.getTaxisAfm());
        
        ssiApp.setSalariesR(String.valueOf(householdApps.stream().map(x -> new BigDecimal(x.getSalariesR()== null? "0" : x.getSalariesR()).subtract(new BigDecimal(x.getSalariesR()== null? "0" : x.getSalariesR()).multiply(BigDecimal.valueOf(0.2))))
                .reduce(BigDecimal.ZERO, BigDecimal::add)));

        ssiApp.setPensionsR(String.valueOf(householdApps.stream().map(x -> new BigDecimal(x.getPensionsR() == null? "0" : x.getPensionsR()))
        .reduce(BigDecimal.ZERO, BigDecimal::add)));

        ssiApp.setFarmingR(String.valueOf(householdApps.stream().map(x -> new BigDecimal(x.getFarmingR() == null? "0" : x.getFarmingR()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)));

        ssiApp.setFreelanceR(String.valueOf(householdApps.stream().map(x -> new BigDecimal(x.getFreelanceR() == null? "0" : x.getFreelanceR()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)));

        ssiApp.setOtherBenefitsR(String.valueOf(householdApps.stream().map(x -> new BigDecimal(x.getOtherBenefitsR() == null? "0" : x.getOtherBenefitsR()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)));

        ssiApp.setDepositsA(String.valueOf(householdApps.stream().map(x -> new BigDecimal(x.getDepositsA() == null? "0" : x.getDepositsA()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)));

        ssiApp.setDomesticRealEstateA(String.valueOf(householdApps.stream().map(x -> new BigDecimal(x.getDomesticRealEstateA() == null? "0" : x.getDomesticRealEstateA()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)));

        ssiApp.setForeignRealEstateA(String.valueOf(householdApps.stream().map(x -> new BigDecimal(x.getForeignRealEstateA() == null? "0" : x.getForeignRealEstateA()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)));

        return ssiApp;
    }

    
    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static BigDecimal calculatePayment(Integer numDays, Integer days, SsiApplication ssiApp, LocalDate referenceDate){

        BigDecimal totalMonthlyValue = getTotalMonthlyValue(ssiApp, referenceDate);

        //maximum monthly allowance is 900
        if(totalMonthlyValue.compareTo(BigDecimal.valueOf(900)) > 0){
            totalMonthlyValue = BigDecimal.valueOf(900);
        }

        if(numDays == days){
            return totalMonthlyValue;
        }
        BigDecimal totalDailyValue = totalMonthlyValue.divide(BigDecimal.valueOf(numDays), 2, RoundingMode.HALF_UP);

        BigDecimal valueToBePaid = BigDecimal.valueOf(days).multiply(totalDailyValue);

        return valueToBePaid;
    }

    public static BigDecimal getTotalMonthlyValue(SsiApplication ssiApp, LocalDate date){
        BigDecimal paymentThresshold = BigDecimal.valueOf(0);
        List<HouseholdMember> household = ssiApp.getHouseholdComposition();
        final LocalDate referenceDate = date == null? LocalDate.now(): date;

        Long adults = household.stream().filter(h -> calculateAge(LocalDate.parse(h.getDateOfBirth(), formatter), referenceDate) >= 18).count();
        Integer adultCount = adults.intValue();
        Integer minorCount = household.size() - adultCount;

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

            BigDecimal salaries = new BigDecimal(ssiApp.getSalariesR()== null? "0" : ssiApp.getSalariesR()).subtract(new BigDecimal(ssiApp.getSalariesR()== null? "0" : ssiApp.getSalariesR()).multiply(BigDecimal.valueOf(0.2)));
            BigDecimal pensions = new BigDecimal(ssiApp.getPensionsR() == null? "0" : ssiApp.getPensionsR());
            BigDecimal farming = new BigDecimal(ssiApp.getFarmingR() == null? "0" : ssiApp.getFarmingR());
            BigDecimal freelance = new BigDecimal(ssiApp.getFreelanceR() == null? "0" : ssiApp.getFreelanceR());
            BigDecimal otherBnfts = new BigDecimal(ssiApp.getOtherBenefitsR() == null? "0" : ssiApp.getOtherBenefitsR());
            BigDecimal deposits = new BigDecimal(ssiApp.getDepositsA() == null? "0" : ssiApp.getDepositsA());
            BigDecimal domesticRe = new BigDecimal(ssiApp.getDomesticRealEstateA() == null? "0" : ssiApp.getDomesticRealEstateA());
            BigDecimal foreignRe = new BigDecimal(ssiApp.getForeignRealEstateA() == null? "0" : ssiApp.getForeignRealEstateA());
            
            BigDecimal totalIncome = (salaries.add( 
                pensions).add(
                farming).add(
                freelance).add(
                otherBnfts).add(
                deposits).add(
                domesticRe).add(
                foreignRe)
                ).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

        if(paymentThresshold.compareTo(totalIncome)<= 0){
            return BigDecimal.ZERO;
        }
        return (paymentThresshold.subtract(totalIncome)).divide(BigDecimal.valueOf(6), 2, RoundingMode.HALF_UP);
    }

    public static Set<String> fetchAllHouseholdAfms(SsiApplication ssiApp){
        Set<String> allAfms = new HashSet<>();
        for (Map.Entry<LocalDateTime, List<HouseholdMember>> app : ssiApp.getHouseholdCompositionHistory().entrySet()) {
            for(HouseholdMember member : app.getValue()){
                allAfms.add(member.getAfm());
            }
        }

        return allAfms;
    }

    public static Boolean areAppHouseholdAfmsTheSame(List<SsiApplication> householdApps, SsiApplication ssiApp){
        List<HouseholdMember> household = ssiApp.getHouseholdComposition();
        //check if by the end of the month all the members of the household have submitted an application
        List<String> appAfms = householdApps.stream().map(a -> a.getTaxisAfm()).collect(Collectors.toList());
        List<String> householdAfms = household.stream().map(m -> m.getAfm()).collect(Collectors.toList());

        if(!householdAfms.containsAll(appAfms)){
            return false;
        }

        return true;
    }

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
