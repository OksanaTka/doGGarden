package com.example.doggarden.Objects;

public class Profile {

    private String dogName = "";
    private String ownerName ="";
    private String breed = "";
    private String about = "";
    private  int gender ;


    public Profile() {
    }

    public String getDogName() {
        return dogName;
    }

    public Profile setDogName(String dogName) {
        this.dogName = dogName;
        return this;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Profile setOwnerName(String ownerName) {
        this.ownerName = ownerName;
        return this;
    }

    public String getBreed() {
        return breed;
    }

    public Profile setBreed(String breed) {
        this.breed = breed;
        return this;
    }

    public String getAbout() {
        return about;
    }

    public Profile setAbout(String about) {
        this.about = about;
        return this;
    }

    public int getGender() {
        return gender;
    }

    public Profile setGender(int gender) {
        this.gender = gender;
        return this;
    }
}
