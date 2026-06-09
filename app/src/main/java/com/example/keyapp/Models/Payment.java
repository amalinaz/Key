package com.example.keyapp.Models;

import java.io.Serializable;

public class Payment implements Serializable {

    private String paymentMethod;
    private double price;
    private long timestamp;

    public Payment(){}
    public Payment(String paymentMethod, double price, long timestamp) {

        this.paymentMethod = paymentMethod;
        this.price = price;
        this.timestamp = timestamp;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
