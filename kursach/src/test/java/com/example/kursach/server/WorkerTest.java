package com.example.kursach.server;

import com.example.kursach.database.ConnectionDB;
import com.example.kursach.models.User;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WorkerTest {

    @Test
    public void findUser() {
        Worker worker = new Worker(new ConnectionDB());
        User user = new User();
        user.setLogin("masha");
        user.setName("Мария");
        User result = new User();
        result.setName(worker.findUser("masha"));

        assertEquals(user.getName(), result.getName());
    }
}