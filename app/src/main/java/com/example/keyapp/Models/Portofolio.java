package com.example.keyapp.Models;

public class Portofolio {
    public String imageUrl;
    public String storagePath;
    public String documentId;

    public Portofolio(String imageUrl, String storagePath, String documentId) {
        this.imageUrl = imageUrl;
        this.storagePath = storagePath;
        this.documentId = documentId;
    }
}
