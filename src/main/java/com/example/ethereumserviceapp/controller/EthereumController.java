package com.example.ethereumserviceapp.controller;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.State;
import com.example.ethereumserviceapp.model.entities.SsiApplication;
import com.example.ethereumserviceapp.service.EthereumService;
import com.example.ethereumserviceapp.service.MongoService;
import com.example.ethereumserviceapp.service.MonitorService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/")
public class EthereumController {

    @Autowired
    EthereumService ethService;

    @Autowired
    MongoService mongoServ;

    @Autowired
    MonitorService monitorService;

    @PostMapping("/addCase")
    protected void addCase(@RequestParam(value = "uuid", required = true) String uuid,
            @RequestParam(value = "date", required = true) String date, @RequestParam(value = "householdId", required = true) String householdId) {

        Case monitoredCase = new Case();
        monitoredCase.setUuid(uuid);
        monitoredCase.setHouseholdId(householdId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        monitoredCase.setDate(LocalDateTime.parse(date, formatter));

        log.info("add Case ?!?!?");
        ethService.addCase(monitoredCase);

    }

    @PostMapping("/update")
    public @ResponseBody
    String updateCase(@RequestParam(value = "uuid") String uuid) {

        Optional<Case> c = this.ethService.getCaseByUUID(uuid);
        if (c.isPresent()) {
            c.get().setState(State.ACCEPTED);
            this.ethService.updateCase(c.get()/*, false*/);
            return "OK";
        }
        return "FAIL";
    }

    @PostMapping("/validate-update")
    public @ResponseBody
    String validateAndupdateCase(@RequestParam(value = "uuid") String uuid) {

        Optional<Case> c = this.ethService.getCaseByUUID(uuid);
//        if (c.isPresent()) {
            Optional<SsiApplication> ssiApp = mongoServ.findByUuid(uuid);
            if (!ssiApp.isPresent()) {
                return "FAIL";
            }
            c.get().setState(State.ACCEPTED);
            this.ethService.updateCase(c.get()/*, false*/);
            return "OK";
//        }
//        return "FAIL";
    }

    @GetMapping("/getContractState")
    protected String getContractState(@RequestParam(value = "uuid") String uuid){

        Optional<Case> c = ethService.getCaseByUUID(uuid);
        if (c.isPresent()) {
            return c.get().getState().toString();
        }

        return "FAIL";
    }

}
