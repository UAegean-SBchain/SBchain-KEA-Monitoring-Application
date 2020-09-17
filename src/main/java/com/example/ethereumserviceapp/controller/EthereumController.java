package com.example.ethereumserviceapp.controller;

import com.example.ethereumserviceapp.model.CredsAndExp;
import com.example.ethereumserviceapp.service.ContractService;
import com.example.ethereumserviceapp.service.MongoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class EthereumController {

    @Autowired
    ContractService contractService;

    @Autowired
    MongoService mongoServ;

    @PostMapping("/addCase")
    protected void addCase(@RequestParam(value = "uuid", required = true) String uuid, @RequestParam(value = "caseName", required = true) String caseName,
            @RequestParam(value = "isStudent", required = true) Boolean isStudent,
            @RequestParam(value = "date", required = true) String date) {
        contractService.addCase(uuid, caseName, isStudent, date);
    }

    @PostMapping("/updateCase")
    protected void updateCase(@RequestParam(value = "uuid", required = true) String uuid, @RequestParam(value = "caseName", required = true) String caseName,
            @RequestParam(value = "isStudent", required = true) Boolean isStudent,
            @RequestParam(value = "date", required = true) String date,
            @RequestParam(value = "state", required = true) int state) {

        contractService.updateCase(uuid, caseName, isStudent, date, state);
    }

    @PostMapping("/deployContract")
    protected void deployContract() {
        contractService.deployContract();
    }

    @GetMapping("/getAllCases")
    protected void getAllCases() {
        contractService.getAllCases();
    }

    @GetMapping("/getCreds")
    public @ResponseBody
    CredsAndExp[] getAllCredentialIds(@RequestParam(value = "uuid", required = true) String uuid) {

        return mongoServ.findCredentialIdsByUuid(uuid);

    }

}
