package com.example.ethereumserviceapp.model;

import java.util.List;

import com.example.ethereumserviceapp.model.entities.SsiApplication;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class CaseAppDTO {
    
    Case principalCase;
    List<SsiApplication> householdApps;
}
