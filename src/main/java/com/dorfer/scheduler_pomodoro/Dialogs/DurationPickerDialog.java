package com.dorfer.scheduler_pomodoro.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dorfer.scheduler_pomodoro.R;

public class DurationPickerDialog extends Dialog implements View.OnClickListener {
    private TextView hoursTextView, minutesTextView;
    private ImageButton addHourBtn, addMinuteBtn, reduceHourBtn, reduceMinuteBtn;
    private Button doneBtn, cancelBtn;
    private AppCompatActivity activity;
    private int hours = 0, minutes = 0;

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(int hourOfDay, int minute);
        public void updateSwitchers();
    }

    private NoticeDialogListener mListener;

    public DurationPickerDialog(AppCompatActivity activity) {
        super(activity);

        this.activity = activity;
        mListener = (DurationPickerDialog.NoticeDialogListener)activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.duration_dialog);

        hoursTextView = (TextView) findViewById(R.id.duration_hours);
        minutesTextView = (TextView) findViewById(R.id.duration_minutes);
        addHourBtn = (ImageButton) findViewById(R.id.add_hour);
        reduceHourBtn = (ImageButton) findViewById(R.id.reduce_hour);
        addMinuteBtn = (ImageButton) findViewById(R.id.add_minute);
        reduceMinuteBtn = (ImageButton) findViewById(R.id.reduce_minute);
        doneBtn = (Button) findViewById(R.id.done_dialog);
        cancelBtn = (Button) findViewById(R.id.cancel_dialog);

        addHourBtn.setOnClickListener(this);
        reduceHourBtn.setOnClickListener(this);
        addMinuteBtn.setOnClickListener(this);
        reduceMinuteBtn.setOnClickListener(this);
        doneBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.add_hour: {
                Log.d("myTag", "add hour");

                ++hours;
                checkHours();
                setHoursTextView(hours);

                break;
            }
            case R.id.reduce_hour: {
                Log.d("myTag", "reduce hour");

                --hours;
                checkHours();
                setHoursTextView(hours);

                break;
            }
            case R.id.add_minute: {
                Log.d("myTag", "add minute");

                minutes += 5;
                checkMinutes();
                setMinutesTextView(minutes);

                break;
            }
            case R.id.reduce_minute: {
                Log.d("myTag", "reduce minute");

                minutes -= 5;
                checkMinutes();
                setMinutesTextView(minutes);

                break;
            }
            case R.id.cancel_dialog: {
                Log.d("myTag", "cancel clicked!");

                mListener.updateSwitchers();

                dismiss();
                break;
            }
            case R.id.done_dialog: {
                Log.d("myTag", "done clicked!");

                mListener.onDialogPositiveClick(hours, minutes);
                mListener.updateSwitchers();

                dismiss();

                break;
            }
            default: {
                mListener.updateSwitchers();
            }
        }
    }

    private void checkMinutes() {
        if (minutes < 0) {
            minutes += 60;
        }
        else if (minutes > 59) {
            minutes -= 60;
        }
    }

    private void checkHours() {
        if (hours < 0) {
            hours += 24;
        }
        else if (hours > 23) {
            hours -= 24;
        }
    }

    private void setHoursTextView(int hours) {
        hoursTextView.setText(getTimeString(hours));
    }

    private void setMinutesTextView(int minutes) {
        minutesTextView.setText(getTimeString(minutes));
    }

    private String getTimeString(int hoursOrMinutes) {
        String strHoursOrMinutes;

        if (hoursOrMinutes < 10) {
            strHoursOrMinutes = "0" + hoursOrMinutes;
        }
        else {
            strHoursOrMinutes = "" + hoursOrMinutes;
        }

        return strHoursOrMinutes;
    }

    @Override
    public void onStop() {
        super.onStop();
        mListener.updateSwitchers();
    }

}
