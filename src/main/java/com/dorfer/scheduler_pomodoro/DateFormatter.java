package com.dorfer.scheduler_pomodoro;

import java.util.Calendar;

import com.dorfer.scheduler_pomodoro.Date.EDate;

public class DateFormatter {
    public static final int ENGLISH = 0;
    public static final int RUSSIAN = 1;

    private static String[] enMonth = {
        "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
            "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    private static String[] ruMonth = {
            "янв.", "февр.", "мар.", "апр.", "мая.", "июн.", "июл",
            "авг.", "сент.", "окт.", "нояб.", "дек."
    };

    private DateFormatter() {
        //empty
    }

    public static String dateToString(Calendar calendar, int language) {
        return dateToString(calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.YEAR),
                language);
    }

    public static String dateToString(EDate date, int language) {
        return dateToString(date.getDay(), date.getMonth(), date.getYear(), language);
    }

    public static String dateToString(int day, int month, int year, int language) {

        String result = "problem with language";

        switch (language) {
            case ENGLISH: {
                result = "" + (day < 10 ? "0" + day : day) +
                        " " + enMonth[month - 1] + " " + year;
                break;
            }
            case RUSSIAN: {
                result = "" + (day < 10 ? "0" + day : day) +
                        " " + ruMonth[month - 1] + " " + year;
                break;
            }
        }

        return result;
    }

}
