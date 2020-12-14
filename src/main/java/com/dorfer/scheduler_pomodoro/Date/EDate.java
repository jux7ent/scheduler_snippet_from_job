package com.dorfer.scheduler_pomodoro.Date;

import java.util.Calendar;

public class EDate {
    private int year, month, day;

    public EDate() {
        this(Calendar.getInstance());
    }

    public EDate(int year, int month, int day) {
        setDate(year, month, day);
    }

    public EDate(Calendar calendar) {
        setDate(calendar);
    }

    public EDate(String date) {
        setDate(date);
    }

    public void setDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public void setDate(Calendar calendar) {
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.DAY_OF_MONTH);
    }

    public void setDate(String date) { //25:01:2005
        String[] strings = date.split(":");
        year = Integer.parseInt(strings[2]);
        month = Integer.parseInt(strings[1]);
        day = Integer.parseInt(strings[0]);
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    @Override
    public String toString() {
        return "" + day + ":" + month + ":" + year;
    }
}
