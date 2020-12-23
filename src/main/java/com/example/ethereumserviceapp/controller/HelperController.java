package com.example.ethereumserviceapp.controller;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.State;
import com.example.ethereumserviceapp.model.entities.SsiApplication;
import com.example.ethereumserviceapp.repository.SsiApplicationRepository;
import com.example.ethereumserviceapp.service.EthereumService;
import com.example.ethereumserviceapp.utils.CsvUtils;
import com.example.ethereumserviceapp.utils.VisualizationHelperUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class HelperController {

    @Autowired
    SsiApplicationRepository ssiAppRepo;

    @Autowired
    EthereumService ethService;

    //used for generating mock applications
    @PostMapping("/addApplications")
    protected void addApplication(){
        ssiAppRepo.save(VisualizationHelperUtils.generateSsiAppAltered1());
        ssiAppRepo.save(VisualizationHelperUtils.generateSsiAppAltered2());
        ssiAppRepo.save(VisualizationHelperUtils.generateSsiAppAltered3());
        ssiAppRepo.save(VisualizationHelperUtils.generateSsiAppAltered4());
        ssiAppRepo.save(VisualizationHelperUtils.generateSsiAppAltered5());
    }

    @GetMapping("/findApplications")
    protected List<SsiApplication> findAllApplications(){
        List<SsiApplication> apps = ssiAppRepo.findAll();

        return apps;
    }

    @PostMapping("/updateCaseMock")
    protected void updateCase(){
        Optional<Case> ethCase = ethService.getCaseByUUID("1SiYd6");
        LocalDateTime currDate = ethCase.get().getDate();
        for(int i=1; i<=40; i++){
            Case mCase = ethCase.get();
            mCase.setDate(currDate.plusDays(i));
            mCase.setState(State.ACCEPTED);
            ethService.updateCase(mCase, true);
        }
        
    }
    
}
