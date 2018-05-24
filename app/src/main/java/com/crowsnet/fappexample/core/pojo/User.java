package com.crowsnet.fappexample.core.pojo;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    private String uid;
    private int role;

    public static User from(DataSnapshot snapshot) {
        User user = snapshot.getValue(User.class);
        user.uid = snapshot.getKey();

        return user;
    }

    public User() {
        //firebase constructor
    }

    public String getUid() {
        return uid;
    }

    public int getRole() {
        return role;
    }
}
