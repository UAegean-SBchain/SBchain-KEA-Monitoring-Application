package com.example.ethereumserviceapp.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final static DateTimeFormatter dFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static LocalDateTime historyDateStringToLDT(String dateS) {
        return LocalDateTime.parse(dateS, formatter);
    }

    public static String dateToString(LocalDateTime ldt) {
        return ldt.format(formatter);
    }

    public static LocalDate dateStringToLD(String dateS) {
        return LocalDate.parse(dateS, dFormatter);
    }

    public static String dateToString(LocalDate ld) {
        return ld.format(dFormatter);
    }
    
}
