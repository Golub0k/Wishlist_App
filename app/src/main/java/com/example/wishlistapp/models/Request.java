package com.example.wishlistapp.models;

public class Request {
    String senderId;
    String recipientId;
    String state;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    String key;

    public Request(String senderId, String recipientId, String state) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.state = state;
    }

    public Request() {
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
