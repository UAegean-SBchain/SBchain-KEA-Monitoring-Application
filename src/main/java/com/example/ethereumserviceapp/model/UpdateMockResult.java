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
public class UpdateMockResult {
    //private LocalDate date;
    private LocalDateTime date;
    private Double value;
    private String uuid;

}
