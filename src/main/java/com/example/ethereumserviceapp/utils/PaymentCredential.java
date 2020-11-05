package com.example.ethereumserviceapp.utils;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaymentCredential {
    
    private LocalDateTime date;
    private String name;
    private String value;
    private Integer days;
}
