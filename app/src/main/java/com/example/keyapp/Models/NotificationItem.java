package com.example.keyapp.Models;

public class NotificationItem {
    private String title;
    private String message;
    private String type;
    private String orderId;

    public NotificationItem(String title, String message, String type, String orderId) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.orderId = orderId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}