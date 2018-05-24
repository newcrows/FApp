package com.crowsnet.fappexample.core.pojo;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class ItemType {

    private String bk, col, label;
    private int duration;
    private String key;

    public static ItemType from(DataSnapshot childSnapshot) {
        ItemType itemType = childSnapshot.getValue(ItemType.class);
        itemType.setKey(childSnapshot.getKey());

        return itemType;
    }

    public static Map<String, ItemType> mapFrom(DataSnapshot snapshot) {
        Map<String, ItemType> types = new HashMap<>();

        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
            ItemType itemType = ItemType.from(childSnapshot);
            types.put(itemType.getKey(), itemType);
        }

        return types;
    }

    public ItemType() {
        //firebase constructor
    }

    public ItemType(String bk, String col, String label, int duration) {
        this.bk = bk;
        this.col = col;
        this.label = label;
        this.duration = duration;
    }

    public String getBk() {
        return bk;
    }

    public String getCol() {
        return col;
    }

    public String getLabel() {
        return label;
    }

    public int getDuration() {
        return duration;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "[bk: " + bk + ", col: " + col + ", label: " + label + ", duration: " + duration + "]";
    }
}
