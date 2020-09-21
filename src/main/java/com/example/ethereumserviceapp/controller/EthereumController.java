package com.example.ethereumserviceapp.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.service.EthereumService;
import com.example.ethereumserviceapp.service.MongoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/")
public class EthereumController {

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
                
    }

}
