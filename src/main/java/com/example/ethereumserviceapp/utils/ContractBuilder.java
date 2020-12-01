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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.CasePayment;
import com.example.ethereumserviceapp.model.State;

import org.web3j.tuples.generated.Tuple9;
import org.web3j.utils.Numeric;

/**
 *
 * @author nikos
 */
public class ContractBuilder {

    public static Case buildCaseFromTuple(Tuple9<byte[], BigInteger, List<BigInteger>, List<BigInteger>, BigInteger, List<BigInteger>, List<BigInteger>, List<BigInteger>, BigInteger> theCase) {
        Case transformedCase = new Case();
        List<CasePayment> paymentHistory = new ArrayList<>();
        LinkedHashMap<LocalDateTime, State> history = new LinkedHashMap<>();
        transformedCase.setUuid(ByteConverters.hexToASCII(Numeric.toHexStringNoPrefix((byte[]) theCase.component1())));
        transformedCase.setDate(Instant.ofEpochMilli(theCase.component2().longValue()).atZone(ZoneId.systemDefault()).toLocalDateTime());
        transformedCase.setState(State.values()[theCase.component5().intValue()]);

        for(int i=0; i<theCase.component3().size(); i++){
            history.put(Instant.ofEpochMilli(theCase.component3().get(i).longValue()).atZone(ZoneId.systemDefault()).toLocalDateTime(), State.values()[theCase.component4().get(i).intValue()]);
            transformedCase.setHistory(history);
        }

        for(int i=0; i<theCase.component6().size(); i++){
            CasePayment transformedPayment = new CasePayment();
            if(theCase.component8().get(i).intValue() != 0){
                transformedPayment.setPaymentDate(Instant.ofEpochMilli(theCase.component6().get(i).longValue()).atZone(ZoneId.systemDefault()).toLocalDateTime());
                transformedPayment.setPayment(new BigDecimal(theCase.component7().get(i)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                transformedPayment.setState(State.values()[theCase.component8().get(i).intValue()]);
                paymentHistory.add(transformedPayment);
            }
            
        }
        transformedCase.setPaymentHistory(paymentHistory);
        transformedCase.setOffset(new BigDecimal(theCase.component9()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        return transformedCase;
    }

}
