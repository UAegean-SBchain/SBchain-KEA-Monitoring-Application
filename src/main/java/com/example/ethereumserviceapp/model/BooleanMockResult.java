package com.example.ethereumserviceapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BooleanMockResult {
    private LocalDate date;
    private boolean value;
    private String data;
    private String uuid;

}
