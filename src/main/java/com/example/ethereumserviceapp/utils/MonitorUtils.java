/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp.utils;

import com.example.ethereumserviceapp.model.entities.SsiApplication;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author nikos
 */
@Slf4j
public class MonitorUtils {
//mock checks replace with correct ones

    public static Boolean isApplicationAccepted(SsiApplication ssiApp) {
        try {
            String employmentStatus = ssiApp.getEmploymentStatus();
            String hospitalized = ssiApp.getHospitalized();
            Long totalIncome = Long.valueOf(ssiApp.getTotalIncome());
            ssiApp.getTaxisFamilyName();
            ssiApp.getTaxisFirstName();

            if (employmentStatus.equals("unemployed") || totalIncome < Long.valueOf(10000) || hospitalized.equals("true")) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }
}
