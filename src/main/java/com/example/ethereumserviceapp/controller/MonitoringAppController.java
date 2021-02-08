package com.example.ethereumserviceapp.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.MonitorCmdHelper;
import com.example.ethereumserviceapp.model.entities.SsiApplication;
import com.example.ethereumserviceapp.repository.SsiApplicationRepository;
import com.example.ethereumserviceapp.service.EthereumService;
import com.example.ethereumserviceapp.service.HelperService;
import com.example.ethereumserviceapp.service.MongoService;
import com.example.ethereumserviceapp.utils.CsvUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MonitoringAppController {

    @Autowired
    private EthereumService ethService;

    @Autowired
    private MongoService mongoServ;

    @Autowired
    private SsiApplicationRepository ssiAppRepo;

    @Autowired
    private HelperService helpService;

    @GetMapping("/listCaseUuids")
    protected ModelAndView listCaseUuids(@ModelAttribute MonitorCmdHelper monitorCmdHelper, ModelMap model, HttpServletRequest request){

        List<String> ethUuids = ethService.getAllCaseUUID();
        model.addAttribute("ethUuids", ethUuids);

        return new ModelAndView("showCases", "monitorCmdHelper", monitorCmdHelper);
        
    }

    @GetMapping("/getCase")
    protected ModelAndView getCase(@RequestParam(value = "uuid", required = true) String uuid, ModelMap model, HttpServletRequest request){

        Optional<Case> ethCase = ethService.getCaseByUUID(uuid);
        log.info("111111111111111111111 case :{}", ethCase);
        model.addAttribute("ethCase", ethCase.isPresent()? ethCase.get() : "");

        Optional<SsiApplication> ssiApp = mongoServ.findByUuid(uuid);
        model.addAttribute("ssiApp", ssiApp.isPresent()? ssiApp.get() : "");
        
        return new ModelAndView("showCase");
    }

    @GetMapping("/prepareAddTestData")
    protected ModelAndView submitCsv(ModelMap model, HttpServletRequest request){
        return new ModelAndView("submitCsv");
    }

    @PostMapping("/addTestData")
    protected ModelAndView addTestData(@RequestParam("file") MultipartFile file) {
        String message = "";
        List<SsiApplication> ssiApplications = new ArrayList<>();
        
        //if (CsvUtils.hasCSVFormat(file)) {
            try {
                ssiApplications = CsvUtils.csvToSsiApplication(file.getInputStream());
            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                log.info(message);
                log.error(e.getMessage());
            }
        //}

        if(!ssiApplications.isEmpty()){
            ssiAppRepo.saveAll(ssiApplications);

            for(SsiApplication ssiApp:ssiApplications){
                helpService.addTestCase(ssiApp.getUuid());
            }
        }

        return new ModelAndView("redirect:/listCaseUuids");
    }

    @PostMapping("/monitorCases")
    protected ModelAndView runMonitoringOnCase(@ModelAttribute MonitorCmdHelper monitorCmdHelper, ModelMap model, HttpServletRequest request){
        helpService.runMonitoring(monitorCmdHelper.getStartDate(), monitorCmdHelper.getNumDays(), Double.valueOf(monitorCmdHelper.getPValue()));
        model.addAttribute("monitorCmdHelper", monitorCmdHelper);

        return listCaseUuids(monitorCmdHelper, model, request);
    }
}
