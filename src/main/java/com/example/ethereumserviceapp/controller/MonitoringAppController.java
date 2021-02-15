package com.example.ethereumserviceapp.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.MonitorCmdHelper;
import com.example.ethereumserviceapp.model.entities.SsiApplication;
import com.example.ethereumserviceapp.repository.SsiApplicationRepository;
import com.example.ethereumserviceapp.service.EthereumService;
import com.example.ethereumserviceapp.service.HelperService;
import com.example.ethereumserviceapp.service.MongoService;
import com.example.ethereumserviceapp.utils.CsvUtils;
import com.example.ethereumserviceapp.utils.ExportCaseToExcel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    protected ModelAndView listCaseUuids(@ModelAttribute MonitorCmdHelper monitorCmdHelper, ModelMap model, HttpServletRequest request, @RequestParam("page") Optional<Integer> page, 
         @RequestParam("size") Optional<Integer> size) {
      int currentPage = page.orElse(1);
      int pageSize = size.orElse(20);

      Page<String> uuidPage = ethService.getCaseUuidsPaginated(PageRequest.of(currentPage - 1, pageSize));

      model.addAttribute("uuidPage", uuidPage);

      int totalPages = uuidPage.getTotalPages();
      if (totalPages > 0) {
          List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
              .boxed()
              .collect(Collectors.toList());
          model.addAttribute("pageNumbers", pageNumbers);
      }

      return new ModelAndView("showCases");

        // List<String> ethUuids = ethService.getAllCaseUUID();
        // model.addAttribute("ethUuids", ethUuids);

        // return new ModelAndView("showCases", "monitorCmdHelper", monitorCmdHelper);
        
    }

    @GetMapping("/getCase")
    protected ModelAndView getCase(@RequestParam(value = "uuid", required = true) String uuid, ModelMap model, HttpServletRequest request){

        Optional<Case> ethCase = ethService.getCaseByUUID(uuid);
        model.addAttribute("ethCase", ethCase.isPresent()? ethCase.get() : "");
        
        return new ModelAndView("showCase");
    }

    @GetMapping("/getApplication")
    protected ModelAndView getApplication(@RequestParam(value = "uuid", required = true) String uuid, ModelMap model, HttpServletRequest request){

        Optional<Case> ethCase = ethService.getCaseByUUID(uuid);
        model.addAttribute("ethCase", ethCase.isPresent()? ethCase.get() : "");

        Optional<SsiApplication> ssiApp = mongoServ.findByUuid(uuid);
        model.addAttribute("ssiApp", ssiApp.isPresent()? ssiApp.get() : "");
        
        return new ModelAndView("showApplication");
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

    @GetMapping("/prepareMonitorCases")
    protected ModelAndView prepareRunMonitoring(@ModelAttribute MonitorCmdHelper monitorCmdHelper, ModelMap model, HttpServletRequest request){
        return new ModelAndView("monitoringRun");
    }

    @PostMapping("/monitorCases")
    protected ModelAndView runMonitoringOnCase(@ModelAttribute MonitorCmdHelper monitorCmdHelper, ModelMap model, HttpServletRequest request){
        log.info("xxxxxxxxxxxxxxxxx start monitoring :{}", LocalDateTime.now());
        helpService.runMonitoring(monitorCmdHelper.getStartDate(), monitorCmdHelper.getNumDays(), Double.valueOf(monitorCmdHelper.getPValue()));
        log.info("yyyyyyyyyyyyyyyyy end monitoring :{}", LocalDateTime.now());
        model.addAttribute("monitorCmdHelper", monitorCmdHelper);

        return new ModelAndView("redirect:/listCaseUuids");
    }
}
