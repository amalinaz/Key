package com.example.keyapp.Models;

public class Portofolio {

    private String BAid;
    private String imageUrl;
    private String storagePath;
    private String documentId;

    public Portofolio(String BAid, String imageUrl, String storagePath, String documentId) {
        this.BAid = BAid;
        this.imageUrl = imageUrl;
        this.storagePath = storagePath;
        this.documentId = documentId;
    }
    public String getBAid() {
        return BAid;
    }

    public void setBAid(String BAid) {
        this.BAid = BAid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
