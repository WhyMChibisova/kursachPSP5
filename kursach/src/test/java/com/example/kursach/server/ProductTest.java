package com.example.kursach.server;

import com.example.kursach.database.ConnectionDB;
import com.example.kursach.models.Product;
import com.example.kursach.models.User;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProductTest {

    @Test
    public void selectProduct() {
        Worker worker = new Worker(new ConnectionDB());
        Product product = new Product();
        product.setName("Хлеб");
        product.setPrice(2.49);
        Product result = new Product();
        result.setPrice(worker.selectProduct("Хлеб"));

        assertEquals(String.valueOf(product.getPrice()), String.valueOf(result.getPrice()));
    }
}