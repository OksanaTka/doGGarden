package com.example.doggarden.Objects;

import java.io.Serializable;

public class User implements Serializable {

    private  String uid ;
    private  String ownerName = "";
    private  String dogName = "";
    private  String phoneNumber = "";
    private  String status = "";
    private  String favoritePark = "";
    private double lastLng=0.0;
    private double lastLat=0.0;
    private String breed = "";
    private String dogGender = "";
    private String age ;
    private String about="";
    private String imageUrl;



    public User() {
    }

    public String getUid() {
        return uid;
    }

    public User setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public User setOwnerName(String ownerName) {
        this.ownerName = ownerName;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public User setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getFavoritePark() {
        return favoritePark;
    }

    public User setFavoritePark(String favoritePark) {
        this.favoritePark = favoritePark;
        return this;
    }

    public String getDogName() {
        return dogName;
    }

    public User setDogName(String dogName) {
        this.dogName = dogName;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public User setStatus(String status) {
        this.status = status;
        return this;
    }

    public double getLastLng() {
        return lastLng;
    }

    public User setLastLng(double lastLng) {
        this.lastLng = lastLng;
        return this;
    }

    public double getLastLat() {
        return lastLat;
    }

    public User setLastLat(double lastLat) {
        this.lastLat = lastLat;
        return this;
    }

    public String getBreed() {
        return breed;
    }

    public User setBreed(String breed) {
        this.breed = breed;
        return this;
    }

    public String getDogGender() {
        return dogGender;
    }

    public User setDogGender(String dogGender) {
        this.dogGender = dogGender;
        return this;
    }

    public String getAge() {
        return age;
    }

    public User setAge(String age) {
        this.age = age;
        return this;
    }

    public String getAbout() {
        return about;
    }

    public User setAbout(String about) {
        this.about = about;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public User setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }
}
