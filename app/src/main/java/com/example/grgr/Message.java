package com.example.grgr;

public class Message {
    private String userId;
    private String text;

    public Message() {
        // Default constructor required for Firebase
    }

    public Message(String userId, String text) {
        this.userId = userId;
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public String getText() {
        return text;
    }
}

