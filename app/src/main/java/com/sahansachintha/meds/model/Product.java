package com.sahansachintha.meds.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Product implements Serializable {

    private int id;

    private String name;
    private String title;
    private String description;

    //private int imageId;
    private String image; // Store Image URL instead of binary data

    //private Category category;
    private String categoryName;

    private String dosage; // Dosage information (e.g., 500mg)
    //private double weight; // Weight in grams or milliliters
    //private String unitOfMeasurement; // e.g., mg, g, ml

    private String price;
    //private BigDecimal price;

    private int quantity;
    private String manufacturer;
    private String expiryDate;

    public Product(int id, String name, String title, String description, String image, String categoryName, String dosage, String price, int quantity) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.description = description;
        this.image = image;
        this.categoryName = categoryName;
        this.dosage = dosage;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}
