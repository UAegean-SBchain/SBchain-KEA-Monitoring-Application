/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp.service;

import com.example.ethereumserviceapp.model.entities.SsiApplication;

/**
 *
 * @author nikos
 */
public interface MonitorService {

    public void startMonitoring();

    public Boolean checkIndividualCredentials(SsiApplication ssiApp);

}
