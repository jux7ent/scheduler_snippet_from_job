package com.dorfer.scheduler_pomodoro.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dorfer.scheduler_pomodoro.Recievers.AlarmReceiverPomodoro;
import com.dorfer.scheduler_pomodoro.CustomView.CircularIndicatorView;
import com.dorfer.scheduler_pomodoro.R;
import com.dorfer.scheduler_pomodoro.RVAdapter;

public class PomodoroActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String APP_PREFERENCE_DEFAULT_WORK_DURATION = "default work duration";
    public static final String APP_PREFERENCE_DEFAULT_BREAK_DURATION = "default break duration";
    public static final String APP_PREFERENCE_WORK_DURATION = "workDuration";
    public static final String APP_PREFERENCE_BREAK_DURATION = "breakDuration";
    public static final String APP_PREFERENCE_WORK_OR_BREAK = "work or break";;
    public static final String APP_PREFERENCE_START_OR_PAUSE = "start or pause";
    public static final String APP_PREFERENCE_LAST_TIME = "last time";
    public static final String APP_PREFERENCE_SOUND = "sound";
    public static final String APP_PREFERENCE_VIBRATION = "vibration";


    public static final int DEFAULT_WORK_DURATION = 1500; //1500 seconds = 25 minutes
    public static final int DEFAULT_BREAK_DURATION = 300; //300 seconds
    public static final boolean DEFAULT_SOUND = true;
    public static final boolean DEFAULT_VIBRATION = true;
    public static final String NOTIFICATION_CHANNEL = "pomodoroChannel";
    public static final int NOTIFICATION_CHANNEL_ID = 3;

    private final static String TAG = "myTag" + PomodoroActivity.class.toString();
    private int workDuration, breakDuration, defaultWorkDuration, defaultBreakDuration;
    private boolean workOrBreak; //work = true; break = false;
    private boolean startOrPause; //start = true; pause = false;
    private long lastTime;
    private boolean timerIsBusy;
    private ActionMenuView settingsMenuItem;
    private SharedPreferences sharedPref;
    private TextView pomodoroClockFace;
    private FloatingActionButton fabStop, fabStartPause, fabNextAction;
    private ImageView workOrBreakIcon;
    private CircularIndicatorView mCircularIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pomodoro_activity);

        sharedPref = getSharedPreferences(getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);

        Toolbar pomodoroToolbar = (Toolbar) findViewById(R.id.pomodoro_toolbar);

        setSupportActionBar(pomodoroToolbar);
        getSupportActionBar().setTitle(R.string.pomodoro_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pomodoroToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        settingsMenuItem = (ActionMenuView) findViewById(R.id.settings_menu_item);
        pomodoroClockFace = (TextView) findViewById(R.id.pomodoro_clock_face);
        fabStop = (FloatingActionButton) findViewById(R.id.fab_refresh);
        fabStartPause = (FloatingActionButton) findViewById(R.id.fab_start_pause);
        fabNextAction = (FloatingActionButton) findViewById(R.id.fab_next_action);
        workOrBreakIcon = (ImageView) findViewById(R.id.work_or_break_icon);
        mCircularIndicatorView = (CircularIndicatorView) findViewById(R.id.circularIndicatorView);

        fabStop.setOnClickListener(this);
        fabStartPause.setOnClickListener(this);
        fabNextAction.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Удаляем конкретное уведомление
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(NOTIFICATION_CHANNEL_ID);

        workDuration = sharedPref.getInt(APP_PREFERENCE_WORK_DURATION,
                DEFAULT_WORK_DURATION);
        breakDuration = sharedPref.getInt(APP_PREFERENCE_BREAK_DURATION,
                DEFAULT_BREAK_DURATION);
        workOrBreak = sharedPref.getBoolean(APP_PREFERENCE_WORK_OR_BREAK,
                true);
        startOrPause = sharedPref.getBoolean(APP_PREFERENCE_START_OR_PAUSE,
                false);
        defaultWorkDuration = sharedPref.getInt(APP_PREFERENCE_DEFAULT_WORK_DURATION,
                DEFAULT_WORK_DURATION);
        defaultBreakDuration = sharedPref.getInt(APP_PREFERENCE_DEFAULT_BREAK_DURATION,
                DEFAULT_BREAK_DURATION);
        lastTime = sharedPref.getLong(APP_PREFERENCE_LAST_TIME,
                System.currentTimeMillis());

        if (!startOrPause) { //if on pause
            workDuration = defaultWorkDuration;
            breakDuration = defaultBreakDuration;
        }

        double differenceTime = 0;

        if (startOrPause) {
            differenceTime = System.currentTimeMillis() - lastTime;
            differenceTime /= 1000.; //convert millis to seconds
        }

        if (workOrBreak) { //work
            workDuration -= differenceTime;
            if (workDuration <= 0) {
                switchActivity();
            }
            else {
                pomodoroClockFace.setText("" + RVAdapter.minutesInTimeString(workDuration));
                workOrBreakIcon.setImageDrawable(getDrawable(R.drawable.ic_pomodoro_work));
                mCircularIndicatorView.setAngle(workDuration / (float)defaultWorkDuration);
            }
        }
        else { //break
            breakDuration -= differenceTime;
            if (breakDuration <= 0) {
                switchActivity();
            }
            else {
                pomodoroClockFace.setText("" + RVAdapter.minutesInTimeString(breakDuration));
                workOrBreakIcon.setImageDrawable(getDrawable(R.drawable.ic_pomodoro_break));
                mCircularIndicatorView.setAngle(breakDuration / (float)defaultBreakDuration);
            }
        }

        if (startOrPause) { //start
            fabStartPause.setImageDrawable(getDrawable(R.drawable.ic_pomodoro_pause));
            runTimer();
        }
    }

    @Override
    protected void onPause() {
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt(APP_PREFERENCE_WORK_DURATION, workDuration);
        editor.putInt(APP_PREFERENCE_BREAK_DURATION, breakDuration);
        editor.putBoolean(APP_PREFERENCE_WORK_OR_BREAK, workOrBreak);
        editor.putBoolean(APP_PREFERENCE_START_OR_PAUSE, startOrPause);
        editor.putLong(APP_PREFERENCE_LAST_TIME, System.currentTimeMillis());


        editor.apply();

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pomodoro_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.settings_menu_item: {
                Toast.makeText(this, "Pomodoro settings clicked!!!", Toast.LENGTH_SHORT)
                        .show();

                Intent intent = new Intent(this, SettingsPomodoroActivity.class);
                startActivityForResult(intent, 0);

                break;
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.fab_refresh: {
                startOrPause = false;
                fabStartPause.setImageDrawable(getDrawable(R.drawable.ic_pomodoro_play));

                if (workOrBreak) { //work
                    workDuration = defaultWorkDuration;
                    pomodoroClockFace.setText("" + RVAdapter.minutesInTimeString(workDuration));
                    mCircularIndicatorView.setAngle(1);
                }
                else { //break
                    breakDuration = defaultBreakDuration;
                    pomodoroClockFace.setText("" + RVAdapter.minutesInTimeString(breakDuration));
                    mCircularIndicatorView.setAngle(1);
                }

                break;
            }
            case R.id.fab_start_pause: {
                if (startOrPause) { //now start(for pause clicked)
                    startOrPause = false;
                    fabStartPause.setImageDrawable(getDrawable(R.drawable.ic_pomodoro_play));

                    stopAlarm();
                }
                else { // for start clicked
                    startOrPause = true;
                    fabStartPause.setImageDrawable(getDrawable(R.drawable.ic_pomodoro_pause));
                    runTimer();

                    if (workOrBreak) { //work
                        startAlarm(workDuration * 1000);
                    }
                    else {
                        startAlarm(breakDuration * 1000);
                    }
                }

                break;
            }
            case R.id.fab_next_action: {
                switchActivity();
                break;
            }
        }
    }

    private void runTimer() {
        if (!timerIsBusy) {
            timerIsBusy = true;
            final Handler handler = new Handler();
            handler.post(new Runnable() {

                @Override
                public void run() {
                    if (workOrBreak && startOrPause) { //work
                        --workDuration;
                        pomodoroClockFace.setText("" + RVAdapter.minutesInTimeString(workDuration));
                        mCircularIndicatorView.setAngle(
                                workDuration / (float)defaultWorkDuration,
                                false);
                        if (workDuration == 0) {
                            startOrPause = false;
                            timerIsBusy = false;
                            switchActivity();
                        }
                    } else if (startOrPause) { //break
                        --breakDuration;
                        pomodoroClockFace.setText("" + RVAdapter.minutesInTimeString(breakDuration));
                        mCircularIndicatorView.setAngle(
                                breakDuration / (float)defaultBreakDuration,
                                false);
                        if (breakDuration == 0) {
                            startOrPause = false;
                            timerIsBusy = false;
                            switchActivity();
                        }
                    }
                    else {
                        timerIsBusy = false;
                    }
                    if (startOrPause) { //start
                        handler.postDelayed(this, 1000);
                    }
                }
            });
        }
    }

    private void switchActivity() {
        fabStartPause.setImageDrawable(getDrawable(R.drawable.ic_pomodoro_play));
        startOrPause = false;
        if (!workOrBreak) { //was break
            workDuration = defaultWorkDuration;
            pomodoroClockFace.setText("" + RVAdapter.minutesInTimeString(workDuration));
            workOrBreakIcon.setImageDrawable(getDrawable(R.drawable.ic_pomodoro_work));
            mCircularIndicatorView.setAngle(1);
        }
        else {
            breakDuration = defaultBreakDuration;
            pomodoroClockFace.setText("" + RVAdapter.minutesInTimeString(breakDuration));
            workOrBreakIcon.setImageDrawable(getDrawable(R.drawable.ic_pomodoro_break));
            mCircularIndicatorView.setAngle(1);
        }
        workOrBreak = !workOrBreak;
    }

    private void startAlarm(int millis) {
        Log.d(TAG, "in startAlarm");
        Intent intent = new Intent(getApplicationContext(), AlarmReceiverPomodoro.class);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(PomodoroActivity.this,
                        0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        if (Build.VERSION.SDK_INT < 23) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + millis,
                    pendingIntent);
        }
        else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + millis,
                    pendingIntent);
        }
    }

    private void stopAlarm() {
        Log.d(TAG, "in stopAlarm");
        Intent intent = new Intent(getApplicationContext(), AlarmReceiverPomodoro.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(PomodoroActivity.this,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
