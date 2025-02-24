package com.sahansachintha.meds.model;

import java.io.Serializable;

public class Category implements Serializable {
    private String id;
    private String name;
    private int imgId = -1;
    private String image;

    public Category(String id, String name, int imgId) {
        this.id = id;
        this.name = name;
        this.imgId = imgId;
    }

    public Category(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    // Getters and setters for id, name, and imgId+
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
