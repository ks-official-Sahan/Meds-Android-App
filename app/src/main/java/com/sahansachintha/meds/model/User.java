package com.sahansachintha.meds.model;

import java.io.Serializable;

public class User implements Serializable {
    private String firebaseUid;
    private String email;
    private String name;
    private String mobile;
    private String address;
    private String city;
    private String country;
    private String profileImage;
    private String location;

    public User(String firebaseUid, String email) {
        this.firebaseUid = firebaseUid;
        this.email = email;
    }

    public User(String firebaseUid, String email, String name, String mobile, String address, String city, String country, String profileImage, String location) {
        this.firebaseUid = firebaseUid;
        this.email = email;
        this.name = name;
        this.mobile = mobile;
        this.address = address;
        this.city = city;
        this.country = country;
        this.profileImage = profileImage;
        this.location = location;
    }

    public String getFirebaseUid() {
        return firebaseUid;
    }

    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
