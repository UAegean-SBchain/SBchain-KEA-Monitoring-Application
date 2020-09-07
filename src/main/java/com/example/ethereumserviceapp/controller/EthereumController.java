package com.example.ethereumserviceapp.controller;

import com.example.ethereumserviceapp.service.ContractService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/")
public class EthereumController {

    @Autowired
    ContractService contractService;

    @PostMapping("/addCase")
    protected void addCase(){
        contractService.addCaseToBChain();
    }

    @GetMapping("/getBlockNumber")
    protected void getBlockNumber(){
        contractService.getBlockNumber();
    }

    @GetMapping("/loadContract")
    protected void loadContract(){
        contractService.testLoadContract();
    }

    
    
}