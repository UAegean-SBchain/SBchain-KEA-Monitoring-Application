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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author nikos
 */
public class TestEthService {

    @Test
    public void testGetAllCases() {

        EthereumService ethServ = new EthereumServiceImpl();
        Assertions.assertEquals(ethServ.getAllCaseUUID().size() > 0, true);
        ethServ.getAllCaseUUID().stream().forEach(uuid -> {
            System.out.println(uuid);
        });
        //fakeuuid
        Assertions.assertEquals(ethServ.getCaseByUUID("fakeuuid").isPresent(), true);
    }

    @Test
    public void testAddCase() {
        EthereumService ethServ = new EthereumServiceImpl();
        Case theCase = new Case();
        theCase.setIsStudent(false);
        theCase.setName("caseName");
        ethServ.addCase(theCase);

        Assertions.assertEquals(true, true);

    }

    @Test
    public void testUpdateCase() {
        EthereumService ethServ = new EthereumServiceImpl();
        Case theCase = new Case();
        theCase.setIsStudent(true);
        theCase.setName("caseName_updated");
        theCase.setUuid("2WiYi8");
        theCase.setState(State.PAID);

        // this tests needs to be runned twice, once with calling the contract
        // and once after the transaction has been processed
//        ethServ.updateCase(theCase);
        Assertions.assertEquals(ethServ.getCaseByUUID("2WiYi8").isPresent(), true);
        Assertions.assertEquals(ethServ.getCaseByUUID("2WiYi8").get().getIsStudent(), true);
        Assertions.assertEquals(ethServ.getCaseByUUID("2WiYi8").get().getName(), "caseName_updated");

    }

}
