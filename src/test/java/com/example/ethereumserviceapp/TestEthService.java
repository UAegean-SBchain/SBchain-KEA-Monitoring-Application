/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.State;
import com.example.ethereumserviceapp.service.EthereumService;
import com.example.ethereumserviceapp.service.impl.EthereumServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author nikos
 */
@Slf4j
public class TestEthService {

    @Test
    public void testGetAllCases() {

        EthereumService ethServ = new EthereumServiceImpl();
        ethServ.getAllCaseUUID().stream().forEach(uuid -> {
            System.out.println("****************************the uuis is :");
            System.out.println(uuid);
            System.out.println("****************************");
            System.out.println("********************the case stat is:");
//            System.out.println(ethServ.getCaseByUUID(uuid).get().getState().getValue());
        });
        //fakeuuid
        Assertions.assertEquals(ethServ.getCaseByUUID("fakeuuid").isPresent(), false);
    }

    @Test
    public void testAddCaseDelete() {
        EthereumService ethServ = new EthereumServiceImpl();
        Case theCase = new Case();
        theCase.setUuid("1WEC5TOI63P0AFN1");
        log.info("will try to add");
        ethServ.addCase(theCase);
//        Assertions.assertEquals(ethServ.getCaseByUUID("1WEC5TOI63P0AFN1").isPresent(), true);
//        ethServ.deleteCaseByUuid("1WEC5TOI63P0AFN1");

    }

    @Test
    public void testUpdateCase() {
        EthereumService ethServ = new EthereumServiceImpl();
        Case theCase = new Case();
        theCase.setUuid("2WiYi8");
        theCase.setState(State.PAID);

        // this tests needs to be runned twice, once with calling the contract
        // and once after the transaction has been processed
//        ethServ.updateCase(theCase);
//        Assertions.assertEquals(ethServ.getCaseByUUID("2WiYi8").isPresent(), true);

    }

    @Test
    public void testGetCase() {
        EthereumService ethServ = new EthereumServiceImpl();
        final String uuid = "1SiYd2";

//        Assertions.assertEquals(ethServ.getCaseByUUID(uuid).isPresent(), false);

    }

}
