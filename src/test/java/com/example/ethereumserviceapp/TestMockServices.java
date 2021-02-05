package com.example.ethereumserviceapp;

import com.example.ethereumserviceapp.model.BooleanMockResult;
import com.example.ethereumserviceapp.model.entities.SsiApplication;
import com.example.ethereumserviceapp.service.MockServices;
import com.example.ethereumserviceapp.service.impl.MockServicesImpl;
import com.example.ethereumserviceapp.utils.CsvUtils;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestMockServices {


    @Test
    public void testOtheBenefits() {
        MockServices ms = new MockServicesImpl();
        LocalDate today = LocalDate.now();
        final List<SsiApplication> allApps = CsvUtils.generateMockData(200);
        int num = 0;
        for (SsiApplication app : allApps) {
            LocalDate start = app.getTime();

            List<SsiApplication> householdApps =
                    new ArrayList<>();
            for (SsiApplication innerApp : allApps) {
                if (innerApp.getHouseholdPrincipal().getAfm().equals(app.getHouseholdPrincipal().getAfm())) {
                    householdApps.add(innerApp);
                }
            }

            if (ms.getUpdatedOtherBenefitValue(start,today,0.066666667,app, true, householdApps).isPresent())
                num++;
        }
        System.out.println("UPDATED:: " + num);

    }

    @Test
    public void testDeaths() {
        MockServices ms = new MockServicesImpl();
        LocalDate today = LocalDate.now();
        final List<SsiApplication> allApps = CsvUtils.generateMockData(200);
        int num = 0;
        for (SsiApplication app : allApps) {
            int perHousehold = 0;
            List<SsiApplication> householdApps =
                    new ArrayList<>();
            for (SsiApplication innerApp : allApps) {
                if (innerApp.getHouseholdPrincipal().getAfm().equals(app.getHouseholdPrincipal().getAfm())) {
                    householdApps.add(innerApp);
                }
            }
            Optional<BooleanMockResult> res = ms.getDeaths(app.getTime(),today,0.066666667,app, true, householdApps);
            if (res.isPresent()){
                System.out.println(res.get().getData());
                num++;
                perHousehold++;
            }
            System.out.println("per household " + perHousehold);
        }
        System.out.println("UPDATED:: " + num);
    }

    @Test
    public void testOAED() {
        MockServices ms = new MockServicesImpl();
        LocalDate today = LocalDate.now();
        final List<SsiApplication> allApps = CsvUtils.generateMockData(200);
        int num = 0;
        for (SsiApplication app : allApps) {
            List<SsiApplication> householdApps =
                    new ArrayList<>();
            for (SsiApplication innerApp : allApps) {
                if (innerApp.getHouseholdPrincipal().getAfm().equals(app.getHouseholdPrincipal().getAfm())) {
                    householdApps.add(innerApp);
                }
            }
            Optional<BooleanMockResult> res = ms.getOAEDRegistration(app.getTime(),today,0.066666667,app, true, householdApps);
            if (res.isPresent() && !res.isEmpty()){
                System.out.println(res.get().getData());
                num++;
            }
        }
        System.out.println("UPDATED:: " + num);
    }


}
