package com.crowsnet.fappexample.core.provider;

import android.os.Bundle;
import android.util.Log;

import com.crowsnet.fappexample.core.pojo.User;
import com.crowsnet.fappexample.core.pojo.DateItem;
import com.crowsnet.fappexample.core.pojo.ItemType;
import com.crowsnet.fappexample.core.exception.DateProviderException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DateProvider implements ReferenceProvider.ReferenceListener {

    public static final String TAG = "DateProvider";

    public static final int FLAG_TYPES = 1;
    public static final int FLAG_DATES = 1 << 1;
    public static final int FLAG_REQUEST = 1 << 2;

    private static final String REF_TYPES = "/types";
    private static final String REF_DATES = "/dates";

    private static DateProvider instance;

    private ReferenceProvider provider;

    private User user;
    private String lastUserRef;
    private boolean isStarted;

    private List<DateItem> dateItems;
    private Map<String, ItemType> itemTypes;

    private List<DateListener> dateListeners;
    private Map<DateListener, List<DateFilter>> dateFilters;

    public synchronized static DateProvider getInstance() {
        if (instance == null)
            instance = new DateProvider();

        return instance;
    }

    private DateProvider() {
        provider = ReferenceProvider.getInstance();

        dateItems = new ArrayList<>();
        itemTypes = new HashMap<>();

        dateListeners = new ArrayList<>();
        dateFilters = new HashMap<>();
    }

    public void setUser(User user) {
        this.user = user;

        if (isStarted)
            restart();
    }

    public void restart() {
        stop();
        start();
    }

    public void start() {
        if (isStarted)
            return;
        isStarted = true;

        if (user != null) {
            provider.addReferenceListener(REF_TYPES, this);

            provider.addReferenceListener(getUserDatesRef(), this);
        }
    }

    public void stop() {
        if (!isStarted)
            return;
        isStarted = false;

        if (lastUserRef != null) {
            provider.removeReferenceListener(REF_TYPES, this);

            provider.removeReferenceListener(lastUserRef, this);
            lastUserRef = null;
        }
    }

    public void request(DateListener listener) {
        request(listener, FLAG_REQUEST);
    }

    public void request(DateListener listener, int flag) {
        Log.d(TAG, "request => " + listener + ", " + flag);
        notifyListener(listener, flag);
    }

    public void addDateListener(DateListener listener, DateFilter ... filters) {
        if (!dateListeners.contains(listener)) {
            dateListeners.add(listener);
            addDateFilters(listener, filters);
        }
    }

    public void removeDateListener(DateListener listener) {
        if (dateListeners.contains(listener)) {
            dateListeners.remove(listener);
            dateFilters.remove(listener);
        }
    }

    public void addDateFilters(DateListener listener, DateFilter ... filters) {
        if (filters == null)
            return;

        List<DateFilter> filterList = dateFilters.get(listener);

        if (filterList == null) {
            filterList = new ArrayList<>();
            dateFilters.put(listener, filterList);
        }

        filterList.addAll(Arrays.asList(filters));
    }

    public void removeDateFilters(DateListener listener, DateFilter ... filters) {
        if (filters == null)
            return;

        List<DateFilter> filterList = dateFilters.get(listener);

        if (filterList == null)
            return;

        for (DateFilter filter : filters)
            filterList.remove(filter);

        if (filterList.size() == 0)
            dateFilters.remove(listener);
    }

    public List<ItemType> listTypes() {
        List<ItemType> types = new ArrayList<>();

        for (Map.Entry<String, ItemType> entry : itemTypes.entrySet()) {
            types.add(entry.getValue());
        }

        return types;
    }

    public void addItem(DateItem dateItem) {
        DatabaseReference itemRef = getFirebaseReferenceForUser().push();
        dateItem.setKey(itemRef.getKey());

        itemRef.setValue(dateItem);
    }

    public void updateItem(DateItem dateItem) {
        getFirebaseReferenceForUser().child(dateItem.getKey()).setValue(dateItem);
    }

    public void removeItem(DateItem dateItem) {
        getFirebaseReferenceForUser().child(dateItem.getKey()).removeValue();
    }

    private DatabaseReference getFirebaseReferenceForUser() {
        return FirebaseDatabase.getInstance().getReference(lastUserRef);
    }

    @Override
    public void onAdded(String ref) {
        Log.d(TAG, "onAdded(" + ref +")");
    }

    @Override
    public void onRemoved(String ref) {
        Log.d(TAG, "onRemoved(" + ref +")");
    }

    @Override
    public void onValueEvent(String ref, DataSnapshot snapshot) {
        Log.d(TAG, "onValueEvent(" + ref + ", " + snapshot + ")");

        //TODO: process this in background
        int flag = ref.equals(REF_TYPES) ? FLAG_TYPES : FLAG_DATES;

        switch (flag) {
            case FLAG_TYPES:
                itemTypes = ItemType.mapFrom(snapshot);
                break;
            case FLAG_DATES:
                dateItems = DateItem.listFrom(snapshot);
                break;
            default:
                throw new DateProviderException("Invalid flag");
        }

        notifyDateListeners(flag);
    }

    @Override
    public void onError(String ref, DatabaseError error) {
        Log.d(TAG, "onError(" + ref + ", " + error + ")");
    }

    private void notifyDateListeners(int flag) {
        for (DateListener listener : dateListeners)
            notifyListener(listener, flag);
    }

    @SuppressWarnings("unchecked")
    private void notifyListener(DateListener listener, int flag) {
        //in main
        listener.onPrepare(flag);

        //TODO: in background
        List<DateItem> allowedItems = applyFiltersFor(listener);
        List<?> formattedItems = listener.onFormat(allowedItems, itemTypes, flag);

        //in main
        listener.onPublish(formattedItems, flag);
    }

    private List<DateItem> applyFiltersFor(DateListener listener) {
        List<DateFilter> filterList = dateFilters.get(listener);

        if (filterList == null)
            return dateItems;

        List<DateItem> allowedItems = new ArrayList<>();
        for (DateItem dateItem : dateItems) {
            if (!applyFilters(dateItem, filterList))
                allowedItems.add(dateItem);
        }

        return allowedItems;
    }

    private boolean applyFilters(DateItem item, List<DateFilter> filters) {
        for (DateFilter filter : filters) {
            ItemType type = itemTypes.get(item.getKey());
            if (filter.onFilter(item, type))
                return true;
        }

        return false;
    }

    private String getUserDatesRef() {
        lastUserRef = REF_DATES + "/" + user.getUid();
        return lastUserRef;
    }

    public interface DateListener<T> {

        void onPrepare(int flag);

        List<T> onFormat(List<DateItem> dateItems, Map<String, ItemType> itemTypes, int flag);

        void onPublish(List<T> formattedDates, int flag);
    }

    public interface DateFilter {

        boolean onFilter(DateItem dateItem, ItemType itemType);

        void restore(Bundle sis);

        void save(Bundle out);
    }
}
