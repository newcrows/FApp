package com.crowsnet.fappexample.agenda;

import android.graphics.Color;

import com.crowsnet.fappexample.Utils;
import com.crowsnet.fappexample.core.pojo.DateItem;
import com.crowsnet.fappexample.core.pojo.ItemType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AgendaItem {

    public String key, typeKey, date, time, label, duration, text;
    public int bk, col, timestamp;
    public DateItem dateItem;

    public static AgendaItem from(Calendar calendar, DateItem dateItem, ItemType itemType) {
        int timestamp = dateItem.getTimestamp();
        calendar.setTimeInMillis(Utils.unixToMillis(dateItem.getTimestamp()));

        String key = dateItem.getKey();
        String typeKey = itemType.getKey();
        String date = makeDate(calendar);
        String time = makeTime(calendar);
        String label = itemType.getLabel();
        String duration = itemType.getDuration() + " Minuten";
        String text = dateItem.getText();
        int bk = Color.parseColor(itemType.getBk());
        int col = Color.parseColor(itemType.getCol());

        return new AgendaItem(
                key,
                typeKey,
                date,
                time,
                label,
                duration,
                text,
                bk,
                col,
                timestamp,
                dateItem
        );
    }

    public static List<AgendaItem> listFrom(List<DateItem> dateItems, Map<String, ItemType> itemTypes) {
        Calendar calendar = Calendar.getInstance(Locale.GERMANY);
        List<AgendaItem> items = new ArrayList<>();

        for (DateItem dateItem : dateItems) {
            ItemType itemType = itemTypes.get(dateItem.getType());

            if (itemType == null)
                continue;

            items.add(AgendaItem.from(calendar, dateItem, itemType));
        }

        return items;
    }

    public boolean isComplete() {
        return timestamp != 0 &&
                typeKey != null;
    }

    public AgendaItem() {
        date = "Wähle Datum";
        time = "Wähle Uhrzeit";
        label = "Wähle Typ";
        duration = "?";
        text = "Bearbeiten";
        bk = Color.parseColor("gray");
        col = Color.parseColor("white");
        timestamp = 0;
    }

    private AgendaItem(String key, String typeKey, String date, String time, String label, String duration, String text, int bk, int col, int timestamp, DateItem dateItem) {
        this.key = key;
        this.typeKey = typeKey;
        this.date = date;
        this.time = time;
        this.label = label;
        this.duration = duration;
        this.text = text;
        this.bk = bk;
        this.col = col;
        this.timestamp = timestamp;
        this.dateItem = dateItem;
    }

    public static String makeDate(Calendar calendar) {
        int month = calendar.get(Calendar.MONTH) + 1;

        return calendar.get(Calendar.DAY_OF_MONTH) + "." +
                (month < 10 ? "0" : "") + month + "." +
                calendar.get(Calendar.YEAR);
    }

    public static String makeTime(Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return (hour < 10 ? "0" : "") + hour + ":" + (minute < 10 ? "0" : "") + minute + " Uhr";
    }
}
