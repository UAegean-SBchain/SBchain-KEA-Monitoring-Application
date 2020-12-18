/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp.service;

import java.time.LocalDateTime;

import com.example.ethereumserviceapp.model.entities.SsiApplication;

/**
 *
 * @author nikos
 */
public interface MonitorService {

    public void startScheduledMonitoring();

    public void startMonitoring(LocalDateTime currentDate);

    public Boolean checkIndividualCredentials(SsiApplication ssiApp);

}
