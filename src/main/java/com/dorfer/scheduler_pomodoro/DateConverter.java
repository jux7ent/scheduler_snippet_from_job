package com.dorfer.scheduler_pomodoro;

import android.arch.persistence.room.TypeConverter;

import java.util.Calendar;

public class DateConverter {
    @TypeConverter
    public static String fromDate(Calendar calendar) {
        return "" + calendar.get(Calendar.DAY_OF_MONTH) +
                ":" + calendar.get(Calendar.MONTH) +
                ":" + calendar.get(Calendar.YEAR);
    }

    @TypeConverter
    public static Calendar toDate(String string) {
        Calendar calendar = Calendar.getInstance();
        String[] strs = string.split(":");
        calendar.set(Integer.parseInt(strs[2]),
                Integer.parseInt(strs[1]),
                Integer.parseInt(strs[0]));

        return calendar;
    }
}