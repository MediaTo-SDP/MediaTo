package com.github.sdp.mediato.formats;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.time.LocalDate;
import java.util.Date;

public class DatesTests {

    @Test
    //Tests that the Dates class returns today's date with the right format
    public void getsTodaysDateProperly(){
        LocalDate today = LocalDate.now();
        String expected = pad(today.getDayOfMonth()) + "/" + pad(today.getMonthValue()) + "/" + today.getYear();
        assertEquals(expected, Dates.getToday());
    }

    //Adds leading zero to number
    public String pad(int number){
        if(number < 10) return "0" + number;
        else return String.valueOf(number);
    }
}
