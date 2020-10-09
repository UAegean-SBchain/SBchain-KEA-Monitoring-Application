package com.example.ethereumserviceapp.service.impl;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.CasePayment;
import com.example.ethereumserviceapp.model.State;
import com.example.ethereumserviceapp.service.EthereumService;
import com.example.ethereumserviceapp.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaymentServiceImpl implements PaymentService{

    private EthereumService ethServ;

    final static BigInteger paymentValPerDay = BigInteger.valueOf(100);

    @Autowired
    public PaymentServiceImpl(EthereumService ethServ) {
        this.ethServ = ethServ;
    }
    
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
                BigInteger paymentValue = BigInteger.valueOf(0);
                CasePayment payment = new CasePayment();
                if (caseToBePaid.getState().equals(State.ACCEPTED)) {
                    if(startDate.isBefore(currentDate)){
                        // calculate the number of days to be paid 
                        if(currentDate.minusMonths(Long.valueOf(1)).isAfter(startDate) ){
                            numDays = monthDays(currentDate.minusMonths(Long.valueOf(1)));
                        } else {
                            numDays = monthDays(startDate) - startDate.getDayOfMonth();
                        }
                        paymentValue = mockPaymentService(numDays, caseToBePaid.getOffset());
                        caseToBePaid.setState(State.PAID);
                        payment.setPaymentDate(currentDate);
                        payment.setPayment(paymentValue);
                        ethServ.addPayment(caseToBePaid, payment);
                    }
                }
                //if case is rejected then check the previous month history for days that the case was accepted
                if (caseToBePaid.getState().equals(State.REJECTED)) {
                    int paymentMonth = currentDate.minusMonths(Long.valueOf(1)).getMonthValue();
                    // get the number of days of the previous month that the case was accepted
                    Long acceptedDates = caseToBePaid.getHistory().entrySet().stream().filter(
                        e -> e.getKey().getMonthValue() == paymentMonth && e.getKey().isAfter(currentDate.minusMonths(1)) && e.getValue().equals(State.ACCEPTED)).count();
                    if(acceptedDates.intValue() > 0){

                        paymentValue = mockPaymentService(acceptedDates.intValue(), caseToBePaid.getOffset());
                        payment.setPaymentDate(currentDate);
                        payment.setPayment(paymentValue);
                        ethServ.addPayment(caseToBePaid, payment);
                        //caseToBePaid.setState(State.PAID);
                        //ethServ.updateCase(caseToBePaid);
                    }

                    // if the case's state is rejected delete it from the block chain after possible payment for the remaining "accepted" days
                    ethServ.deleteCaseByUuid(uuid);
                }
            }
        });
    }

    private BigInteger mockPaymentService(Integer days, BigInteger offset){
        BigInteger valueToBePaid = (BigInteger.valueOf(days).multiply(paymentValPerDay)).subtract(offset);

        return valueToBePaid;
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
