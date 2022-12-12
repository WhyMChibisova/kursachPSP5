package com.example.kursach.database;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;

public class ConnectionDB extends Configs {

    private static ConnectionDB instance;
    private Connection connect;
    private Statement statement;
    public ConnectionDB() {
        try {
            String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connect = DriverManager.getConnection(connectionString, dbUser, dbPass);
            this.statement = connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Problem with JDBC Driver");
            e.printStackTrace();
        }
    }

    public static synchronized ConnectionDB getInstance(){
        if (instance == null){
            instance = new ConnectionDB();
        }
        return instance;
    }

    public void insert(String str) {
        try {
            statement.executeUpdate(str);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String str) {
        try {
            statement.executeUpdate(str);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(String str) {
        try {
            statement.executeUpdate(str);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet select(String str) {
        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery(str);
            resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public void close() {
        try {
            connect.close();
            statement.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
