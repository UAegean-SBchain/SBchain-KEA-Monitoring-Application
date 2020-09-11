package com.example.ethereumserviceapp.utils;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Case {
    
    private String uuid;
    private String name;
    private Boolean isStudent;
    private LocalDate date;
    private State state;
    
}
