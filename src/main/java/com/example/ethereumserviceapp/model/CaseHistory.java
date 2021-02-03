package com.example.ethereumserviceapp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CaseHistory {
    
    private LocalDateTime date;
    private BigDecimal dailyBenefit;
    private State state;
    private BigDecimal dailySum;
}
