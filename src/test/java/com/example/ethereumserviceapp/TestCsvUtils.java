package com.example.ethereumserviceapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.example.ethereumserviceapp.utils.CsvUtils;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class TestCsvUtils {

    @Test
    public void testCsvImport() {

        try {
            File initialFile = new File("src/main/resources/testData.csv");
            InputStream is = new FileInputStream(initialFile);
            CsvUtils.csvToSsiApplication(is);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
