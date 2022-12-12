package com.example.kursach.models;

import java.io.Serializable;

public class Product implements Serializable {
    private int id;
    private String name;
    private double price;
    private int category_id;
    private double rating;

    public Product() {
    }

    public Product(String name, double price, int category_id, double rating) {
        this.name = name;
        this.price = price;
        this.category_id = category_id;
        this.rating = rating;
    }

    public Product(int id, String name, double price, int category_id, double rating) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category_id = category_id;
        this.rating = rating;
    }

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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
