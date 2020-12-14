package com.dorfer.scheduler_pomodoro.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.dorfer.scheduler_pomodoro.Dialogs.NumberPickerDialog;
import com.dorfer.scheduler_pomodoro.R;

public class SettingsPomodoroActivity extends AppCompatActivity implements View.OnClickListener,
        NumberPickerDialog.NoticeDialogListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "myTag" + SettingsPomodoroActivity.class.toString();
    private int workDuration;
    private int breakDuration;
    private TextView workDurationTV, breakDurationTV;
    private Switch soundSwitcher, vibrationSwitcher;
    private SharedPreferences sharedPref;
    private boolean clickedWorkOrBreak = true; //true = work; false = break;
    private boolean soundState, vibrationState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_pomodoro_activity);

        sharedPref = getSharedPreferences(getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);

        Toolbar settingsPomodoroToolbar = (Toolbar) findViewById(R.id.settings_pomodoro_toolbar);

        setSupportActionBar(settingsPomodoroToolbar);
        getSupportActionBar().setTitle(R.string.pomodoro_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        settingsPomodoroToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        workDuration = sharedPref.getInt(PomodoroActivity.APP_PREFERENCE_DEFAULT_WORK_DURATION,
                PomodoroActivity.DEFAULT_WORK_DURATION);
        breakDuration = sharedPref.getInt(PomodoroActivity.APP_PREFERENCE_DEFAULT_BREAK_DURATION,
                PomodoroActivity.DEFAULT_BREAK_DURATION);
        soundState = sharedPref.getBoolean(PomodoroActivity.APP_PREFERENCE_SOUND,
                PomodoroActivity.DEFAULT_SOUND);
        vibrationState = sharedPref.getBoolean(PomodoroActivity.APP_PREFERENCE_VIBRATION,
                PomodoroActivity.DEFAULT_VIBRATION);

        workDurationTV = (TextView) findViewById(R.id.pomodoro_settings_work_duration_number);
        breakDurationTV = (TextView) findViewById(R.id.pomodoro_settings_break_duration_number);
        soundSwitcher = (Switch) findViewById(R.id.sound_switcher);
        vibrationSwitcher = (Switch) findViewById(R.id.vibration_switcher);

        workDurationTV.setOnClickListener(this);
        breakDurationTV.setOnClickListener(this);

        soundSwitcher.setOnCheckedChangeListener(this);
        vibrationSwitcher.setOnCheckedChangeListener(this);


        workDurationTV.setText("" + (workDuration / 60));
        breakDurationTV.setText("" + (breakDuration / 60));

        soundSwitcher.setChecked(soundState);
        vibrationSwitcher.setChecked(vibrationState);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();

        switch (id) {
            case R.id.sound_switcher: {
                if (isChecked) {
                    writePref(PomodoroActivity.APP_PREFERENCE_SOUND, true);
                }
                else {
                    writePref(PomodoroActivity.APP_PREFERENCE_SOUND, false);
                }

                break;
            }

            case R.id.vibration_switcher: {
                if (isChecked) {
                    writePref(PomodoroActivity.APP_PREFERENCE_VIBRATION, true);
                }
                else {
                    writePref(PomodoroActivity.APP_PREFERENCE_VIBRATION, false);
                }

                break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.pomodoro_settings_work_duration_number: {
                clickedWorkOrBreak = true;
                Log.d(TAG, "числопикер " + workDuration);
                DialogFragment df = NumberPickerDialog.newInstance(workDuration / 60); //convert seconds to minutes
                df.show(getSupportFragmentManager(), "numberPicker");
                break;
            }
            case R.id.pomodoro_settings_break_duration_number: {
                //break duration picker
                clickedWorkOrBreak = false;
                Log.d(TAG, "числопикер " + workDuration);
                DialogFragment df = NumberPickerDialog.newInstance(breakDuration / 60);
                df.show(getSupportFragmentManager(), "numberPicker");
                break;
            }
        }
    }

    @Override
    public void onDialogPositiveClick(int value) {
        Log.d(TAG, "Мы в Activity и получили " + value + " значение");

        if (clickedWorkOrBreak) { //clicked work
            workDuration = value * 60;

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(PomodoroActivity.APP_PREFERENCE_DEFAULT_WORK_DURATION,
                    workDuration); //convert minutes to seconds
            editor.apply();

            workDurationTV.setText("" + value);
        }
        else {
            breakDuration = value * 60;

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(PomodoroActivity.APP_PREFERENCE_DEFAULT_BREAK_DURATION,
                    breakDuration);
            editor.apply();

            breakDurationTV.setText("" + value);
        }
    }

    @Override
    public void updateSwitchers() {

    }

    private void writePref(final String key, boolean value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
}
