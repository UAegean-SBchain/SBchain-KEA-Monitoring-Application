package com.example.ethereumserviceapp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.example.ethereumserviceapp.model.HouseholdMember;
import com.example.ethereumserviceapp.model.entities.SsiApplication;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CsvUtils {
    public static String TYPE = "text/csv";
    static String[] HEADERs = { "Id", "Title", "Description", "Published" };

    public static boolean hasCSVFormat(MultipartFile file) {

        if (!TYPE.equals(file.getContentType())) {
           return false;
        }

    return true;
    }

    // transforms csv to JPA entity
    public static List<SsiApplication> csvToSsiApplication(InputStream is) throws Exception{
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            CSVParser csvParser = new CSVParser(fileReader,
                CSVFormat.DEFAULT. withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

            List<SsiApplication> ssiAppList = new ArrayList<SsiApplication>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (CSVRecord csvRecord : csvRecords) {
                SsiApplication ssiApp = new SsiApplication();
                    ssiApp.setId(csvRecord.get("_id/$oid"));
                    ssiApp.setUuid(csvRecord.get("uuid"));
                    //ssiApp.setCredentialIds(transformCrendentialIds(csvRecord.get("credentialIds")));
                    ssiApp.setSsn(csvRecord.get("ssn"));
                    ssiApp.setTaxisAfm(csvRecord.get("taxisAfm"));
                    ssiApp.setTaxisFamilyName(csvRecord.get("taxisFamilyName"));
                    ssiApp.setTaxisFirstName(csvRecord.get("taxisFirstName"));
                    ssiApp.setTaxisFathersName(csvRecord.get("taxisFathersName"));
                    ssiApp.setTaxisMothersName(csvRecord.get("taxisMothersName"));
                    ssiApp.setTaxisFamilyNameLatin(csvRecord.get("taxisFamilyNameLatin"));
                    ssiApp.setTaxisFirstNameLatin(csvRecord.get("taxisFirstNameLatin"));
                    ssiApp.setTaxisFathersNameLatin(csvRecord.get("taxisFathersNameLatin"));
                    ssiApp.setTaxisMothersNameLatin(csvRecord.get("taxisMothersNameLatin"));
                    ssiApp.setTaxisAmka(csvRecord.get("taxisAmka"));
                    ssiApp.setTaxisDateOfBirth(csvRecord.get("taxisDateOfBirth"));
                    ssiApp.setTaxisGender(csvRecord.get("taxisGender"));
                    ssiApp.setNationality(csvRecord.get("nationality"));
                    ssiApp.setMaritalStatus(csvRecord.get("maritalStatus"));
                    ssiApp.setHospitalized(csvRecord.get("hospitalized"));
                    ssiApp.setHospitalizedSpecific(csvRecord.get("hospitalizedSpecific"));
                    ssiApp.setMonk(csvRecord.get("monk"));
                    ssiApp.setLuxury(csvRecord.get("luxury"));
                    ssiApp.setStreet(csvRecord.get("street"));
                    ssiApp.setStreetNumber(csvRecord.get("streetNumber"));
                    ssiApp.setPo(csvRecord.get("po"));
                    ssiApp.setMunicipality(csvRecord.get("municipality"));
                    ssiApp.setPrefecture(csvRecord.get("prefecture"));
                    ssiApp.setOwnership(csvRecord.get("ownership"));
                    ssiApp.setSupplyType(csvRecord.get("supplyType"));
                    ssiApp.setMeterNumber(csvRecord.get("meterNumber"));
                    ssiApp.setParticipateFead(csvRecord.get("participateFead"));
                    ssiApp.setSelectProvider(csvRecord.get("selectProvider"));
                    ssiApp.setGender(csvRecord.get("gender"));
                    ssiApp.setDisabilityStatus(csvRecord.get("disabilityStatus"));
                    ssiApp.setLevelOfEducation(csvRecord.get("levelOfEducation"));
                    ssiApp.setEmploymentStatus(csvRecord.get("employmentStatus"));
                    ssiApp.setUnemployed(csvRecord.get("unemployed"));
                    ssiApp.setEmployed(csvRecord.get("employed"));
                    ssiApp.setOaedId(csvRecord.get("oaedId"));
                    ssiApp.setOaedDate(csvRecord.get("oaedDate"));
                    ssiApp.setEmail(csvRecord.get("email"));
                    ssiApp.setMobilePhone(csvRecord.get("mobilePhone"));
                    ssiApp.setLandline(csvRecord.get("landline"));
                    ssiApp.setIban(csvRecord.get("iban"));
                    //ssiApp.setMailAddress(transformMailAddress(csvRecord.get("mailAddress")));
                    ssiApp.setParenthood(csvRecord.get("parenthood"));
                    ssiApp.setCustody(csvRecord.get("custody"));
                    ssiApp.setAdditionalAdults(csvRecord.get("additionalAdults"));
                    ssiApp.setSalariesR(csvRecord.get("salariesR"));
                    ssiApp.setPensionsR(csvRecord.get("pensionsR"));
                    ssiApp.setFarmingR(csvRecord.get("farmingR"));
                    ssiApp.setFreelanceR(csvRecord.get("freelanceR"));
                    ssiApp.setRentIncomeR(csvRecord.get("rentIncomeR"));
                    ssiApp.setUnemploymentBenefitR(csvRecord.get("unemploymentBenefitR"));
                    ssiApp.setOtherBenefitsR(csvRecord.get("otherBenefitsR"));
                    ssiApp.setEkasR(csvRecord.get("ekasR"));
                    ssiApp.setOtherIncomeR(csvRecord.get("otherIncomeR"));
                    ssiApp.setErgomeR(csvRecord.get("ergomeR"));
                    ssiApp.setDepositInterestA(csvRecord.get("depositInterestA"));
                    ssiApp.setDepositsA(csvRecord.get("depositsA"));
                    ssiApp.setDomesticRealEstateA(csvRecord.get("domesticRealEstateA"));
                    ssiApp.setForeignRealEstateA(csvRecord.get("foreignRealEstateA"));
                    ssiApp.setVehicleValueA(csvRecord.get("vehicleValueA"));
                    ssiApp.setInvestmentsA(csvRecord.get("investmentsA"));
                    ssiApp.setSalariesRHistory(transformHistoryField(csvRecord.get("salariesRHistory")));
                    ssiApp.setPensionsRHistory(transformHistoryField(csvRecord.get("pensionsRHistory")));
                    ssiApp.setFarmingRHistory(transformHistoryField(csvRecord.get("farmingRHistory")));
                    ssiApp.setFreelanceRHistory(transformHistoryField(csvRecord.get("freelanceRHistory")));
                    ssiApp.setOtherBenefitsRHistory(transformHistoryField(csvRecord.get("otherBenefitsRHistory")));
                    ssiApp.setDepositsAHistory(transformHistoryField(csvRecord.get("depositsAHistory")));
                    ssiApp.setDomesticRealEstateAHistory(transformHistoryField(csvRecord.get("domesticRealEstateAHistory")));
                    ssiApp.setForeignRealEstateAHistory(transformHistoryField(csvRecord.get("foreignRealEstateAHistory")));
                    ssiApp.setHouseholdPrincipal(transformHousehold(csvRecord.get("householdPrincipal")).get(0));
                    ssiApp.setHouseholdComposition(transformHousehold(csvRecord.get("householdComposition")));
                    ssiApp.setHouseholdCompositionHistory(transformHhHistory(csvRecord.get("householdCompositionHistory")));
                    ssiApp.setMonthlyGuarantee(csvRecord.get("monthlyGuarantee"));
                    ssiApp.setTotalIncome(csvRecord.get("totalIncome"));
                    ssiApp.setMonthlyIncome(csvRecord.get("monthlyIncome"));
                    ssiApp.setMonthlyAid(csvRecord.get("monthlyAid"));
                    ssiApp.setSavedInDb(Boolean.valueOf(csvRecord.get("savedInDb")));
                    ssiApp.setStatus(csvRecord.get("status"));
                    ssiApp.setSubmittedMunicipality(csvRecord.get("submittedMunicipality"));
                    ssiApp.setTime(LocalDate.parse(csvRecord.get("time"), formatter));
                    
                ssiAppList.add(ssiApp);
            }
            return ssiAppList;
        } catch (IOException e) {
            log.error("error on submit csv :{}", e.getMessage());
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

    private static List<HouseholdMember> transformHousehold(String householdComp){

        List<HouseholdMember> householdComposition = new ArrayList<>();
        String[] hhComp = householdComp.split("\\|");
        for(int i=0; i<hhComp.length; i++){
            String[] hhEntry = hhComp[i].split(";");
            HouseholdMember hhMember = new HouseholdMember();
            hhMember.setName(hhEntry[0]);
            hhMember.setSurname(hhEntry[1]);
            hhMember.setAfm(hhEntry[2]);
            hhMember.setDateOfBirth(hhEntry[3]);

            householdComposition.add(hhMember);
        }
        return householdComposition;
    }

    private static LinkedHashMap<LocalDateTime, List<HouseholdMember>> transformHhHistory(String history){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LinkedHashMap<LocalDateTime, List<HouseholdMember>> hhHistory = new LinkedHashMap<>();
        String[] hhs = history.split("-");
        for(int i=0; i<hhs.length; i++){
            String[] hh = hhs[i].split("_");
            List<HouseholdMember> householdEntry = transformHousehold(hh[1]);
            hhHistory.put(LocalDateTime.parse(hh[0], formatter), householdEntry);
        }
        return hhHistory;
    }

    private static LinkedHashMap<LocalDateTime, String> transformHistoryField(String history){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LinkedHashMap<LocalDateTime, String> histField = new LinkedHashMap<>();
        if(!"".equals(history)){
            String[] histArray = history.split("\\|");
            for(int i=0; i<histArray.length; i++){
                String[] historyEntry = histArray[i].split("_");
                histField.put(LocalDateTime.parse(historyEntry[0], formatter), historyEntry[1]);
            }
        }
        return histField;
    }

    private static List<String> transformCrendentialIds(String credentials){
        return Arrays.asList(credentials.split("\\|"));
    }

    private static Map<String, String> transformMailAddress(String address){
        Map<String, String> mailAddresses = new HashMap<>();

        String[] mailAddress = address.split("\\|");
        for(int i=0; i<mailAddress.length; i++){
            String[] addressEntry = mailAddress[i].split(":");
            mailAddresses.put(addressEntry[0], addressEntry[1]);
        }

        return mailAddresses;
    }
    
}
