package com.example.ethereumserviceapp;

import com.example.ethereumserviceapp.model.BooleanMockResult;
import com.example.ethereumserviceapp.model.entities.SsiApplication;
import com.example.ethereumserviceapp.service.MockServices;
import com.example.ethereumserviceapp.service.impl.MockServicesImpl;
import com.example.ethereumserviceapp.utils.CsvUtils;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TestMockServices {


    @Test
    public void testOtheBenefits() {
        MockServices ms = new MockServicesImpl();
        LocalDate today = LocalDate.now();
        final List<SsiApplication> houshold = CsvUtils.generateMockData(200);
        int num = 0;
        for (SsiApplication app : houshold) {
            LocalDate start = app.getTime();
            if (ms.getUpdatedOtherBenefitValue(start,today,0.066666667,app, true, houshold).isPresent())
                num++;
        }
        System.out.println("UPDATED:: " + num);

    }

    @Test
    public void testDeaths() {
        MockServices ms = new MockServicesImpl();
        LocalDate today = LocalDate.now();
        final List<SsiApplication> household = CsvUtils.generateMockData(200);
        int num = 0;
        for (SsiApplication app : household) {
            Optional<BooleanMockResult> res = ms.getDeaths(app.getTime(),today,0.066666667,app, true, household);
            if (res.isPresent()){
                System.out.println(res.get().getData());
                num++;
            }
        }
        System.out.println("UPDATED:: " + num);
    }

    @Test
    public void testOAED() {
        MockServices ms = new MockServicesImpl();
        LocalDate today = LocalDate.now();
        final List<SsiApplication> household = CsvUtils.generateMockData(200);
        int num = 0;
        for (SsiApplication app : household) {
            Optional<BooleanMockResult> res = ms.getOAEDRegistration(app.getTime(),today,0.066666667,app, true, household);
            if (res.isPresent() && !res.isEmpty()){
                System.out.println(res.get().getData());
                num++;
            }
        }
        System.out.println("UPDATED:: " + num);
    }


}
