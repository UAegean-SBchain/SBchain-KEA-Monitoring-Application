/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp.utils;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.State;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;
import org.web3j.tuples.generated.Tuple5;

/**
 *
 * @author nikos
 */
public class ContractBuilder {

    public static Case buildCaseFromTuple(Tuple5<byte[], String, Boolean, BigInteger, BigInteger> theCase) {
        Case transformedCase = new Case();
        ByteBuffer byteBuffer = ByteBuffer.wrap(theCase.component1());
        Long high = byteBuffer.getLong();
        Long low = byteBuffer.getLong();
        transformedCase.setUuid(String.valueOf(new UUID(high, low)));
        transformedCase.setName(theCase.component2());
        transformedCase.setIsStudent(theCase.component3());
        transformedCase.setDate(Instant.ofEpochMilli(theCase.component4().longValue()).atZone(ZoneId.systemDefault()).toLocalDateTime());
        transformedCase.setState(State.values()[theCase.component5().intValue()]);
        return transformedCase;
    }

}
