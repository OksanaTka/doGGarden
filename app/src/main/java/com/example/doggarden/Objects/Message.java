package com.example.doggarden.Objects;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Message {
    private String message;
    private String name;
    private String uid;
    private String key;
    private long creationDate;
    private String time;
    private User user;


    public Message() {
        setTime();
    }

    public Message(String message, String name,String uid) {
        this.message = message;
        this.name = name;
        this.uid = uid;
    }
    public Message(String message, User user) {
        this.message = message;
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public Message setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getName() {
        return name;
    }

    public Message setName(String name) {
        this.name = name;
        return this;
    }

    public String getKey() {
        return key;
    }

    public Message setKey(String key) {
        this.key = key;
        return this;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public Message setCreationDate(long creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public Message setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getTime() {
        return time;
    }

    public void setTime() {
        DateFormat df = new SimpleDateFormat("HH:mm");
        time = df.format(Calendar.getInstance().getTime());
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
