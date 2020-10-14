package com.example.ethereumserviceapp.model;

import java.math.BigDecimal;
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
    private List<CasePayment> paymentHistory;
    private BigDecimal offset;
}
