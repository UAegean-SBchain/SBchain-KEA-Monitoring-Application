package com.example.ethereumserviceapp.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.CasePayment;
import com.example.ethereumserviceapp.model.State;
import com.example.ethereumserviceapp.model.entities.SsiApplication;
import com.example.ethereumserviceapp.service.EthereumService;
import com.example.ethereumserviceapp.service.MongoService;
import com.example.ethereumserviceapp.service.PaymentService;
import com.example.ethereumserviceapp.utils.EthAppUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaymentServiceImpl implements PaymentService{

    private EthereumService ethServ;
    private MongoService mongoServ;

    final static BigDecimal paymentValPerDay = BigDecimal.valueOf(100);

    @Autowired
    public PaymentServiceImpl(EthereumService ethServ, MongoService mongoServ) {
        this.ethServ = ethServ;
        this.mongoServ = mongoServ;
    }
    
    @Override
    @Scheduled(cron = "0 0 0 1 * ?")
    public void startPayment(){
        
        List<String> uuids = this.ethServ.getAllCaseUUID();
        uuids.stream().forEach(uuid -> {
            // get the case from the block chain
            Optional<Case> theCase = this.ethServ.getCaseByUUID(uuid);
            if(!theCase.isPresent()){
                return;
            }
            Case caseToBePaid = theCase.get();
            LocalDateTime startDate = caseToBePaid.getHistory().entrySet().iterator().next().getKey();
            LocalDateTime currentDate = LocalDateTime.now();
            BigDecimal paymentValue = BigDecimal.valueOf(0);
            if (caseToBePaid.getState().equals(State.ACCEPTED)) {
                Optional<SsiApplication> ssiApp = mongoServ.findByUuid(uuid);
                //check payment credentials
                if(!ssiApp.isPresent() || !checkPaymentCredentials(caseToBePaid, ssiApp.get())){
                    return;
                }
                if(startDate.isBefore(currentDate)){
                    Long acceptedDates= caseToBePaid.getHistory().entrySet().stream().filter(
                        e -> (e.getKey().toLocalDate().compareTo(currentDate.toLocalDate().minusMonths(1)) >= 0) 
                        && e.getKey().toLocalDate().isBefore(currentDate.toLocalDate())
                        && e.getValue().equals(State.ACCEPTED)).count();
                    paymentValue = EthAppUtils.calculatePayment(acceptedDates.intValue(), caseToBePaid.getOffset(), ssiApp.get());
                    //Call to payment service
                    State paymentState = paymentService(paymentValue, caseToBePaid);
                    addPayment(paymentValue, caseToBePaid, currentDate, paymentState);
                }
            }
            //if case is rejected then check the previous month history for days during which the case was accepted
            if (caseToBePaid.getState().equals(State.REJECTED) || caseToBePaid.getState().equals(State.PAUSED)) {
                // get the number of days of the previous month during which the case was accepted
                Long acceptedDates= caseToBePaid.getHistory().entrySet().stream().filter(
                        e -> (e.getKey().toLocalDate().compareTo(currentDate.toLocalDate().minusMonths(1)) >= 0) 
                        && e.getKey().toLocalDate().isBefore(currentDate.toLocalDate())
                        && e.getValue().equals(State.ACCEPTED)).count();
                if(acceptedDates.intValue() > 0){
                    Optional<SsiApplication> ssiApp = mongoServ.findByUuid(uuid);
                    //check payment credentials
                    if(ssiApp.isPresent() && checkPaymentCredentials(caseToBePaid, ssiApp.get())){
                        paymentValue = EthAppUtils.calculatePayment(acceptedDates.intValue(), caseToBePaid.getOffset(), ssiApp.get());
                        //Call to payment service
                        State paymentState = paymentService(paymentValue, caseToBePaid);
                        addPayment(paymentValue, caseToBePaid, currentDate, paymentState);
                    }
                } else if(caseToBePaid.getState().equals(State.REJECTED) ){
                    // if the case's state is rejected and there are no days during the month during which the case was accepted, delete it from the block chain 
                    ethServ.deleteCaseByUuid(uuid);
                }
            }
        
        });
    }

    private State paymentService(BigDecimal valueToBePaid, Case caseToBePaid){
        //mock Call to external service
        if(!mockExternalPaymentService(valueToBePaid, caseToBePaid.getUuid())){
            caseToBePaid.setOffset(BigDecimal.valueOf(0));
            //caseToBePaid.setState(State.PAID);
            return State.FAILED;
        } 
        caseToBePaid.setOffset(BigDecimal.valueOf(0));
        return State.PAID;
    }

    private Boolean mockExternalPaymentService(BigDecimal valueToBePaid, String uuid){
        return true;
    }

    private void addPayment(BigDecimal valueToBePaid, Case caseToBePaid, LocalDateTime currentDate, State state){
        CasePayment payment = new CasePayment();
        payment.setPaymentDate(currentDate);
        payment.setPayment(valueToBePaid);
        payment.setState(state);
        ethServ.addPayment(caseToBePaid, payment);
        log.info("new payment :{}", payment);
    }

    private Boolean checkPaymentCredentials(Case caseToBePaid, SsiApplication ssiApp){
        //mock household check
        Map<String, String>[] houseHold = ssiApp.getHouseholdComposition();
        if(houseHold != null){
            for(int i = 0; i < houseHold.length; i++){
                if(houseHold[i].entrySet().stream().anyMatch(h -> h.getValue().equals("deceased"))){
                    return false;
                }
            }
        }
        //external oaed check
        if(!oaedRegistrationCheck(ssiApp.getOaedId())){
            return false;
        }
        //external housing subsidy check
        if(!houseBenefitCheck(ssiApp.getTaxisAfm())){
            return false;
        }
        // check if meter number appears on other applications
        if(mongoServ.findByMeterNumber(ssiApp.getMeterNumber()).size() > 1){
            return false;
        }
        // check for luxury living
        if(ssiApp.getLuxury() == null? false : ssiApp.getLuxury().equals(String.valueOf(Boolean.TRUE))){
            return false;
        }
        //check OAED benefits
        if(BigInteger.valueOf(Long.valueOf(ssiApp.getUnemploymentBenefitR() == null? "0" : ssiApp.getUnemploymentBenefitR())).compareTo(BigInteger.valueOf(300)) > 0 ){
            return false;
        }
        //economics check
        if(EthAppUtils.getTotalMonthlyValue(ssiApp).compareTo(BigDecimal.valueOf(0)) == 0){
            return false;
        }
        //check Ergome benefits
        if(BigInteger.valueOf(Long.valueOf(ssiApp.getErgomeR() == null? "0" : ssiApp.getErgomeR())).compareTo(BigInteger.valueOf(300)) > 0 ){
            return false;
        }
        //check for failed payments
        if(caseToBePaid.getPaymentHistory() == null? false : caseToBePaid.getPaymentHistory().stream().filter(s -> s.getState().equals(State.FAILED)).count() >= 3){
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

        //check for duplicates in households
        Map<String, String>[] householdArray = ssiApp.getHouseholdComposition();
        if(householdArray != null){
            for(int i=0; i<householdArray.length; i++){
                Map<String, String> household = householdArray[i];
                List<SsiApplication> hSsiApp = mongoServ.findByHouseholdCompositionIn(household);
                if(hSsiApp.size()>1){
                    return false;
                }
            }
        }
        //check if iban exists in other application
        if(mongoServ.findByIban(ssiApp.getIban()).size() > 1){
            return false;
        }
        log.info("return check true ?");
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
}
