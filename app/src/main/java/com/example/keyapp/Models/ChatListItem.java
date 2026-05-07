package com.example.keyapp.Models;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class ChatListItem implements Serializable {

    private String chatId;
    private String receiverId;
    private String receiverName;
    private String receiverProfileImage;
    private String lastMessage;
    private Timestamp timestamp;

    public ChatListItem(){

    }

    public ChatListItem(String chatId, String receiverId, String receiverName, String lastMessage, String receiverProfileImage, Timestamp timestamp) {
        this.chatId = chatId;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.lastMessage = lastMessage;
        this.receiverProfileImage = receiverProfileImage;
        this.timestamp = timestamp;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getReceiverProfileImage() {
        return receiverProfileImage;
    }

    public void setReceiverProfileImage(String receiverProfileImage) {
        this.receiverProfileImage = receiverProfileImage;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
