package com.example.ethereumserviceapp.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Optional;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.CasePayment;
import com.example.ethereumserviceapp.model.State;
import com.example.ethereumserviceapp.model.entities.SsiApplication;
import com.example.ethereumserviceapp.repository.SsiApplicationRepository;
import com.example.ethereumserviceapp.service.EthereumService;
import com.example.ethereumserviceapp.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaymentServiceImpl implements PaymentService{

    private EthereumService ethServ;

    final static BigDecimal paymentValPerDay = BigDecimal.valueOf(100);

    @Autowired
    public PaymentServiceImpl(EthereumService ethServ) {
        this.ethServ = ethServ;
    }

    @Autowired
    private SsiApplicationRepository ssiRepo;
    
    @Override
    @Scheduled(cron = "0 0 0 1 * ?")
    public void startPayment(){
        List<String> uuids = this.ethServ.getAllCaseUUID();
        uuids.stream().forEach(uuid -> {
            Integer numDays = 0;

            // get the case from the block chain
            Optional<Case> theCase = this.ethServ.getCaseByUUID(uuid);
            if(theCase.isPresent()){
                Case caseToBePaid = theCase.get();
                LocalDateTime startDate = caseToBePaid.getHistory().entrySet().iterator().next().getKey();
                LocalDateTime currentDate = LocalDateTime.now();
                BigDecimal paymentValue = BigDecimal.valueOf(0);
                CasePayment payment = new CasePayment();
                if (caseToBePaid.getState().equals(State.ACCEPTED)) {
                    Optional<SsiApplication> ssiApp = ssiRepo.findByUuid(uuid);
                    //check payment credentials
                    if(!ssiApp.isPresent() || !checkPaymentCredentials(caseToBePaid, ssiApp.get())){
                        return;
                    }
                    if(startDate.isBefore(currentDate)){
                        // calculate the number of days to be paid 
                        // if(currentDate.minusMonths(Long.valueOf(1)).isAfter(startDate) ){
                        //     numDays = monthDays(currentDate.minusMonths(Long.valueOf(1)));
                        // } else {
                        //     numDays = monthDays(startDate) - startDate.getDayOfMonth();
                        // }
                        
                        // calculate the number of days to be paid 
                        Long acceptedDates = caseToBePaid.getHistory().entrySet().stream().filter(
                        e -> e.getKey().getMonthValue() == currentDate.minusMonths(Long.valueOf(1)).getMonthValue() && e.getValue().equals(State.ACCEPTED)).count();

                        paymentValue = mockPaymentService(acceptedDates.intValue(), caseToBePaid.getOffset());
                        caseToBePaid.setState(State.PAID);
                        payment.setPaymentDate(currentDate);
                        payment.setPayment(paymentValue);
                        ethServ.addPayment(caseToBePaid, payment);
                    }
                }
                //if case is rejected then check the previous month history for days that the case was accepted
                if (caseToBePaid.getState().equals(State.REJECTED) || caseToBePaid.getState().equals(State.PAUSED)) {
                    int paymentMonth = currentDate.minusMonths(Long.valueOf(1)).getMonthValue();
                    // get the number of days of the previous month that the case was accepted
                    Long acceptedDates = caseToBePaid.getHistory().entrySet().stream().filter(
                        e -> e.getKey().getMonthValue() == paymentMonth && e.getKey().isAfter(currentDate.minusMonths(1)) && e.getValue().equals(State.ACCEPTED)).count();
                    if(acceptedDates.intValue() > 0){
                        Optional<SsiApplication> ssiApp = ssiRepo.findByUuid(uuid);
                        //check payment credentials
                        if(ssiApp.isPresent() && checkPaymentCredentials(caseToBePaid, ssiApp.get())){
                            paymentValue = mockPaymentService(acceptedDates.intValue(), caseToBePaid.getOffset());
                            payment.setPaymentDate(currentDate);
                            payment.setPayment(paymentValue);
                            ethServ.addPayment(caseToBePaid, payment);
                            //caseToBePaid.setState(State.PAID);
                            //ethServ.updateCase(caseToBePaid);
                        }
                    } else if(caseToBePaid.getState().equals(State.REJECTED) ){
                        // if the case's state is rejected and there are no days during the month tha the case was accepted delete it from the block chain 
                        ethServ.deleteCaseByUuid(uuid);
                    }

                    
                }
            }
        });
    }

    private BigDecimal mockPaymentService(Integer days, BigDecimal offset){
        BigDecimal valueToBePaid = (BigDecimal.valueOf(days).multiply(paymentValPerDay)).subtract(offset);

        return valueToBePaid;
    }

    private Boolean checkPaymentCredentials(Case caseToBePaid, SsiApplication ssiApp){
        //mock household check
        Map<String, String>[] houseHold = ssiApp.getHouseholdComposition();
        for(int i = 0; i < houseHold.length; i++){
            if(houseHold[i].entrySet().stream().anyMatch(h -> h.getValue().equals("deceased"))){
                return false;
            }
        }
        //external oaed check
        if(!oaedRegistrationCheck(ssiApp.getOaedId())){
            return false;
        }
        //external housing subsidy check
        if(houseBenefitCheck(ssiApp.getTaxisAfm())){
            return false;
        }
        // check if meter number appears on other applications
        if(ssiRepo.findByMeterNumber(ssiApp.getMeterNumber()).size() > 1){
            return false;
        }
        // check for luxury living
        if(ssiApp.getLuxury().equals(String.valueOf(Boolean.TRUE))){
            return false;
        }
        //check OAED benefits
        if(BigInteger.valueOf(Long.valueOf(ssiApp.getUnemploymentBenefitR())).compareTo(BigInteger.valueOf(500)) > 0 ){
            return false;
        }
        //check other benefits
        if(BigInteger.valueOf(Long.valueOf(ssiApp.getOtherBenefitsR())).compareTo(BigInteger.valueOf(500)) > 0 ){
            return false;
        }
        //check Ergome benefits
        if(BigInteger.valueOf(Long.valueOf(ssiApp.getErgomeR())).compareTo(BigInteger.valueOf(500)) > 0 ){
            return false;
        }

        //check if two months have passed while the application is in status paused
        Iterator<Entry<LocalDateTime, State>> it = caseToBePaid.getHistory().entrySet().iterator();
        LocalDate pausedStartDate = LocalDate.of(1900, 1, 1);
        LocalDate pausedEndDate = LocalDate.of(1900, 1, 1);
        while(it.hasNext()){
            if(pausedEndDate.equals(pausedStartDate.plusMonths(2))){
                return false;
            }
            Map.Entry<LocalDateTime, State> entry = it.next();
            if(!entry.getValue().equals(State.PAUSED)){
                pausedStartDate = LocalDate.of(1900, 1, 1);
                continue;
            }
            if(pausedStartDate.equals(LocalDate.of(1900, 1, 1))){
                pausedStartDate = entry.getKey().toLocalDate();
            }
            pausedEndDate = entry.getKey().toLocalDate();
            
        }
        // check that if there differences in Amka register
        if(differenceInAmka(ssiApp.getTaxisAmka())){
            return false;
        }
        //check if iban exists in other application
        if(ssiRepo.findByIban(ssiApp.getIban()).size() > 1){
            return false;
        }

        return true;
    }

    //mock oaed registration check
    private Boolean oaedRegistrationCheck(String oaedId){
        return true;
    }
    //mock housing subsidy check
    private Boolean houseBenefitCheck(String afm){
        return true;
    }

    //mockAmkaCheck
    private Boolean differenceInAmka(String amka){
        return false;
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
}
