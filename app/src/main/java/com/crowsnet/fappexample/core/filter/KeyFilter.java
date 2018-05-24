package com.crowsnet.fappexample.core.filter;

import android.os.Bundle;

import com.crowsnet.fappexample.core.pojo.DateItem;
import com.crowsnet.fappexample.core.pojo.ItemType;
import com.crowsnet.fappexample.core.provider.DateProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeyFilter implements DateProvider.DateFilter {

    public static final String TAG = "KeyFilter";

    private static final String SIS_ALLOWED_KEYS = TAG + ".allowedKeys";

    private List<String> allowedKeys;

    public KeyFilter(String ... allowedKeys) {
        this.allowedKeys = Arrays.asList(allowedKeys);
    }

    public void allowKey(String key) {
        if (!allowedKeys.contains(key))
            allowedKeys.add(key);
    }

    public void filterKey(String key) {
        allowedKeys.remove(key);
    }

    @Override
    public boolean onFilter(DateItem dateItem, ItemType itemType) {
        return !allowedKeys.contains(dateItem.getKey());
    }

    @Override
    public void restore(Bundle sis) {
        if (sis.containsKey(SIS_ALLOWED_KEYS))
            allowedKeys = sis.getStringArrayList(SIS_ALLOWED_KEYS);
    }

    @Override
    public void save(Bundle out) {
        out.putStringArrayList(SIS_ALLOWED_KEYS, (ArrayList<String>) allowedKeys);
    }
}
