package com.example.ethereumserviceapp.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.example.ethereumserviceapp.model.GeneratePopFormBind;
import com.example.ethereumserviceapp.model.MonitorCmdHelper;
import com.example.ethereumserviceapp.model.Notifications;
import com.example.ethereumserviceapp.model.entities.SsiApplication;
import com.example.ethereumserviceapp.repository.SsiApplicationRepository;
import com.example.ethereumserviceapp.service.HelperService;
import com.example.ethereumserviceapp.utils.CsvUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.core.MessagePostProcessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SocialEconomyController {

    @Autowired
    SsiApplicationRepository ssiRepo;

    @Autowired
    HelperService helperService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    @GetMapping("/")
    public ModelAndView getHome(@ModelAttribute MonitorCmdHelper monitorCmdHelper) {
        return new ModelAndView("economy", "monitorCmdHelper", monitorCmdHelper);
    }

    @GetMapping("/generate")
    public ModelAndView generatePopulationGET(@ModelAttribute GeneratePopFormBind popForm) {
        return new ModelAndView("generate", "popForm", popForm);
    }

    @PostMapping("/generate")
    protected ResponseEntity<Resource> generatePopulationPOST(@ModelAttribute GeneratePopFormBind popForm, ModelMap model, HttpServletRequest request) {
        List<SsiApplication> household = CsvUtils.generateMockData(200, popForm.getCrossBorder(), popForm.getWomen(), popForm.getMarried(),
                popForm.getParents(), popForm.getUnderAge(), popForm.getEmployed());
        try {
            File pop = CsvUtils.writeToCSV(household, popForm.getCrossBorder(), popForm.getWomen(), popForm.getMarried(),
                    popForm.getParents(), popForm.getUnderAge(), popForm.getEmployed());

            InputStreamResource resource = new InputStreamResource(new FileInputStream(pop));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + pop.getName());
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(pop.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }

    @GetMapping("/uploadCSV")
    protected ModelAndView uploadCSVGET(@ModelAttribute GeneratePopFormBind popForm, ModelMap model, HttpServletRequest request, RedirectAttributes redirectAttrs) {

        if(redirectAttrs.getAttribute("finished")!=null){
            model.addAttribute(redirectAttrs.getAttribute("finished"));
            log.info("finished was found as a redirect param");
        }
        return new ModelAndView("uploadCSV", "popForm", popForm);
    }

    @PostMapping("/uploadCSV")
    protected RedirectView uploadCSVPOST(@RequestParam("file") MultipartFile file, RedirectAttributes redir) {
        String message = "";
        List<SsiApplication> ssiApplications = new ArrayList<>();
        try {
            ssiApplications = CsvUtils.csvToSsiApplication(file.getInputStream());
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            log.info(message);
            log.error(e.getMessage());
        }
        if (!ssiApplications.isEmpty()) {
            ssiRepo.saveAll(ssiApplications);

            for (SsiApplication ssiApp : ssiApplications) {
                helperService.addTestCase(ssiApp.getUuid());
            }
        }

        RedirectView redirectView = new RedirectView("/uploadCSV", true);
        redir.addFlashAttribute("finished", true);
        return redirectView;
    }

    @GetMapping("/simulate")
    protected ModelAndView simulate(@ModelAttribute MonitorCmdHelper monitorCmdHelper, ModelMap model,
                                    HttpServletRequest request, RedirectAttributes redirectAttrs) {
        simpMessagingTemplate.convertAndSend("/topic/app/", new Notifications("this is a testttt"));

        return new ModelAndView("simulate", "monitorCmdHelper", monitorCmdHelper);
    }

    @PostMapping("/startSimulation")
    protected @ResponseBody String simulatePost(@ModelAttribute MonitorCmdHelper monitorCmdHelper, ModelMap model,
                        HttpServletRequest request, RedirectAttributes redirectAttrs) {
        simpMessagingTemplate.convertAndSend("/topic/app",
                "started", new ModelMap(), new MessagePostProcessor() {
                    @Override
                    public Message<?> postProcessMessage(Message<?> message) {
                        log.info(message.toString());
                        return message;
                    }
                });
//        log.info("xxxxxxxxxxxxxxxxx start monitoring :{}", LocalDateTime.now());
        helperService.runMonitoring(monitorCmdHelper.getStartDate(), monitorCmdHelper.getNumDays(),
                Double.valueOf(monitorCmdHelper.getPValue()));

        simpMessagingTemplate.convertAndSend("/topic/app",
                "finished", new ModelMap(), new MessagePostProcessor() {
                    @Override
                    public Message<?> postProcessMessage(Message<?> message) {
                        log.info(message.toString());
                        return message;
                    }
                });
        return "OK";
    }

    @GetMapping("/getJsonReport")
    protected ResponseEntity<Resource> getJsonReport() {
        try {
            File pop = new File("caseApps.json");
            InputStreamResource resource = new InputStreamResource(new FileInputStream(pop));
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + pop.getName());
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(pop.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    @GetMapping("/getXLSReport")
    protected ResponseEntity<Resource> getXLSReport() {
        try {
            File pop = new File("AcceptedCaseSample.xlsx");
            InputStreamResource resource = new InputStreamResource(new FileInputStream(pop));
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + pop.getName());
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(pop.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }



}
