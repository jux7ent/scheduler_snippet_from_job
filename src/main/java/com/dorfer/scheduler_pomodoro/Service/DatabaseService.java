package com.dorfer.scheduler_pomodoro.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.List;

import com.dorfer.scheduler_pomodoro.Database.Task;
import com.dorfer.scheduler_pomodoro.Database.TaskDatabase;

public class DatabaseService extends Service {

    private static final String TAG = DatabaseService.class.toString();
    private TaskDatabase database;
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        HandlerThread thread = new HandlerThread("ServiceStartArguments", 5);

        thread.start();

        database = TaskDatabase.getTaskDatabase(this);
        Log.d(TAG, "database created");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public List<Task> getAllTasks() {
        return database.taskDao().getAllTasks();
    }
}
