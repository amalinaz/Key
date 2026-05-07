package com.example.keyapp.Models;

public class BAprofile {
    String BAid;
    String BAname;
    double distance;
    double rating;
    String photoUrl;
    long minPrice;
    Double experience;
    private double score;
    public BAprofile() {

    }
    public BAprofile(String BAid, String BAname, String photoUrl, long minPrice, double distance, double rating, Double experience) {
        this.BAid = BAid;
        this.BAname = BAname;
        this.photoUrl = photoUrl;
        this.minPrice = minPrice;
        this.distance = distance;
        this.rating = rating;
        this.experience = experience;
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
    public double getRating() {
        return rating;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }
    public Double getExperience() {
        return experience;
    }
    public void setExperience(Double experience) {
        this.experience = experience;
    }
    public double getScore() {
        return score;
    }
    public void setScore(double score) {
        this.score = score;
    }

}
