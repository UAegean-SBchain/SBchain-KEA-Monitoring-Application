package com.example.ethereumserviceapp.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.CredsAndExp;
import com.example.ethereumserviceapp.model.State;
import com.example.ethereumserviceapp.service.ContractService;
import com.example.ethereumserviceapp.service.EthereumService;
import com.example.ethereumserviceapp.service.MongoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/")
public class EthereumController {

    @Autowired
    ContractService contractService;

    @Autowired
    EthereumService ethService;
    MongoService mongoServ;

    @PostMapping("/addCase")
    protected void addCase(@RequestParam(value = "uuid", required = true) String uuid, @RequestParam(value = "caseName", required = true) String caseName,
            @RequestParam(value = "isStudent", required = true) Boolean isStudent,
            @RequestParam(value = "date", required = true) String date) {

        Case monitoredCase = new Case();
        monitoredCase.setUuid(uuid);
        monitoredCase.setName(caseName);
        monitoredCase.setIsStudent(isStudent);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        monitoredCase.setDate(LocalDateTime.parse(date, formatter));

        log.info("add Case ?!?!?");
        ethService.addCase(monitoredCase);
                
        //contractService.addCase(uuid, caseName, isStudent, date);
    }

    @PostMapping("/updateCase")
    protected void updateCase(@RequestParam(value = "uuid", required = true) String uuid, @RequestParam(value = "caseName", required = true) String caseName,
            @RequestParam(value = "isStudent", required = true) Boolean isStudent,
            @RequestParam(value = "date", required = true) String date,
            @RequestParam(value = "state", required = true) String state) {

        Case monitoredCase = new Case();
        monitoredCase.setUuid(uuid);
        monitoredCase.setName(caseName);
        monitoredCase.setIsStudent(isStudent);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        //LocalDateTime dateTime = LocalDateTime.parse(str, formatter);

        monitoredCase.setDate(LocalDateTime.parse(date, formatter));
        monitoredCase.setState(State.valueOf(state));

        ethService.updateCase(monitoredCase);

        //contractService.updateCase(uuid, caseName, isStudent, date, state);
    }

    @PostMapping("/deployContract")
    protected void deployContract() {
        contractService.deployContract();
    }

    @GetMapping("/getAllCases")
    protected String getAllCases() {
        return ethService.getAllCaseUUID().get(0).trim();
        //contractService.getAllCases();
    }

    @GetMapping("/getCase")
    protected Case getCase(@RequestParam(value = "uuid", required = true) String uuid) {
        //Optional<Case> theCase = ethService.getCaseByUUID(uuid);
        return ethService.getCaseByUUID(uuid).get();
    }

    @GetMapping("/getCreds")
    public @ResponseBody
    CredsAndExp[] getAllCredentialIds(@RequestParam(value = "uuid", required = true) String uuid) {

        return mongoServ.findCredentialIdsByUuid(uuid);

    }

}
