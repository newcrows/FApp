package com.crowsnet.fappexample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class Utils {

    public static void setColor(TextView textView, int color) {
        textView.setTextColor(color);
    }

    public static void setTextAndColor(TextView textView, String text, int color) {
        textView.setText(text);
        textView.setTextColor(color);
    }

    public static void launchActivity(Context context, Class<? extends Activity> clazz, Bundle extras) {
        Intent intent = new Intent(context, clazz);
        intent.putExtras(extras);

        context.startActivity(intent);
    }

    public static void inflate(int layoutId, ViewGroup group) {
        LayoutInflater.from(group.getContext()).inflate(layoutId, group, true);
    }

    public static long unixToMillis(int unix) {
        return ((long) unix) * 1000L;
    }

    public static int millisToUnix(long millis) {
        return (int) (millis / 1000);
    }
}
