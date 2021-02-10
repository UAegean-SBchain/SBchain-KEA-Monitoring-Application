package com.example.ethereumserviceapp.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExternalChecksResult {
    private Boolean rejection;
    private LocalDate date;
    private Boolean changedFinancials;
    private int count;
}
