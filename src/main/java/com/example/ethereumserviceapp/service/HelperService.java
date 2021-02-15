package com.example.ethereumserviceapp.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.CaseAppDTO;
import com.example.ethereumserviceapp.model.State;
import com.example.ethereumserviceapp.model.entities.SsiApplication;
import com.example.ethereumserviceapp.utils.ExportCaseToExcel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HelperService {

    @Autowired
    private EthereumService ethService;

    @Autowired
    private MongoService mongoService;

    @Autowired
    private MonitorService monitorService;

    @Autowired
    private PaymentService paymentService;

    public void addTestCase(String uuid) {
        Optional<SsiApplication> ssiApp = mongoService.findByUuid(uuid);
        if (ssiApp.isPresent()) {
            Case caseToAdd = new Case();
            caseToAdd.setDate(LocalDateTime.of(ssiApp.get().getTime(), LocalTime.of(00, 00, 00)));
            caseToAdd.setUuid(uuid);
            ethService.addCase(caseToAdd);
        }
    }

    public void runMonitoring(String startDate, String numDays, double pValue) {

        String startDateFixed = startDate.replace("T", " ");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime currentDate = LocalDateTime.parse(startDateFixed, formatter);
        LocalDateTime endDate = currentDate.plusDays(Long.valueOf(numDays));
        List<CaseAppDTO> storedDataForSE = null;

        while (currentDate.compareTo(endDate) < 0) {

            if (currentDate.plusDays(1).compareTo(endDate) == 0) {
                storedDataForSE = new ArrayList<>();
            }
            if (currentDate.getDayOfMonth() == 1) {
                paymentService.startPayment(currentDate, false);
            }
            monitorService.startMonitoring(currentDate, false, pValue, true, storedDataForSE);
            
            currentDate = currentDate.plusDays(1);
        }

        // Object to JSON in file
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

            String jsonCases = objectMapper.writeValueAsString(storedDataForSE);
            JsonNode tree = objectMapper.readTree(jsonCases);
            String formattedJson = objectMapper.writeValueAsString(tree);
                    
            FileOutputStream outputStream = new FileOutputStream("caseApps.json");
            byte[] strToBytes = formattedJson.getBytes();
            outputStream.write(strToBytes);

            outputStream.close();
            //jsonCases.writeValue(new File("caseApps.json"), storedDataForSE);
        } catch (IOException e) {
            log.error("object to json error :{}", e.getMessage());
        }

        if(storedDataForSE != null && !storedDataForSE.isEmpty()){
            List<CaseAppDTO> acceptedCases = storedDataForSE.stream().filter(c -> c.getPrincipalCase().getState().equals(State.ACCEPTED)).collect(Collectors.toList());
            List<CaseAppDTO> rejectedCases = storedDataForSE.stream().filter(c -> c.getPrincipalCase().getState().equals(State.REJECTED)).collect(Collectors.toList());
            if(!acceptedCases.isEmpty() && acceptedCases.size()>0){
                Random rand = new Random(); 
                int randAccepted = acceptedCases.size() == 1? 0 : rand.nextInt(acceptedCases.size()-1);
                
                ExportCaseToExcel excelExporter = new ExportCaseToExcel(acceptedCases.get(randAccepted).getPrincipalCase(), acceptedCases.get(randAccepted).getHouseholdApps());
                try {
                    excelExporter.export(false, "AcceptedCaseSample.xlsx");
                } catch (IOException e1) {
                    log.error("export to excel error :{}", e1.getMessage());
                }
            }

            if(!rejectedCases.isEmpty() && rejectedCases.size()>0){
                Random rand = new Random(); 
                int randRejected = rejectedCases.size() == 1? 0 :rand.nextInt(rejectedCases.size()-1);
                
                ExportCaseToExcel excelExporter = new ExportCaseToExcel(rejectedCases.get(randRejected).getPrincipalCase(), rejectedCases.get(randRejected).getHouseholdApps());
                try {
                    excelExporter.export(false, "RejectedCaseSample.xlsx");
                } catch (IOException e1) {
                    log.error("export to excel error :{}", e1.getMessage());
                }
            }
        }
    }
}
