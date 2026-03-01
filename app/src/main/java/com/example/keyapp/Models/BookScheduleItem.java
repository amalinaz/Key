package com.example.keyapp.Models;

public class BookScheduleItem {
    private String BAname;
    private String serviceName;
    private String date;
    private String time;

    public BookScheduleItem(String BAname, String serviceName, String date, String time) {
        this.BAname = BAname;
        this.serviceName = serviceName;
        this.date = date;
        this.time = time;
    }


    public String getBAname() {
        return BAname;
    }
    public void setBAname(String BAname) {
        this.BAname = BAname;
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

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
