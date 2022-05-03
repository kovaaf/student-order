package edu.javacourse.studentorder.util;
/*
 *   Created by Kovalyov Anton 03.05.2022
 */

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

    public static final String DD_MM_YYYY = "dd.MM.yyyy";

    @Override
    public LocalDate unmarshal(String s) throws Exception {
        return LocalDate.parse(s, DateTimeFormatter.ofPattern(DD_MM_YYYY));
    }

    @Override
    public String marshal(LocalDate localDate) throws Exception {
        return localDate.format(DateTimeFormatter.ofPattern(DD_MM_YYYY));
    }
}
