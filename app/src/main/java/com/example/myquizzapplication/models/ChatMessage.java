package com.example.myquizzapplication.models;

public class ChatMessage {
    public String role; // "user" hoặc "model"
    public String content;

    public ChatMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }
}

