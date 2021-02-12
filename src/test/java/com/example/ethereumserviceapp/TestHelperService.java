package com.example.ethereumserviceapp;

import com.example.ethereumserviceapp.service.HelperService;
import com.example.ethereumserviceapp.service.MonitorService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class TestHelperService {

    @InjectMocks
    HelperService helpService;

    @Autowired
    private MonitorService monitorService;
    
    @Test
    public void testRunMonitoring() {
        helpService.runMonitoring("2021-02-17T13:45", "2", 0.1);
    }
}
