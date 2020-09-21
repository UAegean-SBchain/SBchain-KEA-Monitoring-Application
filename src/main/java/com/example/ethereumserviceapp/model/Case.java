package com.example.ethereumserviceapp.model;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

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
    private String name;
    private Boolean isStudent;
    private LocalDateTime date;
    private State state;
    private LinkedHashMap<LocalDateTime, State> history;

}
