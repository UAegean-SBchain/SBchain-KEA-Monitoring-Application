package com.example.ethereumserviceapp.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ExternalAPICallResult {

    private Integer count;
    //private LocalDate date;
    private LocalDateTime date;
    private Boolean changed;
    private RejectionCode rejectionCode;
}
