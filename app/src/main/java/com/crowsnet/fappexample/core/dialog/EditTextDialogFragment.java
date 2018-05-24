package com.crowsnet.fappexample.core.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.crowsnet.fappexample.R;

import java.util.Calendar;

public class EditTextDialogFragment extends DialogFragment {

    private String text;
    private EditListener listener;

    public void setText(String text) {
        this.text = text;
    }

    public void setListener(EditListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(R.string.edit_dialog_title);

        final EditText editText = makeEditText();
        builder.setView(editText);

        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onEdited(editText.getText().toString());
            }
        });

        return builder.create();
    }

    private EditText makeEditText() {
        EditText editText = new EditText(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        editText.setLayoutParams(lp);
        editText.setText(text);
        return editText;
    }

    public interface EditListener {

        void onEdited(String text);
    }
}
