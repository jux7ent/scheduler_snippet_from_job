package com.dorfer.scheduler_pomodoro.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.support.v7.widget.Toolbar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import com.dorfer.scheduler_pomodoro.DateConverter;
import com.dorfer.scheduler_pomodoro.Dialogs.DurationPickerDialog;
import com.dorfer.scheduler_pomodoro.R;
import com.dorfer.scheduler_pomodoro.Database.Task;
import com.dorfer.scheduler_pomodoro.Database.TaskDatabase;
import com.dorfer.scheduler_pomodoro.Dialogs.TimePickerFragment;
import com.dorfer.scheduler_pomodoro.RVAdapter;
import com.dorfer.scheduler_pomodoro.Recievers.AlarmReceiverTaskNotification;


public class AddTaskActivity extends AppCompatActivity implements View.OnClickListener,
        TimePickerFragment.NoticeDialogListener, CompoundButton.OnCheckedChangeListener,
        DurationPickerDialog.NoticeDialogListener {

    public final static int TIME_NOT_SET = 1500;
    public final static String NOTIFICATION_CHANNEL = "addTask notificationChannel";
    public final static int NOTIFICATION_CHANNEL_ID = 2;
    public final static String KEY_FOR_TASK_CONTENT = "task content";

    private final static String TAG = "myTag " + AddTaskActivity.class.toString();
    private ActionMenuView doneMenuItem;
    private EditText taskContentField;
    private Switch setStartTimeSwitcher;
    private Switch setDurationSwitcher;
    private TextView bindStartTimeSwitcher;
    private TextView bindDurationSwitcher;
    private TextView bindSetNotification;
    private CheckBox notificationCheckBox;
    private TaskDatabase database;
    private Calendar currentDay; //passed date from DayActivity.class
    private int minutesStartTime = TIME_NOT_SET; //1500 = doesn't set start time
    private int minutesDuration = TIME_NOT_SET; //1500 = doesn't set end time
    private boolean startOrEndClicked = true; //true = start; false = end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_task);

        currentDay = DateConverter.toDate(getIntent()
                .getExtras()
                .getString(DayActivity.CURRENT_DAY_TAG));

        //toolbar settings
        Toolbar myToolBar = (Toolbar) findViewById(R.id.add_task_toolbar);

        setSupportActionBar(myToolBar);
        getSupportActionBar().setTitle(R.string.add_new_item_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        doneMenuItem = (ActionMenuView) findViewById(R.id.done_menu_item);
        taskContentField = (EditText) findViewById(R.id.task_content_field);

        setStartTimeSwitcher = (Switch) findViewById(R.id.set_start_time_switcher);
        setDurationSwitcher = (Switch) findViewById(R.id.set_duration_switcher);

        bindStartTimeSwitcher = (TextView) findViewById(R.id.bind_start_time_switcher);
        bindDurationSwitcher = (TextView) findViewById(R.id.bind_duration_switcher);
        bindSetNotification = (TextView) findViewById(R.id.bind_set_notification);

        notificationCheckBox = (CheckBox) findViewById(R.id.add_task_notification_check_box);
        notificationCheckBox.setVisibility(View.INVISIBLE);
        bindSetNotification.setVisibility(View.INVISIBLE);

        bindStartTimeSwitcher.setTextColor(Color.argb(0, 0, 0, 0));
        bindDurationSwitcher.setTextColor(Color.argb(0, 0, 0, 0));

        setStartTimeSwitcher.setOnCheckedChangeListener(this);
        setDurationSwitcher.setOnCheckedChangeListener(this);

        database = TaskDatabase.getTaskDatabase(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_task_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.done_menu_item: {

                Task task = new Task();

                task.setContent(taskContentField.getText().toString());
                Calendar calendar = Calendar.getInstance();
                task.setDate(currentDay);
                task.setStartTime(minutesStartTime);
                task.setDuration(minutesDuration);
                task.setNotification(notificationCheckBox.isChecked());

                Log.d(TAG, "Сейчас будем вставлять!\n" + task.getStartTime());

                insertTask(task);

                if (task.isNotification()) {
                    startNotification(task);
                }

                Log.d(TAG, "done clicked!\n" + task.toString());
                Toast.makeText(this, R.string.done, Toast.LENGTH_SHORT).show();

                finish();

                break;
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.set_start_time_switcher: {

                break;
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();

        switch (id) {
            case R.id.set_start_time_switcher: {
                if(isChecked) {
                    //do stuff when Switch is ON
                    Log.d(TAG, "set start time clicked!\n");
                    startOrEndClicked = true;
                    DialogFragment newFragment = new TimePickerFragment();
                    newFragment.show(getSupportFragmentManager(), "timePicker");
                    notificationCheckBox.setVisibility(View.VISIBLE);
                    bindSetNotification.setVisibility(View.VISIBLE);
             //       bindStartTimeSwitcher.setTextColor(Color.argb(255, 0, 0, 0));

                } else {
                    //do stuff when Switch if OFF
                    minutesStartTime = TIME_NOT_SET;
                    bindStartTimeSwitcher.setTextColor(Color.argb(0, 0, 0, 0));
                    notificationCheckBox.setVisibility(View.INVISIBLE);
                    bindSetNotification.setVisibility(View.INVISIBLE);
                }

                break;
            }

            case R.id.set_duration_switcher: {
                if(isChecked) {
                    //do stuff when Switch is ON
                    Log.d(TAG, "set end time clicked!\n");
                    startOrEndClicked = false;
                /*    DialogFragment newFragment = new DurationPickerDialog();
                    newFragment.show(getSupportFragmentManager(), "timePicker");*/
                    DurationPickerDialog picker = new DurationPickerDialog(this);
                    picker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    picker.show();
                 //   bindDurationSwitcher.setTextColor(Color.argb(255, 0, 0, 0));

                } else {
                    //do stuff when Switch if OFF
                    minutesDuration = TIME_NOT_SET;
                    bindDurationSwitcher.setTextColor(Color.argb(0, 0, 0, 0));
                }
            }
        }

    }

    private void insertTask(final Task task) {
        TaskDatabase database = TaskDatabase.getTaskDatabase(this);
        database.taskDao().insertTask(task);
    }

    private void startNotification(Task task) {
        Intent notificationIntent = new Intent(this,
                AlarmReceiverTaskNotification.class);

        notificationIntent.putExtra(KEY_FOR_TASK_CONTENT, task.getContent());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                task.getId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE),
                0, 0, 0);

        long millis = calendar.getTimeInMillis() + (task.getStartTime() * 60000); //convert minutes on millis

        if (Build.VERSION.SDK_INT < 23) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    millis,
                    pendingIntent);
        }
        else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    millis,
                    pendingIntent);
        }

    }

    @Override
    public void onDialogPositiveClick(int hourOfDay, int minute) {
        if (startOrEndClicked) { //start time
            minutesStartTime = hourOfDay * 60 + minute;
            bindStartTimeSwitcher.setText(RVAdapter.minutesInTimeString(minutesStartTime));
        }
        else { //duration
            minutesDuration = hourOfDay * 60 + minute;
            bindDurationSwitcher.setText(RVAdapter.minutesInTimeString(minutesDuration));
        }
    }

    @Override
    public void updateSwitchers() {
        int alphaForBindStart, alphaForBindDuration;
        if (minutesStartTime == TIME_NOT_SET) {
            setStartTimeSwitcher.setChecked(false);
            alphaForBindStart = 0;
        }
        else {
            alphaForBindStart = 255;
        }

        if (minutesDuration == TIME_NOT_SET) {
            setDurationSwitcher.setChecked(false);
            alphaForBindDuration = 0;
        }
        else {
            alphaForBindDuration = 255;
        }
        bindStartTimeSwitcher.setTextColor(Color.argb(alphaForBindStart, 0, 0, 0));
        bindDurationSwitcher.setTextColor(Color.argb(alphaForBindDuration, 0, 0, 0));
    }
}
