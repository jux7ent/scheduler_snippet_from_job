package com.dorfer.scheduler_pomodoro.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.NumberPicker;

import com.dorfer.scheduler_pomodoro.R;

public class NumberPickerDialog extends DialogFragment {

    private static final String TAG = "myTag" + NumberPickerDialog.class.toString();
    private AppCompatActivity activity;

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(int value);
        public void updateSwitchers();
    }

    private NoticeDialogListener mListener;

    public static NumberPickerDialog newInstance(int value) {
        NumberPickerDialog f = new NumberPickerDialog();

        Bundle args = new Bundle();
        args.putInt("value", value);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final NumberPicker numberPicker = new NumberPicker(getActivity());

        mListener = (NoticeDialogListener)getActivity();

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(120);
        numberPicker.setValue(getArguments().getInt("value", 25));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.duration_picker_title);
        builder.setMessage(R.string.duration_picker_message);

        builder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onDialogPositiveClick(numberPicker.getValue());
            }
        });
/*
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }); */

        builder.setView(numberPicker);
        return builder.create();
    }
}
