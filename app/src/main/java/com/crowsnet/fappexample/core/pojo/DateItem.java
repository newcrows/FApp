package com.crowsnet.fappexample.core.pojo;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class DateItem {

    private int timestamp;
    private String type;

    private String key;
    private String text;

    public static DateItem from(DataSnapshot childSnapshot) {
        DateItem dateItem = childSnapshot.getValue(DateItem.class);
        dateItem.setKey(childSnapshot.getKey());

        return dateItem;
    }

    public static List<DateItem> listFrom(DataSnapshot snapshot) {
        List<DateItem> items = new ArrayList<>();

        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            items.add(DateItem.from(dataSnapshot));
        }

        return items;
    }

    public DateItem() {
        //firebase constructor
    }

    public DateItem(int timestamp, String type, String text) {
        this.timestamp = timestamp;
        this.type = type;
        this.text = text;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "[timestamp: " + timestamp + ", type: " + type + "]";
    }
}
