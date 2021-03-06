package com.example.ethereumserviceapp;

import static org.mockito.ArgumentMatchers.anySet;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.State;
import com.example.ethereumserviceapp.model.entities.SsiApplication;
import com.example.ethereumserviceapp.service.MongoService;
import com.example.ethereumserviceapp.utils.DateUtils;
import com.example.ethereumserviceapp.utils.MonitorUtils;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class TestMonitorUtils extends TestUtils {

    @Mock
    MongoService mongoServ;

    @Test
    public void testOffsetPayment(){

        Case monitoredCase = generateMockCase("2WiYi1", State.ACCEPTED, false, false, "");
        //monitoredCase.setOffset(BigDecimal.valueOf(20));
        SsiApplication ssiApp1 = generateSsiAppAltered1();
        SsiApplication ssiApp2 = generateSsiAppAltered2();
        SsiApplication ssiApp3 = generateSsiAppAltered3();
        SsiApplication ssiApp4 = generateSsiAppAltered4();
        SsiApplication ssiApp5 = generateSsiAppAltered5();

        List<SsiApplication> ssiApps = new ArrayList<>();
        ssiApps.add(ssiApp1);
        ssiApps.add(ssiApp2);
        ssiApps.add(ssiApp3);
        ssiApps.add(ssiApp4);
        ssiApps.add(ssiApp5);

        MonitorUtils.calculateOffset(monitoredCase, ssiApp1, ssiApps);

        log.info("xxxxxxxxxxxxxxxxxxx offset :{}", monitoredCase.getOffset());
    }

    @Test
    public void testOffsetPaymentNoAlterations(){

        Case monitoredCase = generateMockCase("2WiYi1", State.ACCEPTED, false, false, "");
        SsiApplication ssiApp1 = generateSsiApp1();
        SsiApplication ssiApp2 = generateSsiApp2();
        SsiApplication ssiApp3 = generateSsiApp3();

        List<SsiApplication> ssiApps = new ArrayList<>();
        ssiApps.add(ssiApp1);
        ssiApps.add(ssiApp2);
        ssiApps.add(ssiApp3);

        MonitorUtils.calculateOffset(monitoredCase, ssiApp1, ssiApps);

        log.info("xxxxxxxxxxxxxxxxxxx offset :{}", monitoredCase.getOffset());
    }

    @Test
    public void testCalcualtePaymentWithoutOffset(){

        Case monitoredCase = generateMockCase("2WiYi1", State.ACCEPTED, false, false, "");
        SsiApplication ssiApp1 = generateSsiAppAltered1();
        SsiApplication ssiApp2 = generateSsiAppAltered2();
        SsiApplication ssiApp3 = generateSsiAppAltered3();
        SsiApplication ssiApp4 = generateSsiAppAltered4();
        SsiApplication ssiApp5 = generateSsiAppAltered5();

        List<SsiApplication> ssiApps = new ArrayList<>();
        ssiApps.add(ssiApp1);
        ssiApps.add(ssiApp2);
        ssiApps.add(ssiApp3);
        ssiApps.add(ssiApp4);
        ssiApps.add(ssiApp5);

        BigDecimal payment = MonitorUtils.calculateCurrentPayment(monitoredCase, ssiApp1, ssiApps, LocalDate.now(), false);

        log.info("xxxxxxxxxxxxxxxxxxx payment :{}", payment);

    }

    @Test
    public void testCalcualtePaymentRejectedWithoutOffset(){

        Case monitoredCase = generateMockCase("2WiYi1", State.REJECTED, false, false, "");
        SsiApplication ssiApp1 = generateSsiAppAltered1();
        SsiApplication ssiApp2 = generateSsiAppAltered2();
        SsiApplication ssiApp3 = generateSsiAppAltered3();
        SsiApplication ssiApp4 = generateSsiAppAltered4();
        SsiApplication ssiApp5 = generateSsiAppAltered5();

        List<SsiApplication> ssiApps = new ArrayList<>();
        ssiApps.add(ssiApp1);
        ssiApps.add(ssiApp2);
        ssiApps.add(ssiApp3);
        ssiApps.add(ssiApp4);
        ssiApps.add(ssiApp5);

        BigDecimal payment = MonitorUtils.calculateCurrentPayment(monitoredCase, ssiApp1, ssiApps, LocalDate.now(), false);

        log.info("xxxxxxxxxxxxxxxxxxx payment :{}", payment);

    }

    @Test
    public void testCalcualtePaymentWithoutOffsetNoAlterations(){

        Case monitoredCase = generateMockCase("2WiYi1", State.ACCEPTED, false, false, "");
        SsiApplication ssiApp1 = generateSsiApp1();
        SsiApplication ssiApp2 = generateSsiApp2();
        SsiApplication ssiApp3 = generateSsiApp3();

        List<SsiApplication> ssiApps = new ArrayList<>();
        ssiApps.add(ssiApp1);
        ssiApps.add(ssiApp2);
        ssiApps.add(ssiApp3);

        BigDecimal payment = MonitorUtils.calculateCurrentPayment(monitoredCase, ssiApp1, ssiApps, LocalDate.now(), false);

        log.info("xxxxxxxxxxxxxxxxxxx payment :{}", payment);
        //log.info("ssiApp after :{}", ssiApp);

    }

    @Test
    public void testCalcualtePaymentWithoutOffsetNoAlterationsAsyncRejected(){

        Case monitoredCase = generateMockCase("2WiYi1", State.ACCEPTED, false, false, DateUtils.dateToString(LocalDate.now().minusMonths(3).withDayOfMonth(15)));
        SsiApplication ssiApp1 = generateSsiApp1();
        SsiApplication ssiApp2 = generateSsiApp2();
        SsiApplication ssiApp3 = generateSsiApp3();

        List<SsiApplication> ssiApps = new ArrayList<>();
        ssiApps.add(ssiApp1);
        ssiApps.add(ssiApp2);
        ssiApps.add(ssiApp3);

        BigDecimal payment = MonitorUtils.calculateCurrentPayment(monitoredCase, ssiApp1, ssiApps, LocalDate.now(), false);

        log.info("xxxxxxxxxxxxxxxxxxx payment :{}", payment);
        //log.info("ssiApp after :{}", ssiApp);

    }

    @Test
    public void testOffsetPaymentAsyncRejected(){

        Case monitoredCase = generateMockCase("2WiYi1", State.ACCEPTED, false, false, DateUtils.dateToString(LocalDate.now().minusMonths(3).withDayOfMonth(15)));
        SsiApplication ssiApp1 = generateSsiAppAltered1();
        SsiApplication ssiApp2 = generateSsiAppAltered2();
        SsiApplication ssiApp3 = generateSsiAppAltered3();
        SsiApplication ssiApp4 = generateSsiAppAltered4();
        SsiApplication ssiApp5 = generateSsiAppAltered5();

        List<SsiApplication> ssiApps = new ArrayList<>();
        ssiApps.add(ssiApp1);
        ssiApps.add(ssiApp2);
        ssiApps.add(ssiApp3);
        ssiApps.add(ssiApp4);
        ssiApps.add(ssiApp5);

        MonitorUtils.calculateOffset(monitoredCase, ssiApp1, ssiApps);

        log.info("xxxxxxxxxxxxxxxxxxx offset :{}", monitoredCase.getOffset());
    }

    @Test
    public void testCalcualtePaymentWithoutPriorOffsetAsyncRejected(){

        Case monitoredCase = generateMockCase("2WiYi1", State.ACCEPTED, false, false, DateUtils.dateToString(LocalDate.now().minusMonths(1).withDayOfMonth(15)));
        SsiApplication ssiApp1 = generateSsiAppAltered1();
        SsiApplication ssiApp2 = generateSsiAppAltered2();
        SsiApplication ssiApp3 = generateSsiAppAltered3();
        SsiApplication ssiApp4 = generateSsiAppAltered4();
        SsiApplication ssiApp5 = generateSsiAppAltered5();

        List<SsiApplication> ssiApps = new ArrayList<>();
        ssiApps.add(ssiApp1);
        ssiApps.add(ssiApp2);
        ssiApps.add(ssiApp3);
        ssiApps.add(ssiApp4);
        ssiApps.add(ssiApp5);

        BigDecimal payment = MonitorUtils.calculateCurrentPayment(monitoredCase, ssiApp1, ssiApps, LocalDate.now(), false);

        log.info("xxxxxxxxxxxxxxxxxxx payment :{}", payment);

    }
}
