package com.example.keyapp.Models;

public class User {
    private String userID, userName, email, password, profileImageUrl;
    private Integer userlvl;

    public User(String userID, String userName, String email, String password, Integer userlvl, String profileImageUrl) {
        this.userID = userID;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.userlvl = userlvl;
        this.profileImageUrl = profileImageUrl;
    }

    public User() {

    }
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Integer getUserlvl() {
        return userlvl;
    }
    public void setUserlvl(Integer userlvl) {
        this.userlvl = userlvl;
    }
}
