package com.crowsnet.fappexample.core.filter;

import android.os.Bundle;
import android.util.Log;

import com.crowsnet.fappexample.core.pojo.DateItem;
import com.crowsnet.fappexample.core.pojo.ItemType;
import com.crowsnet.fappexample.core.provider.DateProvider;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateRangeFilter implements DateProvider.DateFilter {

    public static final String TAG = "DateRangeFilter";

    private static final int HOURS_BEFORE = 400;
    private static final int DAYS_AFTER = 14;

    private int unixStart = Integer.MIN_VALUE;
    private int unixEnd = Integer.MAX_VALUE;

    public void setStartDate(Date date) {
        int unix = toUnix(date);
        if (unix > unixEnd)
            throw new IllegalArgumentException("Start date must not be after end date");

        unixStart = unix;
    }

    public void setEndDate(Date date) {
        int unix = toUnix(date);
        if (unix < unixStart)
            throw new IllegalArgumentException("End date must not be before start date");

        unixEnd = unix;
    }

    private int toUnix(Date date) {
        return  (int) (date.getTime() / 1000);
    }

    @Override
    public boolean onFilter(DateItem dateItem, ItemType itemType) {
        int unix = dateItem.getTimestamp();

        Log.d(TAG, "unix=" + unix + ", allowed=" + !(unix < unixStart || unix > unixEnd));

        return unix < unixStart || unix > unixEnd;
    }

    @Override
    public void restore(Bundle sis) {
        Calendar date = Calendar.getInstance(Locale.GERMANY);
        date.add(Calendar.HOUR_OF_DAY, -HOURS_BEFORE);

        setStartDate(date.getTime());

        date.add(Calendar.HOUR_OF_DAY, HOURS_BEFORE);
        date.add(Calendar.DAY_OF_YEAR, DAYS_AFTER);

        setEndDate(date.getTime());
    }

    @Override
    public void save(Bundle out) {

    }
}
