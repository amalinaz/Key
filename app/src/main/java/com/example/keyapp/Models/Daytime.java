package com.example.keyapp.Models;

import java.util.List;

public class Daytime {
    private String day;
    private List<String> time;

    public Daytime(String day, List<String> time) {
        this.day = day;
        this.time = time;
    }



    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public List<String> getTime() {
        return time;
    }

    public void setTime(List<String> time) {
        this.time = time;
    }
}
