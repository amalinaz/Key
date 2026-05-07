package com.example.keyapp.Models;

import java.io.Serializable;
import com.google.firebase.Timestamp;
public class ChatMessage implements Serializable {
    private String senderId;
    private String receiverId;
    private String message;
    private Timestamp timestamp;
    private boolean read;
    public ChatMessage() {
    }

    public ChatMessage(String senderId, String receiverId, String message, Timestamp timestamp, boolean read) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = timestamp;
        this.read = read;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getMessage() {
        return message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return read;
    }
}
