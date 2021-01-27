package com.example.ethereumserviceapp.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDateTime historyDateStringToLDT(String dateS) {
        return LocalDateTime.parse(dateS, formatter);

    }

    public static String dateToString(LocalDateTime ldt) {
        return ldt.format(formatter);

    }
}
