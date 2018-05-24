package com.crowsnet.fappexample.core.provider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReferenceProvider {

    public static final String TAG = "ReferenceProvider";

    private static final int NOT_FOUND = -1;

    private static ReferenceProvider instance;

    private FirebaseDatabase database;

    private Map<String, List<Entry>> entries;

    public synchronized static ReferenceProvider getInstance() {
        if (instance == null)
            instance = new ReferenceProvider();

        return instance;
    }

    private ReferenceProvider() {
        database = FirebaseDatabase.getInstance();

        entries = new HashMap<>();
    }

    public void addReferenceListener(String ref, ReferenceListener listener) {
        List<Entry> entriesForRef = entries.get(ref);

        if (entriesForRef == null) {
            entriesForRef = new ArrayList<>();
            entries.put(ref, entriesForRef);
        }

        int idx = entryIndexOf(entriesForRef, listener);
        if (idx == NOT_FOUND) {
            ValueEventListener valueListener = makeValueListener(ref, listener);
            registerValueListener(ref, valueListener);

            entriesForRef.add(makeEntry(listener, valueListener));

            listener.onAdded(ref);
        }
    }

    public void removeReferenceListener(String ref, ReferenceListener listener) {
        List<Entry> entriesForRef = entries.get(ref);
        if (entriesForRef == null)
            return;

        int idx = entryIndexOf(entriesForRef, listener);
        if (idx != NOT_FOUND) {
            Entry removedEntry = entriesForRef.remove(idx);
            unregisterValueListener(ref, removedEntry.valueListener);

            listener.onRemoved(ref);
        }

        if (entriesForRef.size() == 0)
            entries.remove(ref);
    }

    private void registerValueListener(String ref, ValueEventListener listener) {
        getReference(ref).addValueEventListener(listener);
    }

    private void unregisterValueListener(String ref, ValueEventListener listener) {
        getReference(ref).removeEventListener(listener);
    }

    private ValueEventListener makeValueListener(final String ref, final ReferenceListener listener) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onValueEvent(ref, dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onError(ref, databaseError);
            }
        };
    }

    private Entry makeEntry(ReferenceListener listener, ValueEventListener valueListener) {
        return new Entry(listener, valueListener);
    }

    private int entryIndexOf(List<Entry> entries, ReferenceListener listener) {
        for (int c = 0; c < entries.size(); c++) {
            if (entries.get(c).referenceListener == listener)
                return c;
        }

        return NOT_FOUND;
    }

    private DatabaseReference getReference(String ref) {
        return database.getReference(ref);
    }

    private static class Entry {

        private ReferenceListener referenceListener;
        private ValueEventListener valueListener;

        private Entry(ReferenceListener referenceListener, ValueEventListener valueListener) {
            this.referenceListener = referenceListener;
            this.valueListener = valueListener;
        }
    }

    public interface ReferenceListener {

        void onAdded(String ref);

        void onRemoved(String ref);

        void onValueEvent(String ref, DataSnapshot snapshot);

        void onError(String ref, DatabaseError error);
    }
}
