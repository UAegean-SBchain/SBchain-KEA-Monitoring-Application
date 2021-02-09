package com.example.ethereumserviceapp.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ExternalAPICallResult {

    private int count;
    private LocalDate date;
}
