package com.example.ethereumserviceapp.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BooleanMockResult {
    //private LocalDate date;
    private LocalDateTime date;
    private boolean value;
    private String data;
    private String uuid;

}
