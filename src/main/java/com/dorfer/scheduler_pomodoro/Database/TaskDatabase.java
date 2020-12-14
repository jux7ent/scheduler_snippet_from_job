package com.dorfer.scheduler_pomodoro.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.dorfer.scheduler_pomodoro.DateConverter;


@Database(entities = {Task.class}, version = 1)
@TypeConverters({DateConverter.class})
public abstract class TaskDatabase extends RoomDatabase {
    private static TaskDatabase INSTANCE;
    private static String DB_NAME = "scheduler.db";

    public abstract TaskDao taskDao();

    public static TaskDatabase getTaskDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (TaskDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TaskDatabase.class, DB_NAME)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
