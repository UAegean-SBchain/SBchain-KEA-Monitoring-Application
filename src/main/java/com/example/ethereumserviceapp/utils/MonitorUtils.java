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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.CasePayment;
import com.example.ethereumserviceapp.model.State;
import com.example.ethereumserviceapp.model.entities.SsiApplication;
import com.example.ethereumserviceapp.model.entities.SsiApplicationTest;

import lombok.val;
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
        LocalDate endOfCredMonth = credentialDate.withDayOfMonth(monthDays(credentialDate));
        
        // this should probably call the payment calculation method and return the value that should have been paid for all paid days with the new credentials
        //BigDecimal fullMonthProjection = BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalPayment = BigDecimal.ZERO;
        BigDecimal projectedPayment = BigDecimal.ZERO;

        for(CasePayment payment:monitoredCase.getPaymentHistory()){
            
            //sum the payment of each month to calculate the total paid amount 
            totalPayment = totalPayment.add(payment.getPayment());

            //if the payment of the month has failed add it all as offset(don't include it the projected sum)
            if(payment.getState().equals(State.FAILED)){
                continue;
            }

            // payment concerns the previous month of the actual date of payment
            LocalDate paymentDate = payment.getPaymentDate().minusMonths(1).toLocalDate();

            // update iff the month of the date is equal or after the date of the modified credential 
            if(paymentDate.isBefore(startOfCredMonth)){
                projectedPayment = projectedPayment.add(payment.getPayment());
                continue; 
            }
            BigDecimal fullMonthProjection = calculatePayment(monthDays(paymentDate), BigDecimal.ZERO, ssiApp);
            if(fullMonthProjection == payment.getPayment()){
                projectedPayment = projectedPayment.add(payment.getPayment());
                continue; 
            }

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

    public static void calculateCurrentMonthOffset(LocalDate credentialDate, Case monitoredCase, SsiApplication ssiApp, BigDecimal projection){

        LocalDate startOfMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().minusMonths(1).withDayOfMonth(monthDays(startOfMonth));

        if(credentialDate.compareTo(startOfMonth) >= 0){

            Long paidDays = monitoredCase.getHistory().entrySet().stream().filter(
                e -> e.getKey().toLocalDate().compareTo(startOfMonth) >=0 && e.getKey().toLocalDate().compareTo(endOfMonth) <=0 && e.getValue().equals(State.ACCEPTED)).count();

            Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
            e -> e.getKey().toLocalDate().compareTo(credentialDate) <= 0 && e.getKey().toLocalDate().compareTo(startOfMonth) >=0 && e.getValue().equals(State.ACCEPTED)).count();
    
            // projection of what the payment would be with the old credential
            BigDecimal fullMonthProjectionOld = calculatePayment(monthDays(startOfMonth), BigDecimal.ZERO, ssiApp);

            BigDecimal paymentPerDayOld = fullMonthProjectionOld.divide(BigDecimal.valueOf(paidDays), 2, RoundingMode.HALF_UP);
            BigDecimal paymentPerDayNew = projection.divide(BigDecimal.valueOf(paidDays), 2, RoundingMode.HALF_UP);

            // the payment value that should be paid this month without any offset
            BigDecimal actualPayment = paymentPerDayOld.multiply(BigDecimal.valueOf(offsetDays)).add(paymentPerDayNew.multiply(BigDecimal.valueOf(paidDays - offsetDays)));

            //add the difference between the projection and the actual payment value to the offset 
            monitoredCase.setOffset(monitoredCase.getOffset().add(projection.subtract(actualPayment)));
        }
    }

    // public static void calculateCurrentMonthOffset2(Case monitoredCase, SsiApplicationTest ssiAppTest){
    //     List<PaymentCredential> changedCredentials = new ArrayList<>();

    //     Map<String, LinkedHashMap<LocalDateTime, String>> credHistoriesMap = new HashMap<>();
    //     if(ssiAppTest.getPensionsRHistory()!=null){
    //         credHistoriesMap.put("pension", ssiAppTest.getPensionsRHistory());
    //     }
    //     if(ssiAppTest.getSalariesRHistory()!=null){
    //         credHistoriesMap.put("salaries", ssiAppTest.getSalariesRHistory());
    //     }
    //     if(ssiAppTest.getOtherBenefitsRHistory()!=null){
    //         credHistoriesMap.put("otherBnfts", ssiAppTest.getOtherBenefitsRHistory());
    //     }
    //     if(ssiAppTest.getFreelanceRHistory()!=null){
    //         credHistoriesMap.put("freelance", ssiAppTest.getFreelanceRHistory());
    //     }
    //     if(ssiAppTest.getDepositsAHistory()!=null){
    //         credHistoriesMap.put("deposits", ssiAppTest.getDepositsAHistory());
    //     }
    //     if(ssiAppTest.getDomesticRealEstateAHistory()!=null){
    //         credHistoriesMap.put("domesticRE", ssiAppTest.getDomesticRealEstateAHistory());
    //     }
    //     if(ssiAppTest.getForeignRealEstateAHistory()!=null){
    //         credHistoriesMap.put("foreignRE", ssiAppTest.getForeignRealEstateAHistory());
    //     }

    //     //if there are altered credentials (credential history size > 1) set the credential date and value to the default (first value) and add it to the list of changed credentials
    //     if(!credHistoriesMap.isEmpty()){
    //         credHistoriesMap.entrySet().forEach(e -> {
    //             if(e.getValue().size()>1){
    //                 e.getValue().entrySet().forEach(h ->{
    //                     if(h.getKey().toLocalDate().isAfter(LocalDate.now().minusMonths(1))){
    //                         PaymentCredential credential = new PaymentCredential();
    //                         credential.setDate(h.getKey()); 
    //                         credential.setValue(h.getValue());
    //                         credential.setName(e.getKey());
    //                         changedCredentials.add(credential);
    //                     }
    //                 });
    //             }
    //         });
    //     }

    //     List<PaymentCredential> changedCredentialsSorted = changedCredentials.stream().sorted(Comparator.comparing(PaymentCredential::getDate)).collect(Collectors.toList());
    //     Map<String, List<PaymentCredential>> nameGroupMap = changedCredentialsSorted.stream().collect(Collectors.groupingBy(e -> e.getName()));
    //     nameGroupMap.entrySet().forEach(h -> {
    //         updateSsiApplication(h.getKey(), h.getValue().get(0).getValue(), ssiAppTest);
    //     });

    //     LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1).minusMonths(1);
    //     Boolean firstRun = true;
    //     Long days = 0L;
    //     for(PaymentCredential cred:changedCredentialsSorted){
    //         if(firstRun){
    //             days = monitoredCase.getHistory().entrySet().stream().filter(
    //                 e -> e.getKey().toLocalDate().compareTo(startOfMonth) >= 0 && e.getKey().toLocalDate().compareTo(cred.getDate().toLocalDate()) < 0 && e.getValue().equals(State.ACCEPTED)).count();
                    
    //             firstRun = false;
    //         } else {
    //             days = monitoredCase.getHistory().entrySet().stream().filter(
    //                 e -> e.getKey().toLocalDate().compareTo(startOfMonth) >= 0 && e.getKey().toLocalDate().compareTo(cred.getDate().toLocalDate()) < 0 && e.getValue().equals(State.ACCEPTED)).count();
                
    //         }
    //     }

    //     for(int i = 0; i< changedCredentialsSorted.size(); i++){
                        
    //         if( i+1 <  changedCredentialsSorted.size() ) {
    //             //m.getValue().get(i).setDays(m.getValue().get(i+1).getDate().getDayOfMonth() - m.getValue().get(i).getDate().getDayOfMonth());
            
    //             final int innerI = i;
    //             Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
    //                 e -> e.getKey().toLocalDate().compareTo(changedCredentialsSorted.get(i).getDate().toLocalDate()) >= 0 && e.getKey().toLocalDate().compareTo(changedCredentialsSorted.get(i+1).getDate().toLocalDate()) <0 && e.getValue().equals(State.ACCEPTED)).count();
                
    //             updateSsiApplication(changedCredentialsSorted.get(i).getName(), changedCredentialsSorted.get(i).getValue(), ssiAppTest);   
    //             BigDecimal offsetPayment = calculatePayment2(fullMonthDays, offsetDays.intValue(), ssiAppTest, BigDecimal.ZERO);
    //             correctedPayment = correctedPayment.add(offsetPayment);
    //             log.info("yyyyyyyyyyyyyyyyyyyyyy offsetPayment :{}", offsetPayment);
    //         } else {
    //             final int innerI = i;
    //             Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
    //                 e -> e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().toLocalDate()) >= 0 && e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().withDayOfMonth(monthDays(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().toLocalDate())).toLocalDate()) <=0 && e.getValue().equals(State.ACCEPTED)).count();
                
    //             updateSsiApplication(monthlyGroupMap.get(startOfMonth).get(i).getName(), monthlyGroupMap.get(startOfMonth).get(i).getValue(), ssiAppTest);   
                
    //             BigDecimal offsetPayment = calculatePayment2(fullMonthDays, offsetDays.intValue(), ssiAppTest, BigDecimal.ZERO);
    //             correctedPayment = correctedPayment.add(offsetPayment);
    //             log.info("zzzzzzzzzzzzzzzzzzzzzzz last offsetPayment :{}", offsetPayment);
    //         }
    //         log.info("fffffffffffffffffffffffffff correctedPayment:{}", correctedPayment);
    //     }




        // changedCredentialsSorted.forEach(s -> {
        //     if(firstRun){
        //         Long days = monitoredCase.getHistory().entrySet().stream().filter(
        //                 e -> e.getKey().toLocalDate().compareTo(startOfMonth) >= 0 && e.getKey().toLocalDate().compareTo(s.getDate().toLocalDate()) < 0 && e.getValue().equals(State.ACCEPTED)).count();
                    
        //         firstRun = false;
        //     }
            
        // });

    //}

    public static void calculateOffset(Case monitoredCase, SsiApplicationTest ssiAppTest){

        List<PaymentCredential> changedCredentials = new ArrayList<>();

        Map<String, LinkedHashMap<LocalDateTime, String>> credHistoriesMap = new HashMap<>();
        if(ssiAppTest.getPensionsRHistory()!=null){
            credHistoriesMap.put("pension", ssiAppTest.getPensionsRHistory());
        }
        if(ssiAppTest.getSalariesRHistory()!=null){
            credHistoriesMap.put("salaries", ssiAppTest.getSalariesRHistory());
        }
        if(ssiAppTest.getOtherBenefitsRHistory()!=null){
            credHistoriesMap.put("otherBnfts", ssiAppTest.getOtherBenefitsRHistory());
        }
        if(ssiAppTest.getFreelanceRHistory()!=null){
            credHistoriesMap.put("freelance", ssiAppTest.getFreelanceRHistory());
        }
        if(ssiAppTest.getDepositsAHistory()!=null){
            credHistoriesMap.put("deposits", ssiAppTest.getDepositsAHistory());
        }
        if(ssiAppTest.getDomesticRealEstateAHistory()!=null){
            credHistoriesMap.put("domesticRE", ssiAppTest.getDomesticRealEstateAHistory());
        }
        if(ssiAppTest.getForeignRealEstateAHistory()!=null){
            credHistoriesMap.put("foreignRE", ssiAppTest.getForeignRealEstateAHistory());
        }

        //if there are altered credentials (credential history size > 1) set the credential date and value to the default (first value) and add it to the list of changed credentials
        if(!credHistoriesMap.isEmpty()){
            credHistoriesMap.entrySet().forEach(e -> {
                if(e.getValue().size()>1){
                    e.getValue().entrySet().stream().skip(1).forEach(p -> {
                        PaymentCredential credential = new PaymentCredential();
                        credential.setDate(p.getKey()); 
                        credential.setValue(p.getValue());
                        credential.setName(e.getKey());
                        changedCredentials.add(credential);
                    });
                    updateSsiApplication(e.getKey(), e.getValue().entrySet().iterator().next().getValue(), ssiAppTest);
                }
            });
        }

        if(!changedCredentials.isEmpty()){
            //sort the list of altered credentials by date
            List<PaymentCredential> changedCredentialsSorted = changedCredentials.stream().sorted(Comparator.comparing(PaymentCredential::getDate)).collect(Collectors.toList());
            log.info("ssssssssssssssssssssssss changedCredentialsSorted :{}", changedCredentialsSorted);
            //create a map of altered credentials grouped by month, with key the start date of the month and value the credentials that were altered during this month
            Map<LocalDate, List<PaymentCredential>> monthlyGroupMap = changedCredentialsSorted.stream().collect(Collectors.groupingBy(e -> e.getDate().withDayOfMonth(1).toLocalDate()));
            
            log.info("aaaaaaaaaaaaaaaaaaaaaaaa monthlyGroupMap :{}", monthlyGroupMap);

            List<CasePayment> paymentHistory = monitoredCase.getPaymentHistory().stream().sorted(Comparator.comparing(CasePayment::getPaymentDate)).collect(Collectors.toList());
            
            // if(currentProjection != null){
            //     CasePayment currentPayment = new CasePayment();
            //     currentPayment.setPaymentDate(LocalDateTime.now());
            //     currentPayment.setPayment(currentProjection);
                
            //     if(monitoredCase.getPaymentHistory() != null && !monitoredCase.getPaymentHistory().isEmpty()){
                    
            //     }
            //     paymentHistory.add(currentPayment);
            // }
            

            monthlyGroupMap.entrySet().forEach(m -> {
                if(m.getKey().compareTo(paymentHistory.get(0).getPaymentDate().toLocalDate()) <= 0){
                    m.getValue().forEach(l ->{
                        updateSsiApplication(l.getName(), l.getValue(), ssiAppTest);   
                    });
                }
            });

            paymentHistory.forEach(ph -> {
                LocalDate startOfMonth = ph.getPaymentDate().minusMonths(1).withDayOfMonth(1).toLocalDate();
                LocalDate endOfMonth = ph.getPaymentDate().minusMonths(1).withDayOfMonth(monthDays(ph.getPaymentDate().minusMonths(1).toLocalDate())).toLocalDate();
                Integer fullMonthDays = monthDays(startOfMonth);
                log.info("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkk startOfMonth :{}", startOfMonth);
log.info("jjjjjjjjjjjjjjjjjjjjjjj monthlyGroupMap.get(startOfMonth) :{}", monthlyGroupMap.get(startOfMonth));

                if(monthlyGroupMap.get(startOfMonth) == null && startOfMonth.isAfter(monthlyGroupMap.entrySet().iterator().next().getValue().get(0).getDate().toLocalDate())){
                    
                    Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                        e -> e.getKey().toLocalDate().compareTo(startOfMonth) >= 0 && e.getKey().toLocalDate().compareTo(endOfMonth) <=0 && e.getValue().equals(State.ACCEPTED)).count();
                    log.info("hhhhhhhhhhhhhhhhhhhhhhhhh after credentials offsetDays :{}", offsetDays);
                    BigDecimal offsetPayment = calculatePayment2(fullMonthDays, offsetDays.intValue(), ssiAppTest, BigDecimal.ZERO);
                    log.info("llllllllllllllllllllllllllll after credentials offsetPayment :{}", offsetPayment);

                    BigDecimal monthlyOffset = ph.getPayment().subtract(offsetPayment);

                    monitoredCase.setOffset(monitoredCase.getOffset().add(monthlyOffset));
                }

                if(monthlyGroupMap.get(startOfMonth)!= null){
                    Long nonOffsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                            e -> e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(0).getDate().withDayOfMonth(1).toLocalDate()) >= 0 && e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(0).getDate().toLocalDate()) <0 && e.getValue().equals(State.ACCEPTED)).count();
                        
                    //BigDecimal correctedPayment = calculatePayment2(nonOffsetDays.intValue(), ssiAppTest, BigDecimal.ZERO);

                    CasePayment payment = monitoredCase.getPaymentHistory().stream()
                        .filter(p -> p.getPaymentDate().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(0).getDate().withDayOfMonth(1).toLocalDate()) >= 0
                        && p.getPaymentDate().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(0).getDate().withDayOfMonth(monthDays(monthlyGroupMap.get(startOfMonth).get(0).getDate().toLocalDate())).toLocalDate()) <= 0)
                        .collect(Collectors.toList()).get(0);

                    BigDecimal correctedPayment = (payment.getPayment()
                        .divide(BigDecimal.valueOf(monthDays(payment.getPaymentDate().toLocalDate())), 2, RoundingMode.HALF_UP))
                        .multiply(BigDecimal.valueOf(nonOffsetDays));
                    log.info("gggggggggggggggggggggg corrected payment start :{}", correctedPayment);
                    for(int i = 0; i< monthlyGroupMap.get(startOfMonth).size(); i++){
                        
                        if( i+1 <  monthlyGroupMap.get(startOfMonth).size() ) {
                            //m.getValue().get(i).setDays(m.getValue().get(i+1).getDate().getDayOfMonth() - m.getValue().get(i).getDate().getDayOfMonth());
                        
                            final int innerI = i;
                            Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                                e -> e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().toLocalDate()) >= 0 && e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI+1).getDate().toLocalDate()) <0 && e.getValue().equals(State.ACCEPTED)).count();
                            
                            updateSsiApplication(monthlyGroupMap.get(startOfMonth).get(i).getName(), monthlyGroupMap.get(startOfMonth).get(i).getValue(), ssiAppTest);   
                            BigDecimal offsetPayment = calculatePayment2(fullMonthDays, offsetDays.intValue(), ssiAppTest, BigDecimal.ZERO);
                            correctedPayment = correctedPayment.add(offsetPayment);
                            log.info("yyyyyyyyyyyyyyyyyyyyyy offsetPayment :{}", offsetPayment);
                        } else {
                            final int innerI = i;
                            Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                                e -> e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().toLocalDate()) >= 0 && e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().withDayOfMonth(monthDays(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().toLocalDate())).toLocalDate()) <=0 && e.getValue().equals(State.ACCEPTED)).count();
                            
                            updateSsiApplication(monthlyGroupMap.get(startOfMonth).get(i).getName(), monthlyGroupMap.get(startOfMonth).get(i).getValue(), ssiAppTest);   
                            
                            BigDecimal offsetPayment = calculatePayment2(fullMonthDays, offsetDays.intValue(), ssiAppTest, BigDecimal.ZERO);
                            correctedPayment = correctedPayment.add(offsetPayment);
                            log.info("zzzzzzzzzzzzzzzzzzzzzzz last offsetPayment :{}", offsetPayment);
                        }
                        log.info("fffffffffffffffffffffffffff correctedPayment:{}", correctedPayment);
                    }

                    BigDecimal monthlyOffset = ph.getPayment().subtract(correctedPayment);

                    monitoredCase.setOffset(monitoredCase.getOffset().add(monthlyOffset));

                }
            });


            // monthlyGroupMap.entrySet().forEach(m -> {
            //     Long nonOffsetDays = monitoredCase.getHistory().entrySet().stream().filter(
            //                 e -> e.getKey().toLocalDate().compareTo(m.getValue().get(0).getDate().withDayOfMonth(1).toLocalDate()) >= 0 && e.getKey().toLocalDate().compareTo(m.getValue().get(0).getDate().toLocalDate()) <0 && e.getValue().equals(State.ACCEPTED)).count();
                        
            //     //BigDecimal correctedPayment = calculatePayment2(nonOffsetDays.intValue(), ssiAppTest, BigDecimal.ZERO);

            //     CasePayment payment = monitoredCase.getPaymentHistory().stream()
            //         .filter(p -> p.getPaymentDate().toLocalDate().compareTo(m.getValue().get(0).getDate().withDayOfMonth(1).toLocalDate()) >= 0
            //         && p.getPaymentDate().toLocalDate().compareTo(m.getValue().get(0).getDate().withDayOfMonth(monthDays(m.getValue().get(0).getDate().toLocalDate())).toLocalDate()) <= 0)
            //         .collect(Collectors.toList()).get(0);

            //     BigDecimal correctedPayment = (payment.getPayment()
            //         .divide(BigDecimal.valueOf(monthDays(payment.getPaymentDate().toLocalDate())), 2, RoundingMode.HALF_UP))
            //         .multiply(BigDecimal.valueOf(nonOffsetDays));
            //     log.info("gggggggggggggggggggggg corrected payment start :{}", correctedPayment);
            //     for(int i = 0; i< m.getValue().size(); i++){
                    
            //         if( i+1 <  m.getValue().size() ) {
            //             //m.getValue().get(i).setDays(m.getValue().get(i+1).getDate().getDayOfMonth() - m.getValue().get(i).getDate().getDayOfMonth());
                    
            //             final int innerI = i;
            //             Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
            //                 e -> e.getKey().toLocalDate().compareTo(m.getValue().get(innerI).getDate().toLocalDate()) >= 0 && e.getKey().toLocalDate().compareTo(m.getValue().get(innerI+1).getDate().toLocalDate()) <0 && e.getValue().equals(State.ACCEPTED)).count();
                        
            //             updateSsiApplication(m.getValue().get(i).getName(), m.getValue().get(i).getValue(), ssiAppTest);   
            //             BigDecimal offsetPayment = calculatePayment2(offsetDays.intValue(), ssiAppTest, BigDecimal.ZERO);
            //             correctedPayment = correctedPayment.add(offsetPayment);
            //             log.info("yyyyyyyyyyyyyyyyyyyyyy offsetPayment :{}", offsetPayment);
            //         } else {
            //             final int innerI = i;
            //             Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
            //                 e -> e.getKey().toLocalDate().compareTo(m.getValue().get(innerI).getDate().toLocalDate()) >= 0 && e.getKey().toLocalDate().compareTo(m.getValue().get(innerI).getDate().withDayOfMonth(monthDays(m.getValue().get(innerI).getDate().toLocalDate())).toLocalDate()) <=0 && e.getValue().equals(State.ACCEPTED)).count();
                        
            //             updateSsiApplication(m.getValue().get(i).getName(), m.getValue().get(i).getValue(), ssiAppTest);   
                        
            //             BigDecimal offsetPayment = calculatePayment2(offsetDays.intValue(), ssiAppTest, BigDecimal.ZERO);
            //             correctedPayment = correctedPayment.add(offsetPayment);
            //             log.info("zzzzzzzzzzzzzzzzzzzzzzz last offsetPayment :{}", offsetPayment);
            //         }
            //         log.info("fffffffffffffffffffffffffff correctedPayment:{}", correctedPayment);
            //     }
            // });
        }
    }

    public static BigDecimal calculateOffset2(Case monitoredCase, SsiApplicationTest ssiAppTest, BigDecimal projectedPayment){

        List<PaymentCredential> changedCredentials = new ArrayList<>();

        Map<String, LinkedHashMap<LocalDateTime, String>> credHistoriesMap = new HashMap<>();
        if(ssiAppTest.getPensionsRHistory()!=null){
            credHistoriesMap.put("pension", ssiAppTest.getPensionsRHistory());
        }
        if(ssiAppTest.getSalariesRHistory()!=null){
            credHistoriesMap.put("salaries", ssiAppTest.getSalariesRHistory());
        }
        if(ssiAppTest.getOtherBenefitsRHistory()!=null){
            credHistoriesMap.put("otherBnfts", ssiAppTest.getOtherBenefitsRHistory());
        }
        if(ssiAppTest.getFreelanceRHistory()!=null){
            credHistoriesMap.put("freelance", ssiAppTest.getFreelanceRHistory());
        }
        if(ssiAppTest.getDepositsAHistory()!=null){
            credHistoriesMap.put("deposits", ssiAppTest.getDepositsAHistory());
        }
        if(ssiAppTest.getDomesticRealEstateAHistory()!=null){
            credHistoriesMap.put("domesticRE", ssiAppTest.getDomesticRealEstateAHistory());
        }
        if(ssiAppTest.getForeignRealEstateAHistory()!=null){
            credHistoriesMap.put("foreignRE", ssiAppTest.getForeignRealEstateAHistory());
        }

        //if there are altered credentials (credential history size > 1) set the credential date and value to the default (first value) and add it to the list of changed credentials
        if(!credHistoriesMap.isEmpty()){
            credHistoriesMap.entrySet().forEach(e -> {
                if(e.getValue().size()>1){
                    e.getValue().entrySet().stream().skip(1).forEach(p -> {
                        PaymentCredential credential = new PaymentCredential();
                        credential.setDate(p.getKey()); 
                        credential.setValue(p.getValue());
                        credential.setName(e.getKey());
                        changedCredentials.add(credential);
                    });
                    updateSsiApplication(e.getKey(), e.getValue().entrySet().iterator().next().getValue(), ssiAppTest);
                }
            });
        }

        LocalDate startOfPayment = LocalDate.now().minusMonths(1).withDayOfMonth(1);

        BigDecimal correctedPayment = BigDecimal.ZERO;

        if(changedCredentials.isEmpty() || !changedCredentials.stream().anyMatch(c -> c.getDate().withDayOfMonth(1).toLocalDate().equals(startOfPayment))){
            return projectedPayment;
        }
        //sort the list of altered credentials by date
        List<PaymentCredential> changedCredentialsSorted = changedCredentials.stream().sorted(Comparator.comparing(PaymentCredential::getDate)).collect(Collectors.toList());
        log.info("ssssssssssssssssssssssss changedCredentialsSorted :{}", changedCredentialsSorted);
        //create a map of altered credentials grouped by month, with key the start date of the month and value the credentials that were altered during this month
        Map<LocalDate, List<PaymentCredential>> monthlyGroupMap = changedCredentialsSorted.stream().collect(Collectors.groupingBy(e -> e.getDate().withDayOfMonth(1).toLocalDate()));
        
        log.info("aaaaaaaaaaaaaaaaaaaaaaaa monthlyGroupMap :{}", monthlyGroupMap);

        List<CasePayment> paymentHistory = monitoredCase.getPaymentHistory().stream().sorted(Comparator.comparing(CasePayment::getPaymentDate)).collect(Collectors.toList());
        
        CasePayment currentPayment = new CasePayment();
        currentPayment.setPaymentDate(LocalDateTime.now());
        currentPayment.setPayment(projectedPayment);
        
        // if(monitoredCase.getPaymentHistory() != null && !monitoredCase.getPaymentHistory().isEmpty()){
            
        // }
        paymentHistory.add(currentPayment);
        

        monthlyGroupMap.entrySet().forEach(m -> {
            if(m.getKey().compareTo(paymentHistory.get(0).getPaymentDate().toLocalDate()) <= 0){
                m.getValue().forEach(l ->{
                    updateSsiApplication(l.getName(), l.getValue(), ssiAppTest);   
                });
            }
        });

        for(CasePayment payment : paymentHistory){
            
            LocalDate startOfMonth = payment.getPaymentDate().minusMonths(1).withDayOfMonth(1).toLocalDate();
            //LocalDate endOfMonth = payment.getPaymentDate().minusMonths(1).withDayOfMonth(monthDays(payment.getPaymentDate().minusMonths(1).toLocalDate())).toLocalDate();
            Integer fullMonthDays = monthDays(startOfMonth);
            log.info("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkk startOfMonth :{}", startOfMonth);
            log.info("jjjjjjjjjjjjjjjjjjjjjjj monthlyGroupMap.get(startOfMonth) :{}", monthlyGroupMap.get(startOfMonth));

            // if(monthlyGroupMap.get(startOfMonth) == null && startOfMonth.isAfter(monthlyGroupMap.entrySet().iterator().next().getValue().get(0).getDate().toLocalDate())){
                
            //     Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
            //         e -> e.getKey().toLocalDate().compareTo(startOfMonth) >= 0 && e.getKey().toLocalDate().compareTo(endOfMonth) <=0 && e.getValue().equals(State.ACCEPTED)).count();
            //     log.info("hhhhhhhhhhhhhhhhhhhhhhhhh after credentials offsetDays :{}", offsetDays);
            //     BigDecimal offsetPayment = calculatePayment2(fullMonthDays, offsetDays.intValue(), ssiAppTest, BigDecimal.ZERO);
            //     log.info("llllllllllllllllllllllllllll after credentials offsetPayment :{}", offsetPayment);

            //     BigDecimal monthlyOffset = ph.getPayment().subtract(offsetPayment);

            //     monitoredCase.setOffset(monitoredCase.getOffset().add(monthlyOffset));
            // }

            if(monthlyGroupMap.get(startOfMonth)!= null){
                if(!startOfMonth.isEqual(startOfPayment)){
                    //updateSsiApplication(monthlyGroupMap.get(startOfMonth).get(i).getName(), monthlyGroupMap.get(startOfMonth).get(i).getValue(), ssiAppTest);
                    continue;   
                }
                Long nonOffsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                        e -> e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(0).getDate().withDayOfMonth(1).toLocalDate()) >= 0 && e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(0).getDate().toLocalDate()) <0 && e.getValue().equals(State.ACCEPTED)).count();
                    
                correctedPayment = calculatePayment2(fullMonthDays, nonOffsetDays.intValue(), ssiAppTest, BigDecimal.ZERO);

                // CasePayment payment = monitoredCase.getPaymentHistory().stream()
                //     .filter(p -> p.getPaymentDate().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(0).getDate().withDayOfMonth(1).toLocalDate()) >= 0
                //     && p.getPaymentDate().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(0).getDate().withDayOfMonth(monthDays(monthlyGroupMap.get(startOfMonth).get(0).getDate().toLocalDate())).toLocalDate()) <= 0)
                //     .collect(Collectors.toList()).get(0);

                // BigDecimal correctedPayment = (payment.getPayment()
                //     .divide(BigDecimal.valueOf(monthDays(payment.getPaymentDate().toLocalDate())), 2, RoundingMode.HALF_UP))
                //     .multiply(BigDecimal.valueOf(nonOffsetDays));
                log.info("gggggggggggggggggggggg corrected payment start :{}", correctedPayment);
                for(int i = 0; i< monthlyGroupMap.get(startOfMonth).size(); i++){
                    
                    if( i+1 <  monthlyGroupMap.get(startOfMonth).size() ) {
                        if(!startOfMonth.isEqual(startOfPayment)){
                            updateSsiApplication(monthlyGroupMap.get(startOfMonth).get(i).getName(), monthlyGroupMap.get(startOfMonth).get(i).getValue(), ssiAppTest);
                            continue;   
                        }
                        //m.getValue().get(i).setDays(m.getValue().get(i+1).getDate().getDayOfMonth() - m.getValue().get(i).getDate().getDayOfMonth());
                    
                        final int innerI = i;
                        Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                            e -> e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().toLocalDate()) >= 0 && e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI+1).getDate().toLocalDate()) <0 && e.getValue().equals(State.ACCEPTED)).count();
                        
                        updateSsiApplication(monthlyGroupMap.get(startOfMonth).get(i).getName(), monthlyGroupMap.get(startOfMonth).get(i).getValue(), ssiAppTest);   
                        BigDecimal offsetPayment = calculatePayment2(fullMonthDays, offsetDays.intValue(), ssiAppTest, BigDecimal.ZERO);
                        correctedPayment = correctedPayment.add(offsetPayment);
                        log.info("yyyyyyyyyyyyyyyyyyyyyy offsetPayment :{}", offsetPayment);
                    } else {
                        if(!startOfMonth.isEqual(startOfPayment)){
                            updateSsiApplication(monthlyGroupMap.get(startOfMonth).get(i).getName(), monthlyGroupMap.get(startOfMonth).get(i).getValue(), ssiAppTest); 
                            continue;   
                        }
                        final int innerI = i;
                        Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                            e -> e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().toLocalDate()) >= 0 && e.getKey().toLocalDate().compareTo(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().withDayOfMonth(monthDays(monthlyGroupMap.get(startOfMonth).get(innerI).getDate().toLocalDate())).toLocalDate()) <=0 && e.getValue().equals(State.ACCEPTED)).count();
                        
                        updateSsiApplication(monthlyGroupMap.get(startOfMonth).get(i).getName(), monthlyGroupMap.get(startOfMonth).get(i).getValue(), ssiAppTest);   
                        
                        BigDecimal offsetPayment = calculatePayment2(fullMonthDays, offsetDays.intValue(), ssiAppTest, BigDecimal.ZERO);
                        correctedPayment = correctedPayment.add(offsetPayment);
                        log.info("zzzzzzzzzzzzzzzzzzzzzzz last offsetPayment :{}", offsetPayment);
                    }
                    log.info("fffffffffffffffffffffffffff correctedPayment:{}", correctedPayment);
                }

                log.info("fffffffffffffffffffffffffff correctedPayment:{}", correctedPayment);

                
                // BigDecimal monthlyOffset = ph.getPayment().subtract(correctedPayment);

                // monitoredCase.setOffset(monitoredCase.getOffset().add(monthlyOffset));

            }
        }

        return correctedPayment;
    }



    private static void updateSsiApplication(String name, String value, SsiApplicationTest ssiAppTest){
        switch(name) {
            case "pension":
            ssiAppTest.setPensionsR(value);
            break;
            case "salaries":
            ssiAppTest.setSalariesR(value);
            break;
            case "otherBnfts":
            ssiAppTest.setOtherBenefitsR(value);
            break;
            case "freelance":
            ssiAppTest.setFreelanceR(value);
            break;
            case "deposits":
            ssiAppTest.setDepositsA(value);
            break;
            case "domesticRE":
            ssiAppTest.setDomesticRealEstateA(value);
            break;
            case "foreignRE":
            ssiAppTest.setForeignRealEstateA(value);
            break;
        }
    }

    // public static <E> boolean doubleSumChecker(final List<E> elems, final Function<E, BigDecimal> elementsFunc, final BigDecimal result,
    //         final double precision) {
    //     final BigDecimal sum = elems.stream().map(elementsFunc).reduce(BigDecimal.ZERO, BigDecimal::add);


    //     return isApproxEqual(sum.doubleValue(), result.doubleValue(), precision);
    // }

    // //C24ValidationUtils.doubleSumChecker(stdcs, e -> e.getData().getFaild().getVol(), faildVol, precision));

    // // public static final Function<SettlementFailsParticipantRange1, List<String>> sfpr1VolLeis = f -> f.getHghstInVol() == null ? new ArrayList<>()
    // // : f.getHghstInVol().stream().map(SettlementFailsParticipant1::getLEI).collect(Collectors.toList());

   

    // public static <E> boolean testFunction(final SsiApplicationTest ssiAppTest, final Function<E, LinkedHashMap<String, String>> elementsFunc, final BigDecimal result,
    //         final double precision) {
    //     //final BigDecimal sum = elems.stream().map(elementsFunc).reduce(BigDecimal.ZERO, BigDecimal::add);
    //     public final Function<SsiApplicationTest, LinkedHashMap<String, String>> sfpr1VolLeis = f -> f.get(elementsFunc)  getHghstInVol() == null ? new ArrayList<>()
    //     : f.getHghstInVol().stream().map(SettlementFailsParticipant1::getLEI).collect(Collectors.toList());
    //     //LinkedHashMap<String, String> map = 

    //     if(ssiAppTest.elementsFunc.size()>1){
    //         ssiAppTest.getForeignRealEstateAHistory().entrySet().stream().skip(1).forEach(p -> {
    //             PaymentCredential credential = new PaymentCredential();
    //             credential.setDate(p.getKey()); 
    //             credential.setValue(p.getValue());
    //             credential.setName("foreignRE");
    //             changedCredentials.add(credential);
    //         });
    //         ssiAppTest.setForeignRealEstateA(ssiAppTest.getForeignRealEstateAHistory().entrySet().iterator().next().getValue());
    //     }

    //     return isApproxEqual(sum.doubleValue(), result.doubleValue(), precision);
    // }

    // private static boolean isApproxEqual(final double a, final double b, final double precision) {

    //     return Math.abs(a - b) < precision;
    // }

    public static void updateOffset2(LocalDate credentialDate, Case monitoredCase, SsiApplicationTest ssiApp){

        //Mock credential date, this illustrates a date at which a credential has been modified prior to being updated in the system
        //LocalDate credentialDate = LocalDate.of(2020, 8, 12);
        LocalDate startOfCredMonth = credentialDate.withDayOfMonth(1);
        LocalDate endOfCredMonth = credentialDate.withDayOfMonth(monthDays(credentialDate));
        
        // this should probably call the payment calculation method and return the value that should have been paid for all paid days with the new credentials
        //BigDecimal fullMonthProjection = BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalPayment = BigDecimal.ZERO;
        BigDecimal projectedPayment = BigDecimal.ZERO;

        for(CasePayment payment:monitoredCase.getPaymentHistory()){
            
            //sum the payment of each month to calculate the total paid amount 
            totalPayment = totalPayment.add(payment.getPayment());

            //if the payment of the month has failed add it all as offset(don't include it the projected sum)
            if(payment.getState().equals(State.FAILED)){
                continue;
            }

            // payment concerns the previous month of the actual date of payment
            LocalDate paymentDate = payment.getPaymentDate().minusMonths(1).toLocalDate();

            // update iff the month of the date is equal or after the date of the modified credential 
            if(paymentDate.isBefore(startOfCredMonth)){
                projectedPayment = projectedPayment.add(payment.getPayment());
                continue; 
            }
            log.info("111111111111111111111 offset :{}", monitoredCase.getOffset());
            BigDecimal fullMonthProjection = calculatePayment2(monthDays(paymentDate), monthDays(paymentDate), ssiApp, monitoredCase.getOffset());
            log.info("222222222222222222222 fullMonthProjection :{}", fullMonthProjection);
            if(fullMonthProjection == payment.getPayment()){
                projectedPayment = projectedPayment.add(payment.getPayment());
                continue; 
            }

            // if the payment concerns the same month as the altered credential, calculate the payment only of the offset days
            if(paymentDate.isBefore(endOfCredMonth)){
           
                Long paidDays = monitoredCase.getHistory().entrySet().stream().filter(
                    e -> e.getKey().toLocalDate().compareTo(startOfCredMonth) >=0 && e.getKey().toLocalDate().compareTo(endOfCredMonth) <=0 && e.getValue().equals(State.ACCEPTED)).count();
                    log.info("33333333333333333333 paidDays :{}", paidDays);
                Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                e -> e.getKey().toLocalDate().compareTo(credentialDate) >= 0 && e.getKey().toLocalDate().compareTo(endOfCredMonth) <=0 && e.getValue().equals(State.ACCEPTED)).count();
                log.info("4444444444444444444444444 offsetDays :{}", offsetDays);
                
                // Long paidDays = monitoredCase.getHistory().entrySet().stream().filter(
                //     e -> e.getKey().getMonthValue() == credentialDate.getMonthValue() && e.getKey().isAfter(startOfCredMonth) && e.getValue().equals(State.ACCEPTED)).count();

                // Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                //     e -> e.getKey().getMonthValue() == credentialDate.getMonthValue() && e.getKey().getYear() == credentialDate.getYear() && e.getKey().getDayOfMonth()>=credentialDate.getDayOfMonth() && e.getValue().equals(State.ACCEPTED)).count();
               
                BigDecimal paymentPerDayActual = payment.getPayment().divide(BigDecimal.valueOf(paidDays), 2, RoundingMode.HALF_UP);
                log.info("5555555555555555555555555 paymentPerDayActual :{}", paymentPerDayActual);
                projectedPayment = projectedPayment.add((BigDecimal.valueOf(paidDays - offsetDays ).multiply(paymentPerDayActual))
                .add(BigDecimal.valueOf(offsetDays).multiply(fullMonthProjection.divide(BigDecimal.valueOf(paidDays), 2, RoundingMode.HALF_UP))));
                log.info("6666666666666666666666666 projectedPayment :{}", projectedPayment);
            } else{
                log.info("payment after credential");
                Long paidDays = monitoredCase.getHistory().entrySet().stream().filter(
                    e -> e.getKey().toLocalDate().compareTo(paymentDate.withDayOfMonth(1)) >=0 && e.getKey().toLocalDate().compareTo(paymentDate.withDayOfMonth(monthDays(paymentDate))) <=0 && e.getValue().equals(State.ACCEPTED)).count();

                // add the projected payment for all accepted dates during this month
                projectedPayment = projectedPayment.add(fullMonthProjection.divide(BigDecimal.valueOf(monthDays(paymentDate)), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(paidDays)));
            } 
        }
        BigDecimal newOffset = totalPayment.subtract(projectedPayment);
        log.info("777777777777777777777777777 newOffset :{}", newOffset);
        monitoredCase.setOffset(newOffset);
        // if the offset in the case is diffrenent than the calculated offset the update the offset 
        // if(newOffset.compareTo(monitoredCase.getOffset()) != 0){
        //     monitoredCase.setOffset(newOffset);
        // }
    }

    public static void updateOffset3(LocalDate credentialDate, Case monitoredCase, SsiApplicationTest ssiApp){

        //Mock credential date, this illustrates a date at which a credential has been modified prior to being updated in the system
        //LocalDate credentialDate = LocalDate.of(2020, 8, 12);
        LocalDate startOfCredMonth = credentialDate.withDayOfMonth(1);
        LocalDate endOfCredMonth = credentialDate.withDayOfMonth(monthDays(credentialDate));
        
        // this should probably call the payment calculation method and return the value that should have been paid for all paid days with the new credentials
        //BigDecimal fullMonthProjection = BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalPayment = BigDecimal.ZERO;
        BigDecimal projectedPayment = BigDecimal.ZERO;

        for(CasePayment payment:monitoredCase.getPaymentHistory()){
            
            //sum the payment of each month to calculate the total paid amount 
            totalPayment = totalPayment.add(payment.getPayment());

            //if the payment of the month has failed add it all as offset(don't include it the projected sum)
            if(payment.getState().equals(State.FAILED)){
                continue;
            }

            // payment concerns the previous month of the actual date of payment
            LocalDate paymentDate = payment.getPaymentDate().minusMonths(1).toLocalDate();

            // update iff the month of the date is equal or after the date of the modified credential 
            if(paymentDate.isBefore(startOfCredMonth)){
                projectedPayment = projectedPayment.add(payment.getPayment());
                continue; 
            }
            log.info("111111111111111111111 offset :{}", monitoredCase.getOffset());
            BigDecimal fullMonthProjection = calculatePayment2(monthDays(paymentDate), monthDays(paymentDate), ssiApp, monitoredCase.getOffset());
            log.info("222222222222222222222 fullMonthProjection :{}", fullMonthProjection);
            if(fullMonthProjection == payment.getPayment()){
                projectedPayment = projectedPayment.add(payment.getPayment());
                continue; 
            }

            // if the payment concerns the same month as the altered credential, calculate the payment only of the offset days
            if(paymentDate.isBefore(endOfCredMonth)){
           
                Long paidDays = monitoredCase.getHistory().entrySet().stream().filter(
                    e -> e.getKey().toLocalDate().compareTo(startOfCredMonth) >=0 && e.getKey().toLocalDate().compareTo(endOfCredMonth) <=0 && e.getValue().equals(State.ACCEPTED)).count();
                    log.info("33333333333333333333 paidDays :{}", paidDays);
                Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                e -> e.getKey().toLocalDate().compareTo(credentialDate) >= 0 && e.getKey().toLocalDate().compareTo(endOfCredMonth) <=0 && e.getValue().equals(State.ACCEPTED)).count();
                log.info("4444444444444444444444444 offsetDays :{}", offsetDays);
                
                // Long paidDays = monitoredCase.getHistory().entrySet().stream().filter(
                //     e -> e.getKey().getMonthValue() == credentialDate.getMonthValue() && e.getKey().isAfter(startOfCredMonth) && e.getValue().equals(State.ACCEPTED)).count();

                // Long offsetDays = monitoredCase.getHistory().entrySet().stream().filter(
                //     e -> e.getKey().getMonthValue() == credentialDate.getMonthValue() && e.getKey().getYear() == credentialDate.getYear() && e.getKey().getDayOfMonth()>=credentialDate.getDayOfMonth() && e.getValue().equals(State.ACCEPTED)).count();
               
                BigDecimal paymentPerDayActual = payment.getPayment().divide(BigDecimal.valueOf(paidDays), 2, RoundingMode.HALF_UP);
                log.info("5555555555555555555555555 paymentPerDayActual :{}", paymentPerDayActual);
                projectedPayment = projectedPayment.add((BigDecimal.valueOf(paidDays - offsetDays ).multiply(paymentPerDayActual))
                .add(BigDecimal.valueOf(offsetDays).multiply(fullMonthProjection.divide(BigDecimal.valueOf(paidDays), 2, RoundingMode.HALF_UP))));
                log.info("6666666666666666666666666 projectedPayment :{}", projectedPayment);
            } else{
                log.info("payment after credential");
                Long paidDays = monitoredCase.getHistory().entrySet().stream().filter(
                    e -> e.getKey().toLocalDate().compareTo(paymentDate.withDayOfMonth(1)) >=0 && e.getKey().toLocalDate().compareTo(paymentDate.withDayOfMonth(monthDays(paymentDate))) <=0 && e.getValue().equals(State.ACCEPTED)).count();

                // add the projected payment for all accepted dates during this month
                projectedPayment = projectedPayment.add(fullMonthProjection.divide(BigDecimal.valueOf(monthDays(paymentDate)), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(paidDays)));
            } 
        }
        BigDecimal newOffset = totalPayment.subtract(projectedPayment);
        log.info("777777777777777777777777777 newOffset :{}", newOffset);
        monitoredCase.setOffset(newOffset);
        // if the offset in the case is diffrenent than the calculated offset the update the offset 
        // if(newOffset.compareTo(monitoredCase.getOffset()) != 0){
        //     monitoredCase.setOffset(newOffset);
        // }
    }
        
}
