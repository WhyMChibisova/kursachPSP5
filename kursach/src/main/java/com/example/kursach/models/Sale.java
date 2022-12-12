package com.example.kursach.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

public class Sale implements Serializable {
    private int id;
    private int product_id;
    private LocalDate date;
    private int count;

    public Sale() {
    }

    public Sale(int product_id, LocalDate date, int count) {
        this.product_id = product_id;
        this.date = date;
        this.count = count;
    }

    public Sale(int id, int product_id, LocalDate date, int count) {
        this.id = id;
        this.product_id = product_id;
        this.date = date;
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
