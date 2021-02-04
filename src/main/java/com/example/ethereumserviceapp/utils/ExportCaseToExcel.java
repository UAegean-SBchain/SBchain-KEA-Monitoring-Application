package com.example.ethereumserviceapp.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.ethereumserviceapp.model.Case;
import com.example.ethereumserviceapp.model.CaseHistory;
import com.example.ethereumserviceapp.model.CasePayment;

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
     
    public ExportCaseToExcel(Case monitoredCase) {
        this.monitoredCase = monitoredCase;
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
        //createCell(row, cellNum, headerStr, style);      
        // createCell(row, 1, "Latest Date", style);       
        // createCell(row, 2, "Latest State", style);    
        // createCell(row, 3, "Offset", style);
        // createCell(row, 4, "Rejection Date", style);

        // createCell(row, 6, "History Date", style);
        // createCell(row, 7, "History State", style);
        // createCell(row, 8, "History Daily Benefit", style);
        // createCell(row, 9, "History Daily Sum", style);

        // createCell(row, 11, "Payment Date", style);
        // createCell(row, 12, "Payment State", style);
        // createCell(row, 13, "Payment Value", style);
         
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
        int rowCount = 1;
 
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        Row caseRow = sheet.createRow(rowCount++);
        int columnCount = 0;

        List<String> caseHeaders = Arrays.asList("Uuid", "Latest Date", "Latest State", "Offset", "Rejection Date", "Daily Benefit", "Current Sum");

        writeHeaderLine(0, caseHeaders);

        createCell(caseRow, columnCount++, monitoredCase.getUuid(), style);
        createCell(caseRow, columnCount++, DateUtils.dateToString(monitoredCase.getDate()), style);
        createCell(caseRow, columnCount++, String.valueOf(monitoredCase.getState()), style);
        createCell(caseRow, columnCount++, String.valueOf(monitoredCase.getOffset()), style);
        createCell(caseRow, columnCount++, monitoredCase.getRejectionDate(), style);
        createCell(caseRow, columnCount++, String.valueOf(monitoredCase.getDailyValue()), style);
        createCell(caseRow, columnCount++, String.valueOf(monitoredCase.getDailySum()), style);

        List<String> historyHeaders = Arrays.asList("History Date", "History State", "History Daily Benefit", "History Daily Sum");

        writeHeaderLine(3, historyHeaders);
        rowCount = 4;
        //columnCount = 0;
        for(CaseHistory ch : monitoredCase.getCaseHistory()){
            Row row = sheet.createRow(rowCount++);
            columnCount = 0;
            //int columnCount = 6;
            createCell(row, columnCount++, DateUtils.dateToString(ch.getDate()), style);
            createCell(row, columnCount++, String.valueOf(ch.getState()), style);
            createCell(row, columnCount++, String.valueOf(ch.getDailyBenefit()), style);
            createCell(row, columnCount++, String.valueOf(ch.getDailySum()), style);
        }
        //reset row count 
        //rowCount = 1;

        List<String> paymentHeaders = Arrays.asList("Payment Date", "Payment State", "Payment Value");

        writeHeaderLine(monitoredCase.getCaseHistory().size() + 5 , paymentHeaders);
        rowCount = monitoredCase.getCaseHistory().size() + 6;

        for(CasePayment ph : monitoredCase.getPaymentHistory()){
            Row row = sheet.createRow(rowCount++);
            columnCount = 0;
            //int columnCount = 11;
            createCell(row, columnCount++, DateUtils.dateToString(ph.getPaymentDate()), style);
            createCell(row, columnCount++, String.valueOf(ph.getState()), style);
            createCell(row, columnCount++, String.valueOf(ph.getPayment()), style);
        }
        
                 
        // for (Case user : listUsers) {
        //     Row row = sheet.createRow(rowCount++);
        //     int columnCount = 0;
             
        //     createCell(row, columnCount++, user.getId(), style);
        //     createCell(row, columnCount++, user.getEmail(), style);
        //     createCell(row, columnCount++, user.getFullName(), style);
        //     createCell(row, columnCount++, user.getRoles().toString(), style);
        //     createCell(row, columnCount++, user.isEnabled(), style);
             
        // }
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
}
