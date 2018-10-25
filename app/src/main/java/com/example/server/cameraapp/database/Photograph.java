package com.example.server.cameraapp.database;

public class Photograph {
    // fields
    private int photoID;
    private String photoPath;
    private Float locLat;
    private Float locLong;
    private Long photoDate;
    private String photoCaption;

    public Photograph() {
    }

    public Photograph(String photoPath, Float locLat, Float locLong, Long date, String photoCaption) {
        this.photoPath = photoPath;
        this.locLat = locLat;
        this.locLong = locLong;
        this.photoDate = date;
        this.photoCaption = photoCaption;
    }

    public void setID(int id) {
        this.photoID = id;
    }

    public int getID() {
        return this.photoID;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getPhotoPath() {
        return this.photoPath;
    }

    public void setLocLat(float locLat) {
        this.locLat = locLat;
    }

    public float getLocLat() {
        return this.locLat;
    }

    public void setLocLong(float locLong) {
        this.locLong = locLong;
    }

    public float getLocLong() {
        return this.locLong;
    }

    public void setPhotoDate(Long photoDate) {
        this.photoDate = photoDate;
    }

    public Long getPhotoDate() {
        return this.photoDate;
    }

    public void setPhotoCaption(String photoCaption) {
        this.photoCaption = photoCaption;
    }

    public String getPhotoCaption() {
        return this.photoCaption;
    }
}
