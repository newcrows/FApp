package com.crowsnet.fappexample.core.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.crowsnet.fappexample.R;

public class SingleChoiceDialogFragment extends DialogFragment {

    private String[] choices;
    private int selected;

    private SingleChoiceListener listener;

    public void setSingleChoiceItems(String[] choices, int selected) {
        this.choices = choices;
        this.selected = selected;
    }

    public void setListener(SingleChoiceListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(R.string.select_dialog_title);

        builder.setSingleChoiceItems(choices, selected, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selected = which;
            }
        });

        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onChoiceSet(selected);
            }
        });

        return builder.create();
    }

    public interface SingleChoiceListener {

        void onChoiceSet(int which);
    }
}
