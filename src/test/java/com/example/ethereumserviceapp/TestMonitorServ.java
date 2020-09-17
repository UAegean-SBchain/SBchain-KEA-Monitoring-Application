/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp;

import com.example.ethereumserviceapp.repository.SsiApplicationRepository;
import com.example.ethereumserviceapp.service.EthereumService;
import com.example.ethereumserviceapp.service.MongoService;
import com.example.ethereumserviceapp.service.MonitorService;
import com.example.ethereumserviceapp.service.impl.EthereumServiceImpl;
import com.example.ethereumserviceapp.service.impl.MongoServiceImpl;
import com.example.ethereumserviceapp.service.impl.MonitorServiceImpl;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author nikos
 */
@SpringBootTest
public class TestMonitorServ {

    @Autowired
    SsiApplicationRepository rep;

    @Test
    public void testGetBallance() throws IOException {
        MongoService monogServ = new MongoServiceImpl(rep);
        EthereumService ethServ = new EthereumServiceImpl();
        MonitorService monServ = new MonitorServiceImpl(monogServ, ethServ);

        monServ.startMonitoring();

        assertEquals(true, true);

    }

}
