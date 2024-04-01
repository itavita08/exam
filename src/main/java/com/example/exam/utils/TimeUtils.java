package com.example.exam.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimeUtils {

    public static Timestamp parseTime(String timestampString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
        Date parsedDate = dateFormat.parse(timestampString);
        return new Timestamp(parsedDate.getTime());
    }

    public static String plusTime(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        LocalDateTime dateTime = LocalDateTime.parse(time, formatter).plusHours(1);
        return formatter.format(dateTime);
    }
}
