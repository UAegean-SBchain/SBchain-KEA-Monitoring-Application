package com.example.ethereumserviceapp.model;

import java.math.BigInteger;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CasePayment {

    LocalDateTime paymentDate;
    BigInteger payment;
    BigInteger offset; 
    Boolean isOffsetPaid;
    
}
