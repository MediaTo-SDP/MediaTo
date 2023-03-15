package com.github.sdp.mediato.formats;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * Dates class defines the format used for dates and provides
 * methods to get them as a String
 */
public class Dates {
    public static String DATE_PATTERN = "dd/MM/yyyy";
    public static DateFormat format = new SimpleDateFormat(DATE_PATTERN);

    public static String getToday(){
        return format.format(new Date());
    }
}
