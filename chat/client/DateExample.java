package com.javarush.task.task30.task3008.client;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by sergei on 3/5/17.
 */
public class DateExample {
    public static void main(String[] args) {
        SimpleDateFormat dateFormat
                = new SimpleDateFormat("YYYY");

        Date date = Calendar.getInstance().getTime();



//        System.out.println("date: " ConsoleHelper.writeMessage(message);+ dateFormat.format(date));
        String testText = "Sergei: heeeeellloooo! hs js";
        System.out.println(testText.split(": ")[0]);
        System.out.println(testText.split(": ")[1]);
    }
}
