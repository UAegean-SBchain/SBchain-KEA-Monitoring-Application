package com.example.ethereumserviceapp;

import com.example.ethereumserviceapp.model.BooleanMockResult;
import com.example.ethereumserviceapp.model.entities.SsiApplication;
import com.example.ethereumserviceapp.service.MockServices;
import com.example.ethereumserviceapp.service.impl.MockServicesImpl;
import com.example.ethereumserviceapp.utils.CsvUtils;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

public class TestMockServices {


    @Test
    public void testOtheBenefits() {
        MockServices ms = new MockServicesImpl();

        final List<SsiApplication> houshold = CsvUtils.generateMockData(200);
        int num = 0;
        for (SsiApplication app : houshold) {
            if (ms.getUpdatedOtherBenefitValue(app, true, houshold).isPresent())
                num++;
        }
        System.out.println("UPDATED:: " + num);

    }

    @Test
    public void testDeaths() {
        MockServices ms = new MockServicesImpl();

        final List<SsiApplication> household = CsvUtils.generateMockData(200);
        int num = 0;
        for (SsiApplication app : household) {
            Optional<BooleanMockResult> res = ms.getDeaths(app, true, household);
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
        final List<SsiApplication> household = CsvUtils.generateMockData(200);
        int num = 0;
        for (SsiApplication app : household) {
            Optional<BooleanMockResult> res = ms.getOAEDRegistration(app, true, household);
            if (res.isPresent()){
                System.out.println(res.get().getData());
                num++;
            }
        }
        System.out.println("UPDATED:: " + num);
    }


}
