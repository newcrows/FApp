package com.crowsnet.fappexample.core.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.crowsnet.fappexample.Utils;

import java.util.Calendar;
import java.util.Locale;

public class DatePickerDialogFragment extends DialogFragment {

    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener listener;

    public void setTimestamp(int timestamp) {
        calendar = Calendar.getInstance(Locale.GERMANY);
        calendar.setTimeInMillis(Utils.unixToMillis(timestamp));
    }

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(
                getContext(),
                listener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
    }
}
