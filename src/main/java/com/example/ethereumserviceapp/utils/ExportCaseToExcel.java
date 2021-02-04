package com.example.ethereumserviceapp.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.CaseHistory;
import com.example.ethereumserviceapp.model.CasePayment;
import com.example.ethereumserviceapp.model.HouseholdMember;
import com.example.ethereumserviceapp.model.entities.SsiApplication;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExportCaseToExcel {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private Case monitoredCase;
    private List<SsiApplication> ssiApps;
     
    public ExportCaseToExcel(Case monitoredCase, List<SsiApplication> ssiApps) {
        this.monitoredCase = monitoredCase;
        this.ssiApps = ssiApps;
        workbook = new XSSFWorkbook();
    } 
 
    private void writeHeaderLine(Integer rowNum, List<String> headerStr) {
        //sheet = workbook.createSheet("Case");
         
        Row row = sheet.createRow(rowNum);
         
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
         
        for(int i=0; i<headerStr.size(); i++){
            createCell(row, i, headerStr.get(i), style);     
        }
    }
     
    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }
     
    private void writeDataLines() {

        sheet = workbook.createSheet("Case");
        int rowCount = 0;
 
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        //Row row = sheet.createRow(rowCount++);

        List<String> ssiAppHeaders = Arrays.asList("uuid",	"taxisAfm",	"taxisFamilyName",
        "taxisFirstName",	"taxisFathersName",	"taxisMothersName",	"taxisFamilyNameLatin",	"taxisFirstNameLatin",	"taxisFathersNameLatin",	"taxisMothersNameLatin",
        "taxisAmka",	"taxisDateOfBirth",	"taxisGender",	"nationality",	"maritalStatus",	"hospitalized",	"hospitalizedSpecific",
        "monk",	"luxury",	"street",	"streetNumber",	"po",	"prefecture",	"ownership",	"supplyType",	"meterNumber",
        "participateFead",	"selectProvider",	"gender",	"disabilityStatus",	"levelOfEducation",	"employmentStatus",	"unemployed",	"employed",
        "oaedId",	"oaedDate",	"email",	"mobilePhone",	"landline",	"iban",	"streetNumber_1",	"prefecture_2",	"municipality",	"parenthood",	"custody",
        "additionalAdults",	"salariesR",	"pensionsR",	"farmingR",	"freelanceR",	"rentIncomeR",	"unemploymentBenefitR",	"otherBenefitsR",	"ekasR",
        "otherIncomeR",	"ergomeR",	"depositInterestA",	"depositsA",	"domesticRealEstateA",	"foreignRealEstateA",	"vehicleValueA",	"investmentsA",
        "totalIncome",	"savedInDb",	"status",	"submittedMunicipality",	"time",	"householdPrincipal",	"householdComposition",	"householdCompositionHistory",
        "salariesRHistory",	"pensionsRHistory",	"farmingRHistory",	"freelanceRHistory",	"otherBenefitsRHistory",	"depositsAHistory",	"domesticRealEstateAHistory",
        "foreignRealEstateAHistory", "unemploymentBenefitRHistory",	"monthlyGuarantee",	"totalIncome_3",	"monthlyIncome",	"monthlyAid",	"savedInDb_4",	"status_5");
        writeHeaderLine(rowCount, ssiAppHeaders);
        int columnCount = 0;

        for (SsiApplication app : ssiApps) {
            rowCount = rowCount+ 1;
            Row row = sheet.createRow(rowCount);
            columnCount = 0;

            createCell(row, columnCount++, app.getUuid(), style);
            //ssn
            //uuid
            //taxisAfm
            createCell(row, columnCount++, app.getTaxisAfm(), style);
            //taxisFamilyName
            createCell(row, columnCount++, app.getTaxisFamilyName(), style);
            //taxisFirstName
            createCell(row, columnCount++, app.getTaxisFirstName(), style);
            //taxisFathersName
            createCell(row, columnCount++, app.getTaxisFathersName(), style);
            //taxisMothersName
            createCell(row, columnCount++, app.getTaxisMothersName(), style);
            //taxisFamilyNameLatin
            createCell(row, columnCount++, app.getTaxisFamilyNameLatin(), style);
            //taxisFirstNameLatin
            createCell(row, columnCount++, app.getTaxisFirstNameLatin(), style);
            //taxisFathersNameLatin
            createCell(row, columnCount++, app.getTaxisFathersNameLatin(), style);
            createCell(row, columnCount++, app.getTaxisMothersNameLatin(), style);
            //taxisMothersNameLatin
            createCell(row, columnCount++, app.getTaxisAmka(), style);
            //taxisAmka
            createCell(row, columnCount++, app.getTaxisDateOfBirth(), style);
            //taxisDateOfBirth
            createCell(row, columnCount++, app.getTaxisGender(), style);
            //taxisGender
            createCell(row, columnCount++, app.getNationality(), style);
            //nationality
            createCell(row, columnCount++, app.getMaritalStatus(), style);
            //maritalStatus
            createCell(row, columnCount++, app.getHospitalized(), style);
            //hospitalized
            createCell(row, columnCount++, app.getHospitalizedSpecific(), style);
            //hospitalizedSpecific
            createCell(row, columnCount++, app.getMonk(), style);
            //monk
            createCell(row, columnCount++, app.getLuxury(), style);
            //luxury
            createCell(row, columnCount++, app.getStreet(), style);
            //street
            createCell(row, columnCount++, app.getStreetNumber(), style);
            //streetNumber
            createCell(row, columnCount++, app.getPo(), style);
            //po
            createCell(row, columnCount++, app.getPrefecture(), style);
            //prefecture
            createCell(row, columnCount++, app.getOwnership(), style);
            //ownership
            createCell(row, columnCount++, app.getSupplyType(), style);
            //supplyType
            createCell(row, columnCount++, app.getMeterNumber(), style);
            //meterNumber
            createCell(row, columnCount++, app.getParticipateFead(), style);
            //participateFead
            createCell(row, columnCount++, app.getSelectProvider(), style);
            //selectProvider
            createCell(row, columnCount++, app.getGender(), style);
            //gender
            createCell(row, columnCount++, app.getDisabilityStatus(), style);
            //disabilityStatus
            createCell(row, columnCount++, app.getLevelOfEducation(), style);
            //levelOfEducation
            createCell(row, columnCount++, app.getEmploymentStatus(), style);
            //employmentStatus
            createCell(row, columnCount++, app.getUnemployed(), style);
            //unemployed
            createCell(row, columnCount++, app.getEmployed(), style);
            //employed
            createCell(row, columnCount++, app.getOaedId(), style);
            //oaedId
            createCell(row, columnCount++, app.getOaedDate(), style);
            //oaedDate
            createCell(row, columnCount++, app.getEmail(), style);
            //email
            createCell(row, columnCount++, app.getMobilePhone(), style);
            //mobilePhone
            createCell(row, columnCount++, app.getLandline(), style);
            //landline
            createCell(row, columnCount++, app.getIban(), style);
            //iban
            createCell(row, columnCount++, app.getStreetNumber1(), style);
            //streetNumber_1
            createCell(row, columnCount++, app.getPrefecture2(), style);
            //prefecture_2
            createCell(row, columnCount++, app.getMunicipality(), style);
            //municipality
            createCell(row, columnCount++, app.getParenthood(), style);
            //parenthood
            createCell(row, columnCount++, app.getCustody(), style);
            //custody
            createCell(row, columnCount++, app.getAdditionalAdults(), style);
            //additionalAdults
            createCell(row, columnCount++, app.getSalariesR(), style);
            //salariesR
            createCell(row, columnCount++, app.getPensionsR(), style);
            //pensionsR
            createCell(row, columnCount++, app.getFarmingR(), style);
            //farmingR
            createCell(row, columnCount++, app.getFreelanceR(), style);
            //freelanceR
            createCell(row, columnCount++, app.getRentIncomeR(), style);
            //rentIncomeR
            createCell(row, columnCount++, app.getUnemploymentBenefitR(), style);
            //unemploymentBenefitR
            createCell(row, columnCount++, app.getOtherBenefitsR(), style);
            //otherBenefitsR
            createCell(row, columnCount++, app.getEkasR(), style);
            //ekasR
            createCell(row, columnCount++, app.getOtherIncomeR(), style);
            //otherIncomeR
            createCell(row, columnCount++, app.getErgomeR(), style);
            //ergomeR
            createCell(row, columnCount++, app.getDepositInterestA(), style);
            //depositInterestA
            createCell(row, columnCount++, app.getDepositsA(), style);
            //depositsA
            createCell(row, columnCount++, app.getDomesticRealEstateA(), style);
            //domesticRealEstateA
            createCell(row, columnCount++, app.getForeignRealEstateA(), style);
            //foreignRealEstateA
            createCell(row, columnCount++, app.getVehicleValueA(), style);
            //vehicleValueA
            createCell(row, columnCount++, app.getInvestmentsA(), style);
            //investmentsA

            // "totalIncome",
            createCell(row, columnCount++, app.getTotalIncome(), style);
            //            "savedInDb",
            createCell(row, columnCount++, "OK", style);
            //            "status",
            createCell(row, columnCount++, "ACCEPTED", style);
            //            "submittedMunicipality",
            createCell(row, columnCount++, "Dimos Paianias", style);
            //            "time",
            
            createCell(row, columnCount++, DateUtils.dateToString(app.getTime()), style);
            //            "householdPrincipal",
            createCell(row, columnCount++, makeMemberToHouseholdString(app.getHouseholdPrincipal()), style);
            //            "householdComposition",
            createCell(row, columnCount++, makeHouseHoldString(app.getHouseholdComposition()), style);
            //            "householdCompositionHistory",
            createCell(row, columnCount++, makeFinancialHistoryString(app.getSalariesRHistory()), style);
            //            "salariesRHistory",
            createCell(row, columnCount++, makeFinancialHistoryString(app.getPensionsRHistory()), style);
            //            "pensionsRHistory",
            createCell(row, columnCount++, makeFinancialHistoryString(app.getFarmingRHistory()), style);
            //            "farmingRHistory",
            createCell(row, columnCount++, makeFinancialHistoryString(app.getFreelanceRHistory()), style);
            //            "freelanceRHistory",
            createCell(row, columnCount++, makeFinancialHistoryString(app.getOtherBenefitsRHistory()), style);
            //            "otherBenefitsRHistory",
            createCell(row, columnCount++, makeFinancialHistoryString(app.getDepositsAHistory()), style);
            //            "depositsAHistory",
            createCell(row, columnCount++, makeFinancialHistoryString(app.getDomesticRealEstateAHistory()), style);
            //            "domesticRealEstateAHistory",
            createCell(row, columnCount++, makeFinancialHistoryString(app.getForeignRealEstateAHistory()), style);
            //            "foreignRealEstateAHistory",
            createCell(row, columnCount++, makeFinancialHistoryString(app.getUnemploymentBenefitRHistory()), style);
            //            "unemploymentBenefitRHistory",
            createCell(row, columnCount++, " ", style);
            //            "monthlyGuarantee",
            createCell(row, columnCount++, " ", style);
            //            "totalIncome_3",
            createCell(row, columnCount++, " ", style);
            //            "monthlyIncome",
            createCell(row, columnCount++, " ", style);
            //            "monthlyAid",
            createCell(row, columnCount++, " ", style);
            //            "savedInDb_4",
            createCell(row, columnCount++, "TRUE", style);
            //            "status_5"
            createCell(row, columnCount++, " ", style);

        }



        columnCount = 0;

        List<String> caseHeaders = Arrays.asList("Uuid", "Latest Date", "Latest State", "Offset", "Rejection Date", "Daily Benefit", "Current Sum");
        rowCount = rowCount + 2;
        
        writeHeaderLine(rowCount, caseHeaders);

        rowCount = rowCount + 1;
        Row caseRow = sheet.createRow(rowCount);

        createCell(caseRow, columnCount++, monitoredCase.getUuid(), style);
        createCell(caseRow, columnCount++, DateUtils.dateToString(monitoredCase.getDate()), style);
        createCell(caseRow, columnCount++, String.valueOf(monitoredCase.getState()), style);
        createCell(caseRow, columnCount++, String.valueOf(monitoredCase.getOffset()), style);
        createCell(caseRow, columnCount++, monitoredCase.getRejectionDate(), style);
        createCell(caseRow, columnCount++, String.valueOf(monitoredCase.getDailyValue()), style);
        createCell(caseRow, columnCount++, String.valueOf(monitoredCase.getDailySum()), style);

        List<String> historyHeaders = Arrays.asList("History Date", "History State", "History Daily Benefit", "History Daily Sum");

        rowCount = rowCount +2;

        writeHeaderLine(rowCount, historyHeaders);

        //columnCount = 0;
        for(CaseHistory ch : monitoredCase.getCaseHistory()){
            rowCount = rowCount + 1;
            Row histRow = sheet.createRow(rowCount);
            columnCount = 0;
            //int columnCount = 6;
            createCell(histRow, columnCount++, DateUtils.dateToString(ch.getDate()), style);
            createCell(histRow, columnCount++, String.valueOf(ch.getState()), style);
            createCell(histRow, columnCount++, String.valueOf(ch.getDailyBenefit()), style);
            createCell(histRow, columnCount++, String.valueOf(ch.getDailySum()), style);
        }
        //reset row count 
        //rowCount = 1;

        List<String> paymentHeaders = Arrays.asList("Payment Date", "Payment State", "Payment Value");

        rowCount = rowCount + 2;
        writeHeaderLine(rowCount , paymentHeaders);

        for(CasePayment ph : monitoredCase.getPaymentHistory()){
            rowCount = rowCount + 1;
            Row payRow = sheet.createRow(rowCount);
            columnCount = 0;
            //int columnCount = 11;
            createCell(payRow, columnCount++, DateUtils.dateToString(ph.getPaymentDate()), style);
            createCell(payRow, columnCount++, String.valueOf(ph.getState()), style);
            createCell(payRow, columnCount++, String.valueOf(ph.getPayment()), style);
        }
        
    }
     
    public void export() throws IOException {
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "ExampleCase.xlsx";

        //writeHeaderLine();
        writeDataLines();

        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        workbook.write(outputStream);
        workbook.close();
         
        outputStream.close();
         
    }

    public static String makeHouseHoldString(List<HouseholdMember> householdAppList) {
        StringJoiner strJoiner = new StringJoiner(
                "|", "", "");
        householdAppList.stream().map(app -> {
            return makeMemberToHouseholdString(app);
        }).forEach(memberStr -> {
            strJoiner.add(memberStr);
        });
        return strJoiner.toString();
    }

    public static String makeMemberToHouseholdString(SsiApplication member) {
        //for 2 members: name;surname;afm;date|name;surname;afm;date
        StringBuilder sb = new StringBuilder();
        sb.append(member.getTaxisFirstName()).append(";")
                .append(member.getTaxisFamilyName()).append(";")
                .append(member.getTaxisAfm()).append(";")
                .append(StringUtils.isEmpty(member.getTaxisDateOfBirth()) ? "" : member.getTaxisDateOfBirth()).append(";")
        ;
        return sb.toString();
    }

    public static String makeMemberToHouseholdString(HouseholdMember member) {
        //for 2 members: name;surname;afm;date|name;surname;afm;date
        StringBuilder sb = new StringBuilder();
        sb.append(member.getName()).append(";")
                .append(member.getSurname()).append(";")
                .append(member.getAfm()).append(";")
                .append(StringUtils.isEmpty(member.getDateOfBirth()) ? "" : member.getDateOfBirth()).append(";")
        ;
        return sb.toString();
    }

    public static String makeFinancialHistoryString(Map<String, String> finHistory){

        StringBuilder sb = new StringBuilder();
        if(finHistory != null && !finHistory.isEmpty()){
            finHistory.forEach((k,v)-> sb.append(k).append("==").append(v).append("|"));
        }
        
        return sb.toString();
    }
}
