package com.dorfer.scheduler_pomodoro.Dialogs;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(int hourOfDay, int minute);
        public void updateSwitchers();
    }

    private NoticeDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstaceState) {
        int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);

        mListener = (TimePickerFragment.NoticeDialogListener)getActivity();
        return new TimePickerDialog(getActivity(), this, hourOfDay, minute, true);
    }

    @Override
    public void onStop() {
        super.onStop();
        mListener.updateSwitchers();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mListener.onDialogPositiveClick(hourOfDay, minute);
        mListener.updateSwitchers();
    }
}
