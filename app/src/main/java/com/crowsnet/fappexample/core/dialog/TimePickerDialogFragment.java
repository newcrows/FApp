package com.crowsnet.fappexample.core.dialog;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import com.crowsnet.fappexample.Utils;

import java.util.Calendar;
import java.util.Locale;

public class TimePickerDialogFragment extends DialogFragment {

    private Calendar calendar;
    private TimePickerDialog.OnTimeSetListener listener;

    public void setTimestamp(int timestamp) {
        calendar = Calendar.getInstance(Locale.GERMANY);
        calendar.setTimeInMillis(Utils.unixToMillis(timestamp));
    }

    public void setListener(TimePickerDialog.OnTimeSetListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(
                getContext(),
                listener,
                calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(getActivity())
        );
    }
}
