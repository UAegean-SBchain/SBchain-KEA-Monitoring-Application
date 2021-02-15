package com.example.ethereumserviceapp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class GeneratePopFormBind {
    private int crossBorder;
    private int women;
    private int married;
    private int parents;
    private int underAge;
    private int employed;
}
