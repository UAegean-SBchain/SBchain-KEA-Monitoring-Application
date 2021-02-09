package com.example.ethereumserviceapp.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExternalChecksResult {
    private boolean rejection;
    private LocalDate date;
}
