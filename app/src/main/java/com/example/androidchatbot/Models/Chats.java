package com.example.androidchatbot.Models;

public class Chats {
    private String phone, username, message, date, time;

    public Chats()
    {

    }

    public Chats(String phone, String username, String message, String date, String time) {
        this.phone = phone;
        this.username = username;
        this.message = message;
        this.date = date;
        this.time = time;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
