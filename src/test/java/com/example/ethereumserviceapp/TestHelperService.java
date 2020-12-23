package com.example.ethereumserviceapp;

import com.example.ethereumserviceapp.service.HelperService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class TestHelperService {

    @InjectMocks
    HelperService helpService;
    
    @Test
    public void testRunMonitoring() {
        helpService.runMonitoringOnCase("1SiDa1");
    }
}
