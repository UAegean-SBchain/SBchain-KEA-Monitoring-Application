package com.example.ethereumserviceapp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Case {

    private String uuid;
    private LocalDateTime date;
    private State state;
    private LinkedHashMap<LocalDateTime, State> history;
    private LinkedHashMap<LocalDateTime, BigDecimal> dailyBenefit;
    private LinkedHashMap<LocalDateTime, BigDecimal> dailySums;
    private List<CaseHistory> caseHistory;
    private List<CasePayment> paymentHistory;
    private BigDecimal offset;
    private String rejectionDate;
    private RejectionCode rejectionCode;
    // helper value does not get saved in BC
    private BigDecimal dailyValue;
    private BigDecimal dailySum;
}
