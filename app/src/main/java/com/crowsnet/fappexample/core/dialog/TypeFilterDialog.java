package com.crowsnet.fappexample.core.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.crowsnet.fappexample.R;
import com.crowsnet.fappexample.core.filter.TypeFilter;
import com.crowsnet.fappexample.core.provider.DateProvider;

public class TypeFilterDialog {

    public static AlertDialog wrap(Context context, final DateProvider.DateListener listener, final TypeFilter typeFilter) {
        String[] labels = typeFilter.getLabels();
        final boolean[] flags = inverse(typeFilter.getFlags());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(R.string.filter_dialog_title);

        builder.setMultiChoiceItems(labels, flags, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                typeFilter.getFlags()[which] = !isChecked;
            }
        });

        builder.setPositiveButton(R.string.dialog_ok, null);

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                DateProvider.getInstance().request(listener);
            }
        });

        return builder.create();
    }

    private static boolean[] inverse(boolean[] booleans) {
        if (booleans == null)
            return null;

        boolean[] inversed = new boolean[booleans.length];
        for (int c = 0; c < inversed.length; c++) {
            inversed[c] = !booleans[c];
        }

        return inversed;
    }
}
