package com.example.kursach.models;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String surname;
    private String name;
    private String login;
    private String password;
    private int role;

    public User(String surname, String name, String login, String password) {
        this.surname = surname;
        this.name = name;
        this.login = login;
        this.password = password;
    }

    public User(String surname, String name, String login, String password, int role) {
        this.surname = surname;
        this.name = name;
        this.login = login;
        this.password = password;
        this.role = role;
    }

    public User(int id, String surname, String name, String login, String password, int role) {
        this.id = id;
        this.surname = surname;
        this.name = name;
        this.login = login;
        this.password = password;
        this.role = role;
    }

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
