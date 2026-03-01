package com.example.keyapp.Models;

public class BAprofile {
    String BAid;
    String BAname;
    double distance;
    Float rating;
    String photoUrl;
    long minPrice;

    public BAprofile() {

    }
    public BAprofile(String BAid, String BAname, String photoUrl, long minPrice, double distance) {
        this.BAid = BAid;
        this.BAname = BAname;
        this.photoUrl = photoUrl;
        this.minPrice = minPrice;
        this.distance = distance;
        this.rating = null;
    }
    public String getBAid() {
        return BAid;
    }
    public void setBAid(String BAid) {
        this.BAid = BAid;
    }
    public String getBAname() {
        return BAname;
    }
    public void setBAname(String BAname) {
        this.BAname = BAname;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
    public long getMinPrice() {
        return minPrice;
    }
    public void setMinPrice(long minPrice) {
        this.minPrice = minPrice;
    }
    public double getDistance() {
        return distance;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }
}
