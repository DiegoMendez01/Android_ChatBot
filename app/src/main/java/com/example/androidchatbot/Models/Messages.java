package com.example.androidchatbot.Models;

public class Messages {

    private String username, phone, lastMessage, profilePicture, chatKey;
    private int unseenMessages;

    public Messages(){

    }

    public Messages(String username, String phone, String lastMessage, String profilePicture, int unseenMessages, String chatKey) {
        this.username = username;
        this.phone = phone;
        this.lastMessage = lastMessage;
        this.profilePicture = profilePicture;
        this.unseenMessages = unseenMessages;
        this.chatKey = chatKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public int getUnseenMessages() {
        return unseenMessages;
    }

    public void setUnseenMessages(int unseenMessages) {
        this.unseenMessages = unseenMessages;
    }

    public String getChatKey() {
        return chatKey;
    }

    public void setChatKey(String chatKey) {
        this.chatKey = chatKey;
    }
}
