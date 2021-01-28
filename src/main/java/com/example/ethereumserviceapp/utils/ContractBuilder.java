/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.CasePayment;
import com.example.ethereumserviceapp.model.State;

import org.web3j.tuples.generated.Tuple4;
import org.web3j.tuples.generated.Tuple8;
import org.web3j.tuples.generated.Tuple9;
import org.web3j.utils.Numeric;

/**
 *
 * @author nikos
 */
public class ContractBuilder {

    public static Case buildCaseFromTuple(Tuple8<byte[], BigInteger, List<BigInteger>,
            List<BigInteger>, List<BigInteger>, BigInteger, BigInteger, BigInteger> theCase) {
        Case transformedCase = new Case();
        //List<CasePayment> paymentHistory = new ArrayList<>();
        LinkedHashMap<LocalDateTime, State> history = new LinkedHashMap<>();
        LinkedHashMap<LocalDateTime, BigDecimal> dailyBenefit = new LinkedHashMap<>();
        transformedCase.setUuid(ByteConverters.hexToASCII(Numeric.toHexStringNoPrefix((byte[]) theCase.component1())));
        transformedCase.setDate(Instant.ofEpochMilli(theCase.component2().longValue()).atZone(ZoneId.systemDefault()).toLocalDateTime());
        transformedCase.setState(State.values()[theCase.component6().intValue()]);

        for(int i=0; i<theCase.component3().size(); i++){
            history.put(Instant.ofEpochMilli(theCase.component3().get(i).longValue()).atZone(ZoneId.systemDefault()).toLocalDateTime(), State.values()[theCase.component4().get(i).intValue()]);
            transformedCase.setHistory(history);
            dailyBenefit.put(Instant.ofEpochMilli(theCase.component3().get(i).longValue()).atZone(ZoneId.systemDefault()).toLocalDateTime(), new BigDecimal(theCase.component5().get(i)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
            transformedCase.setDailyBenefit(dailyBenefit);
        }

        // for(int i=0; i<theCase.component6().size(); i++){
        //     CasePayment transformedPayment = new CasePayment();
        //     if(theCase.component8().get(i).intValue() != 0){
        //         transformedPayment.setPaymentDate(Instant.ofEpochMilli(theCase.component6().get(i).longValue()).atZone(ZoneId.systemDefault()).toLocalDateTime());
        //         transformedPayment.setPayment(new BigDecimal(theCase.component7().get(i)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        //         transformedPayment.setState(State.values()[theCase.component8().get(i).intValue()]);
        //         paymentHistory.add(transformedPayment);
        //     }
            
        // }
        //transformedCase.setPaymentHistory(paymentHistory);
        transformedCase.setOffset(new BigDecimal(theCase.component7()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        if(theCase.component8().longValue() == 0){
            transformedCase.setRejectionDate(LocalDate.of(1900,0,0));
        }else{
            transformedCase.setRejectionDate(Instant.ofEpochMilli(theCase.component8().longValue()).atZone(ZoneId.systemDefault()).toLocalDate());
        }
        return transformedCase;
    }

    public static void linkPaymentToCase(Tuple4<byte[], List<BigInteger>,
            List<BigInteger>, List<BigInteger>> thePayment, Case theCase){

        List<CasePayment> paymentHistory = new ArrayList<>();

        for(int i=0; i<thePayment.component4().size(); i++){
            CasePayment transformedPayment = new CasePayment();
            if(thePayment.component4().get(i).intValue() != 0){
                transformedPayment.setPaymentDate(Instant.ofEpochMilli(thePayment.component2().get(i).longValue()).atZone(ZoneId.systemDefault()).toLocalDateTime());
                transformedPayment.setPayment(new BigDecimal(thePayment.component3().get(i)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                transformedPayment.setState(State.values()[thePayment.component4().get(i).intValue()]);
                paymentHistory.add(transformedPayment);
            }
        }
        theCase.setPaymentHistory(paymentHistory);
    }

}
