/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp.utils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.State;

import org.web3j.tuples.generated.Tuple7;
import org.web3j.utils.Numeric;

/**
 *
 * @author nikos
 */
public class ContractBuilder {

    public static Case buildCaseFromTuple(Tuple7<byte[], String, Boolean, BigInteger, List<BigInteger>, List<BigInteger>, BigInteger> theCase) {
        Case transformedCase = new Case();
        LinkedHashMap<LocalDateTime, State> history = new LinkedHashMap<>();
        // ByteBuffer byteBuffer = ByteBuffer.wrap(theCase.component1());
        // Long high = byteBuffer.getLong();
        // Long low = byteBuffer.getLong();

        //transformedCase.setUuid(String.valueOf(new UUID(high, low)));
        transformedCase.setUuid(ByteConverters.hexToASCII(Numeric.toHexStringNoPrefix((byte[]) theCase.component1())));
        transformedCase.setName(theCase.component2());
        transformedCase.setIsStudent(theCase.component3());
        transformedCase.setDate(Instant.ofEpochMilli(theCase.component4().longValue()).atZone(ZoneId.systemDefault()).toLocalDateTime());
        transformedCase.setState(State.values()[theCase.component7().intValue()]);

        for(int i=0; i<theCase.component5().size(); i++){
            history.put(Instant.ofEpochMilli(theCase.component5().get(i).longValue()).atZone(ZoneId.systemDefault()).toLocalDateTime(), State.values()[theCase.component6().get(i).intValue()]);
            transformedCase.setHistory(history);
        }
        return transformedCase;
    }

}
