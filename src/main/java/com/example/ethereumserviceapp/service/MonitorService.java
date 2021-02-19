/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp.service;

import java.time.LocalDateTime;
import java.util.List;

import com.example.ethereumserviceapp.model.CaseAppDTO;
import com.example.ethereumserviceapp.model.entities.SsiApplication;

/**
 *
 * @author nikos
 */
public interface MonitorService {

    void startScheduledMonitoring();

    void startMonitoring(LocalDateTime currentDate, Boolean isTest, double pValue, Boolean makeMockChecks, List<CaseAppDTO> storeDataForSE);

}
