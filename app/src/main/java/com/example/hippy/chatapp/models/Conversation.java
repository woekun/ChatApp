package com.example.hippy.chatapp.models;

import com.example.hippy.chatapp.Activities.UserList;

import java.util.Date;

public class Conversation {

    public static final int STATUS_SENDING = 0;
    public static final int STATUS_SENT = 1;
    public static final int STATUS_FAILED = 2;
    private int status = STATUS_SENT;
    private String message;
    private Date date;
    private String sender;

    public Conversation(String message, Date date, String sender) {
        this.message = message;
        this.date = date;
        this.sender = sender;
    }

    public Conversation() {
    }

    public boolean isSent() {
        return UserList.user.getUsername().equals(sender);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
