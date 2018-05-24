package com.crowsnet.fappexample.core.filter;

import android.os.Bundle;
import android.util.Log;

import com.crowsnet.fappexample.core.pojo.DateItem;
import com.crowsnet.fappexample.core.pojo.ItemType;
import com.crowsnet.fappexample.core.provider.DateProvider;

import java.util.Arrays;
import java.util.List;

public class TypeFilter implements DateProvider.DateFilter {

    public static final String TAG = "TypeFilter";

    private static final String SIS_KEYS = TAG + ".keys";
    private static final String SIS_LABELS = TAG + ".labels";
    private static final String SIS_FLAGS = TAG + ".flags";

    private static final int NOT_FOUND = -1;

    private String[] keys, labels;
    private boolean[] flags;

    public void setTypes(List<ItemType> types) {
        Log.d(TAG, "setTypes(" + types.size() + ")");

        String[] newKeys = new String[types.size()];
        labels = new String[types.size()];

        for (int c = 0; c < types.size(); c++) {
            ItemType type = types.get(c);

            newKeys[c] = type.getKey();
            labels[c] = type.getLabel();
        }

        //only clear filter state if types actually changed
        if (!Arrays.equals(keys, newKeys)) {
            flags = new boolean[types.size()];
            keys = newKeys;
        }
    }

    public String[] getKeys() {
        return keys;
    }

    public String[] getLabels() {
        return labels;
    }

    public boolean[] getFlags() {
        return flags;
    }

    @Override
    public boolean onFilter(DateItem dateItem, ItemType itemType) {
        if (itemType == null)
            return true;

        int idx = indexOf(itemType.getKey());
        if (idx == NOT_FOUND)
            return true;

        return flags[idx];
    }

    @Override
    public void restore(Bundle sis) {
        Log.d(TAG, "restore");
        if (sis == null) {
            setTypes(DateProvider.getInstance().listTypes());
        } else {
            keys = sis.getStringArray(SIS_KEYS);
            labels = sis.getStringArray(SIS_LABELS);
            flags = sis.getBooleanArray(SIS_FLAGS);
        }
    }

    @Override
    public void save(Bundle out) {
        Log.d(TAG, "save");
        out.putStringArray(SIS_KEYS, keys);
        out.putStringArray(SIS_LABELS, labels);
        out.putBooleanArray(SIS_FLAGS, flags);
    }

    private int indexOf(String key) {
        if (key == null || keys == null)
            return NOT_FOUND;

        for (int c = 0; c < keys.length; c++) {
            if (key.equals(keys[c]))
                return c;
        }

        return NOT_FOUND;
    }
}
