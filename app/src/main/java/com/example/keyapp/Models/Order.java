package com.example.keyapp.Models;

import java.io.Serializable;

public class Order implements Serializable {
    private String orderId;
    private String userId;
    private String username;
    private String serviceName;
    private String selectedDate;
    private String selectedTime;
    private double servicePrice;
    private String status;
    private String BAid;
    private String BAName;
    private String location;
    public Order() {

    }

    public Order(String orderId, String userId, String username, String serviceName, String selectedDate,
                 String selectedTime, double servicePrice, String status, String BAid, String BAName, String location) {
        this.orderId = orderId;
        this.userId = userId;
        this.username = username;
        this.serviceName = serviceName;
        this.selectedDate = selectedDate;
        this.selectedTime = selectedTime;
        this.servicePrice = servicePrice;
        this.status = status;
        this.BAid = BAid;
        this.BAName = BAName;
        this.location = location;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public String getSelectedTime() {
        return selectedTime;
    }

    public void setSelectedTime(String selectedTime) {
        this.selectedTime = selectedTime;
    }

    public double getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(double servicePrice) {
        this.servicePrice = servicePrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBAid() {
        return BAid;
    }

    public void setBAid(String BAid) {
        this.BAid = BAid;
    }

    public String getBAName() {
        return BAName;
    }

    public void setBAName(String BAName) {
        this.BAName = BAName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
