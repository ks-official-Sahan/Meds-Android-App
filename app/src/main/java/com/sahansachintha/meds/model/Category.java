package com.sahansachintha.meds.model;

import java.io.Serializable;

public class Category implements Serializable {
    private int id;
    private String name;
    private int imgId;

    public Category(int id, String name, int imgId) {
        this.id = id;
        this.name = name;
        this.imgId = imgId;
    }

    // Getters and setters for id, name, and imgId+
    public int getId() {
        return id;
    }

    public void setId(int id) {
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
}
