package com.dorfer.scheduler_pomodoro.Database;

import android.content.Context;

public class DatabaseThread implements Runnable {

    private static final String TAG = DatabaseThread.class.toString();
    private TaskDatabase database;
    private Context context;

    public DatabaseThread(Context context) {
        this.context = context;
        database = TaskDatabase.getTaskDatabase(context);
    }

    @Override
    public void run() {

    }


}
