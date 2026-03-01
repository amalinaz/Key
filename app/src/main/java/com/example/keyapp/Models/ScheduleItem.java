package com.example.keyapp.Models;

public class ScheduleItem {

    private String time;
    private int estimatedTime;
    private String providerName;

    public ScheduleItem(String time, int estimatedTime, String providerName) {
        this.time = time;
        this.estimatedTime = estimatedTime;
        this.providerName = providerName;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
