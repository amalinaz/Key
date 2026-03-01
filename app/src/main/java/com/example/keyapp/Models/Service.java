package com.example.keyapp.Models;

import java.io.Serializable;
import java.sql.Time;
import java.util.List;

public class Service implements Serializable {
    private String id;
    private String ServiceName;
    private long ServicePrice;
    private String ServiceDesc;
    private String ServiceCategory;
    private String imgUrl;
    private String BAid; // beauty assistant id
    private int estTime;


    public Service(){

    }

    public Service(String id, String ServiceName, long ServicePrice, String ServiceDesc, String ServiceCategory, String imgUrl, String BAid, int estTime) {
        this.id = id;
        this.ServiceName = ServiceName;
        this.ServicePrice = ServicePrice;
        this.ServiceDesc = ServiceDesc;
        this.ServiceCategory = ServiceCategory;
        this.imgUrl = imgUrl;
        this.BAid = BAid;
        this.estTime = estTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceName() {
        return ServiceName;
    }

    public void setServiceName(String serviceName) {
        ServiceName = serviceName;
    }

    public long getServicePrice() {
        return ServicePrice;
    }

    public void setServicePrice(long servicePrice) {
        ServicePrice = servicePrice;
    }

    public String getServiceDesc() {
        return ServiceDesc;
    }

    public void setServiceDesc(String serviceDesc) {
        ServiceDesc = serviceDesc;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
    public String getBAid() {
        return BAid;
    }

    public void setBAid(String BAid) {
        this.BAid = BAid;
    }

    public String getServiceCategory() {
        return ServiceCategory;
    }

    public void setServiceCategory(String serviceCategory) {
        ServiceCategory = serviceCategory;
    }

    public int getEstTime() {
        return estTime;
    }

    public void setEstTime(int estTime) {
        this.estTime = estTime;
    }
}
