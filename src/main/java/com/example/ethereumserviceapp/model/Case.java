package com.example.ethereumserviceapp.model;

import java.time.LocalDateTime;
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
    private LocalDateTime date;
    private State state;

}
