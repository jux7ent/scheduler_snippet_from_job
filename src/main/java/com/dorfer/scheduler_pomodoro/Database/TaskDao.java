package com.dorfer.scheduler_pomodoro.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Calendar;
import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY date ASC")
    List<Task> getAllTasks();

    @Query("SELECT * FROM tasks WHERE id = :id")
    Task getTask(int id);

    @Query("SELECT * FROM tasks WHERE date = :day ORDER BY start_time ASC")
    List<Task> getDayTasks(Calendar day);

    @Insert
    void insertTask(Task task);

    @Delete
    void deleteTask(Task task);

    @Update
    void updateTask(Task task);
}