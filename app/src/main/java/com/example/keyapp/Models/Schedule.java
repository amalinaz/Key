package com.example.keyapp.Models;

import java.io.Serializable;

public class Schedule implements Serializable {
    private String ServiceName;
    private String username;
    private String date;
    private String time;
    private  String location;
    public  Schedule(){

    }

    public Schedule(String serviceName, String username, String date, String time, String location) {
        ServiceName = serviceName;
        this.username = username;
        this.date = date;
        this.time = time;
        this.location = location;
    }
    public void setServiceName(String serviceName) {
        ServiceName = serviceName;
    }
    public String getServiceName() {
        return ServiceName;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
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
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
}
