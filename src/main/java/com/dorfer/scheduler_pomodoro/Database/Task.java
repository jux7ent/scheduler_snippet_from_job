package com.dorfer.scheduler_pomodoro.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import java.util.Calendar;

import com.dorfer.scheduler_pomodoro.DateConverter;

@Entity(tableName = "tasks")
public class Task {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    @TypeConverters({DateConverter.class})
    private Calendar date;

    @ColumnInfo
    private String content;

    @ColumnInfo(name = "start_time")
    private int startTime; //minutes

    @ColumnInfo
    private int duration; //minutes

    @ColumnInfo(name = "is_complete")
    private boolean isComplete;

    @ColumnInfo(name = "pomodoro")
    private int pomodoro;

    @ColumnInfo(name = "notification")
    private boolean notification;

    @ColumnInfo(name = "category")
    private int category;

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public int getPomodoro() {
        return pomodoro;
    }

    public void setPomodoro(int pomodoro) {
        this.pomodoro = pomodoro;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return date.toString();
    }
}

