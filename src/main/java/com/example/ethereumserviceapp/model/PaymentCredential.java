package com.example.ethereumserviceapp.model;

import java.time.LocalDateTime;
import java.util.List;

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
    private String afm;
    private List<HouseholdMember> household;
}
