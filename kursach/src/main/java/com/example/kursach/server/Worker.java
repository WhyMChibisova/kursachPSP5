package com.example.kursach.server;

import com.example.kursach.database.ConnectionDB;
import com.example.kursach.models.*;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Worker implements Runnable {
    protected Socket clientSocket = null;
    private ObjectInputStream is;
    private ObjectOutputStream os;
    private ConnectionDB connection;
    private boolean flag;

    public Worker(ConnectionDB connection) {
        this.connection = connection;
    }

    public Worker(Socket clientSocket, ConnectionDB connection) {
        this.clientSocket = clientSocket;
        this.connection = connection;
        flag = true;
        try {
            this.is = new ObjectInputStream(clientSocket.getInputStream());
            this.os = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            while (flag) {
                Operations operations = (Operations) is.readObject();
                switch (operations) {
                    case AUTHORIZATION: {
                        User user = (User) is.readObject();
                        String sqlString = "SELECT * FROM users WHERE login = '" + user.getLogin() + "'";
                        ResultSet resultSet = connection.select(sqlString);
                        resultSet.beforeFirst();

                        int counter = 0;
                        while (resultSet.next()) {
                            counter++;
                        }
                        resultSet.first();

                        if (counter >= 1) {
                            String passwordDB = resultSet.getString("password");
                            boolean flag = passwordDB.equals(md5Custom(user.getPassword()));

                            if (flag) {
                                os.writeObject(Operations.OK);

                                int role = Integer.parseInt(resultSet.getString("role"));
                                user.setRole(role);
                                os.writeObject(user);
                                System.out.println("Пользователь " + resultSet.getString("surname") + " авторизован!");
                            } else {
                                os.writeObject(Operations.ERROR_PASS);
                                System.out.println("Неверный пароль");
                            }
                        } else {
                            os.writeObject(Operations.ERROR_USER);
                            System.out.println("Таких пользователей нет!");
                        }
                        break;
                    }
                    case REGISTRATION: {
                        User user = (User) is.readObject();
                        String sqlString = "SELECT * FROM users WHERE login = '" + user.getLogin() + "'";
                        ResultSet resultSet = connection.select(sqlString);
                        resultSet.beforeFirst();

                        int counter = 0;
                        while (resultSet.next()) {
                            counter++;
                        }
                        resultSet.first();

                        if (counter >= 1) {
                            os.writeObject(Operations.ERROR);
                            System.out.println("Такой пользователь уже есть!");
                        } else {
                            String sql = "INSERT INTO users (surname, name, login, password, role) VALUES ('" +
                                    user.getSurname() + "','" + user.getName() + "','" + user.getLogin()
                                    + "','" + md5Custom(user.getPassword()) + "'," + user.getRole() + ")";
                            connection.insert(sql);

                            os.writeObject(Operations.OK);
                            System.out.println("Пользователь " + user.getSurname() + " зарегистрирован!");
                        }
                        break;
                    }
                    case RESTORE_USER: {
                        User user1 = (User) is.readObject();
                        User user2 = (User) is.readObject();
                        String sqlString = "SELECT * FROM users WHERE login = '" + user1.getLogin() + "'";
                        ResultSet resultSet = connection.select(sqlString);
                        resultSet.beforeFirst();

                        int counter = 0;
                        while (resultSet.next()) {
                            counter++;
                        }
                        resultSet.first();

                        if (counter == 0 || !user1.getSurname().equals(resultSet.getString("surname"))
                                || !user1.getName().equals(resultSet.getString("name"))) {
                            os.writeObject(Operations.ERROR);
                            System.out.println("Такого пользователя нет!");
                        } else {
                            boolean flag = user1.getPassword().equals(user2.getPassword());
                            if (flag) {
                                String sql = "UPDATE users SET surname = '" + user1.getSurname()
                                        + "', name = '" + user1.getName() + "', login = '" + user1.getLogin()
                                        + "', password = '" + md5Custom(user1.getPassword()) + "', role = " +
                                        2 + " WHERE idusers = " + resultSet.getString("idusers");
                                connection.update(sql);

                                os.writeObject(Operations.OK);
                                System.out.println("Пользователь " + user1.getSurname() + " восстановлен!");
                            } else {
                                os.writeObject(Operations.ERROR_PASS);
                                System.out.println("Пароли не совпадают!");
                            }

                        }
                        break;
                    }
                    case VIEW_USERS: {
                        String sqlString = "SELECT * FROM users";
                        ResultSet resultSet = connection.select(sqlString);
                        int id;
                        String surname;
                        String name;
                        String login;
                        String password;
                        int role;
                        resultSet.beforeFirst();
                        while (resultSet.next()) {
                            os.writeObject(Operations.OK);
                            id = Integer.parseInt(resultSet.getString("idusers"));
                            surname = resultSet.getString("surname");
                            name = resultSet.getString("name");
                            login = resultSet.getString("login");
                            password = resultSet.getString("password");
                            role = Integer.parseInt(resultSet.getString("role"));
                            User user = new User(id, surname, name, login, password, role);
                            os.writeObject(user);
                        }
                        os.writeObject(Operations.EXIT);
                        break;
                    }
                    case ADD_NEW_USER: {
                        User user = (User) is.readObject();
                        String sqlString = "SELECT * FROM users WHERE login = '" + user.getLogin() + "'";
                        ResultSet resultSet = connection.select(sqlString);
                        resultSet.beforeFirst();

                        int counter = 0;
                        while (resultSet.next()) {
                            counter++;
                        }
                        resultSet.first();

                        if (counter >= 1) {
                            os.writeObject(Operations.ERROR);
                            System.out.println("Такой пользователь уже есть!");
                        } else {
                            String sql = "INSERT INTO users (surname, name, login, password, role) VALUES ('" +
                                    user.getSurname() + "','" + user.getName() + "','" + user.getLogin()
                                    + "','" + md5Custom(user.getPassword()) + "'," + 2 + ")";

                            connection.insert(sql);

                            os.writeObject(Operations.OK);
                            System.out.println("Пользователь " + user.getSurname() + " добавлен!");
                        }
                        break;
                    }
                    case DELETE_USER: {
                        User user = (User) is.readObject();
                        String sql = "DELETE FROM users WHERE login = '" + user.getLogin() + "'";
                        connection.delete(sql);
                        System.out.println("Пользователь " + user.getLogin() + " удалён!");
                        break;
                    }
                    case EDIT_LOGIN_USER: {
                        User user = (User) is.readObject();
                        String sqlString = "SELECT * FROM users WHERE login = '" + user.getLogin() + "'";
                        ResultSet resultSet = connection.select(sqlString);
                        resultSet.beforeFirst();

                        int counter = 0;
                        while (resultSet.next()) {
                            counter++;
                        }
                        resultSet.first();

                        if (counter >= 1) {
                            os.writeObject(Operations.ERROR);
                            System.out.println("Такой пользователь уже есть!");
                        } else {
                            os.writeObject(Operations.OK);
                        }
                        break;
                    }
                    case EDIT_USER: {
                        User user = (User) is.readObject();

                        String sql = "UPDATE users SET surname = '" + user.getSurname() + "', name = '" + user.getName() +
                                "', login = '" + user.getLogin() + "', password = '" + md5Custom(user.getPassword())
                                + "', role = " + user.getRole() + " WHERE idusers = " + user.getId();
                        connection.update(sql);

                        System.out.println("Пользователь " + user.getSurname() + " обновлён!");
                        break;
                    }
                    case VIEW_PRODUCTS: {
                        ResultSet resultSet = null;
                        Operations oper = (Operations) is.readObject();
                        if (oper.equals(Operations.OK)) {
                            String sqlString = "SELECT * FROM products";
                            resultSet = connection.select(sqlString);
                        } else if (oper.equals(Operations.SORT)) {
                            ComboBoxList comboBoxList = (ComboBoxList) is.readObject();

                            String sqlString = "SELECT * FROM " + comboBoxList.getTableName()
                                    + " WHERE " + comboBoxList.getFieldName() + " BETWEEN " + comboBoxList.getValue1()
                                    + " AND " + comboBoxList.getValue2();
                            resultSet = connection.select(sqlString);
                        } else {
                            ComboBoxList comboBoxList = (ComboBoxList) is.readObject();

                            String sqlString = "SELECT * FROM " + comboBoxList.getTableName()
                                    + " WHERE " + comboBoxList.getFieldName() + " = " + comboBoxList.getValue1();
                            resultSet = connection.select(sqlString);
                        }
                        int id;
                        String name;
                        double price;
                        int category_id;
                        double rating;
                        resultSet.beforeFirst();
                        while (resultSet.next()) {
                            os.writeObject(Operations.OK);
                            id = Integer.parseInt(resultSet.getString("idproducts"));
                            name = resultSet.getString("name");
                            price = Double.parseDouble(resultSet.getString("price"));
                            category_id = Integer.parseInt(resultSet.getString("category_id"));
                            rating = Double.parseDouble(resultSet.getString("rating"));
                            Product product = new Product(id, name, price, category_id, rating);
                            os.writeObject(product);
                        }
                        os.writeObject(Operations.EXIT);
                        break;
                    }
                    case ADD_NEW_PRODUCT: {
                        Product product = (Product) is.readObject();

                        String sql = "INSERT INTO products (name, price, category_id, rating) VALUES ('" +
                                product.getName() + "'," + product.getPrice() + "," + product.getCategory_id()
                                + "," + product.getRating() + ")";

                        connection.insert(sql);

                        os.writeObject(Operations.OK);
                        System.out.println("Продукт " + product.getName() + " добавлен!");
                        break;
                    }
                    case EDIT_PRODUCT: {
                        Product product = (Product) is.readObject();

                        String sql = "UPDATE products SET name = '" + product.getName() + "', price = " + product.getPrice() +
                                ", category_id = " + product.getCategory_id() + ", rating = " + product.getRating()
                                + " WHERE idproducts = " + product.getId();
                        connection.update(sql);

                        System.out.println("Продукт №" + product.getId() + " обновлён!");
                        break;
                    }
                    case DELETE_PRODUCT: {
                        Product product = (Product) is.readObject();
                        String sql = "DELETE FROM products WHERE idproducts = " + product.getId();
                        connection.delete(sql);
                        System.out.println("Продукт №" + product.getId() + " удалён!");
                        break;
                    }
                    case VIEW_CATEGORIES: {
                        ResultSet resultSet = null;
                        Operations oper = (Operations) is.readObject();
                        if (oper.equals(Operations.OK)) {
                            String sqlString = "SELECT * FROM category";
                            resultSet = connection.select(sqlString);
                        } else if (oper.equals(Operations.SORT)) {
                            ComboBoxList comboBoxList = (ComboBoxList) is.readObject();

                            String sqlString = "SELECT * FROM " + comboBoxList.getTableName() + " WHERE "
                                    + comboBoxList.getFieldName() + " BETWEEN " + comboBoxList.getValue1()
                                    + " AND " + comboBoxList.getValue2();
                            resultSet = connection.select(sqlString);
                        } else {
                            ComboBoxList comboBoxList = (ComboBoxList) is.readObject();

                            String sqlString = "SELECT * FROM " + comboBoxList.getTableName() + " WHERE "
                                    + comboBoxList.getFieldName() + " = " + comboBoxList.getValue1();
                            resultSet = connection.select(sqlString);
                        }
                        int id;
                        String name;
                        resultSet.beforeFirst();
                        while (resultSet.next()) {
                            os.writeObject(Operations.OK);
                            id = Integer.parseInt(resultSet.getString("idcategory"));
                            name = resultSet.getString("name");
                            Category category = new Category(id, name);
                            os.writeObject(category);
                        }
                        os.writeObject(Operations.EXIT);
                        break;
                    }
                    case VIEW_SALES: {
                        ResultSet resultSet = null;
                        Operations oper = (Operations) is.readObject();
                        if (oper.equals(Operations.OK)) {
                            String sqlString = "SELECT * FROM sales";
                            resultSet = connection.select(sqlString);
                        } else if (oper.equals(Operations.SORT)) {
                            ComboBoxList comboBoxList = (ComboBoxList) is.readObject();
                            if (comboBoxList.getFieldName().equals("date")) {
                                DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                LocalDate dateFrom = LocalDate.parse(comboBoxList.getValue1(), format);
                                LocalDate dateTo = LocalDate.parse(comboBoxList.getValue2(), format);
                                String sqlString = "SELECT * FROM " + comboBoxList.getTableName() + " WHERE "
                                        + comboBoxList.getFieldName() + " BETWEEN '"
                                        + dateFrom + "' AND '" + dateTo + "'";
                                resultSet = connection.select(sqlString);
                            } else {
                                String sqlString = "SELECT * FROM " + comboBoxList.getTableName() + " WHERE "
                                        + comboBoxList.getFieldName() + " BETWEEN " + comboBoxList.getValue1()
                                        + " AND " + comboBoxList.getValue2();
                                resultSet = connection.select(sqlString);
                            }
                        } else {
                            ComboBoxList comboBoxList = (ComboBoxList) is.readObject();

                            if (comboBoxList.getFieldName().equals("date")) {
                                DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                LocalDate date = LocalDate.parse(comboBoxList.getValue1(), format);
                                String sqlString = "SELECT * FROM " + comboBoxList.getTableName() + " WHERE "
                                        + comboBoxList.getFieldName() + " = '" + date + "'";
                                resultSet = connection.select(sqlString);
                            } else {
                                String sqlString = "SELECT * FROM " + comboBoxList.getTableName() + " WHERE "
                                        + comboBoxList.getFieldName() + " = " + comboBoxList.getValue1();
                                resultSet = connection.select(sqlString);
                            }
                        }
                        int id;
                        int product_id;
                        String datestr;
                        int count;
                        resultSet.beforeFirst();
                        while (resultSet.next()) {
                            os.writeObject(Operations.OK);
                            id = Integer.parseInt(resultSet.getString("idsales"));
                            product_id = Integer.parseInt(resultSet.getString("product_id"));
                            datestr = resultSet.getString("date");
                            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            LocalDate date = LocalDate.parse(datestr, format);
                            count = Integer.parseInt(resultSet.getString("count"));
                            Sale sale = new Sale(id, product_id, date, count);
                            os.writeObject(sale);
                        }
                        os.writeObject(Operations.EXIT);
                        break;
                    }
                    case EDIT_SALE: {
                        Sale sale = (Sale) is.readObject();

                        String sql = "UPDATE sales SET product_id = " + sale.getProduct_id() + ", date = '" + sale.getDate() +
                                "', count = " + sale.getCount() + " WHERE idsales = " + sale.getId();
                        connection.update(sql);

                        System.out.println("Продажа №" + sale.getId() + " обновлена!");
                        break;
                    }
                    case DELETE_SALE: {
                        Sale sale = (Sale) is.readObject();
                        String sql = "DELETE FROM sales WHERE idsales = " + sale.getId();
                        connection.delete(sql);
                        System.out.println("Продажа №" + sale.getId() + " удалена!");
                        break;
                    }
                    case ADD_NEW_SALE: {
                        Sale sale = (Sale) is.readObject();

                        String sql = "INSERT INTO sales (product_id, date, count) VALUES (" +
                                sale.getProduct_id() + ",'" + sale.getDate() + "'," + sale.getCount() + ")";

                        connection.insert(sql);

                        os.writeObject(Operations.OK);
                        System.out.println("Продажа " + sale.getDate() + " добавлена!");
                        break;
                    }
                    case PIE_CHART_DATA: {
                        String sqlString = "SELECT * FROM sales";
                        ResultSet resultSet = connection.select(sqlString);
                        resultSet.last();
                        int rows = resultSet.getRow();
                        List count = new ArrayList<String>();
                        List product_id = new ArrayList<String>();
                        List category_id = new ArrayList<String>();
                        List count2 = new ArrayList<String>();
                        resultSet.beforeFirst();
                        while (resultSet.next()) {
                            count.add(resultSet.getString("count"));
                            product_id.add(resultSet.getString("product_id"));
                        }
                        for (int i = 0; i < rows; i++) {
                            String sql = "SELECT * FROM products WHERE idproducts = " + product_id.get(i);
                            ResultSet resultSet1 = connection.select(sql);
                            resultSet1.first();
                            category_id.add(resultSet1.getString("category_id"));
                        }
                        for (int j = 0; j < category_id.size(); j++) {
                            int countAll = 0;
                            for (int i = 0; i < category_id.size(); i++) {
                                if (category_id.get(j).equals(category_id.get(i))) {
                                    countAll += Double.parseDouble((String) count.get(i));
                                }
                            }
                            count2.add(countAll);
                        }
                        for (int j = 0; j < category_id.size(); j++) {
                            int flag = 1;
                            for (int i = 0; i < category_id.size(); i++) {
                                if (category_id.get(j).equals(category_id.get(i))) {
                                    if (flag != 1) {
                                        category_id.remove(i);
                                        count2.remove(i);
                                    }
                                    flag++;
                                }
                            }
                        }
                        for (int i = 0; i < count2.size(); i++) {
                            os.writeObject(Operations.OK);
                            String sql2 = "SELECT * FROM category WHERE idcategory = " + category_id.get(i);
                            ResultSet resultSet2 = connection.select(sql2);
                            resultSet2.first();
                            Sale sale = new Sale();
                            sale.setCount((int)count2.get(i));
                            Category category = new Category();
                            category.setName(resultSet2.getString("name"));
                            os.writeObject(sale);
                            os.writeObject(category);
                        }
                        os.writeObject(Operations.EXIT);
                        break;
                    }
                    case BAR_CHART_DATA: {
                        String sqlString = "SELECT * FROM sales";
                        ResultSet resultSet = connection.select(sqlString);
                        int count;
                        String datestr;
                        resultSet.beforeFirst();
                        while (resultSet.next()) {
                            os.writeObject(Operations.OK);
                            count = Integer.parseInt(resultSet.getString("count"));
                            datestr = resultSet.getString("date");
                            Sale sale = new Sale();
                            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            LocalDate date = LocalDate.parse(datestr, format);
                            sale.setDate(date);
                            sale.setCount(count);
                            os.writeObject(sale);
                        }
                        os.writeObject(Operations.EXIT);
                        break;
                    }
                    case LINE_CHART_DATA: {
                        String sqlString = "SELECT * FROM products";
                        ResultSet resultSet = connection.select(sqlString);
                        double rating;
                        String name;
                        resultSet.beforeFirst();
                        while (resultSet.next()) {
                            os.writeObject(Operations.OK);
                            rating = Double.parseDouble(resultSet.getString("rating"));
                            name = resultSet.getString("name");
                            Product product = new Product();
                            product.setRating(rating);
                            product.setName(name);
                            os.writeObject(product);
                        }
                        os.writeObject(Operations.EXIT);
                        break;
                    }
                    case SAVE_FILE: {
                        ComboBoxList comboBoxList = (ComboBoxList) is.readObject();
                        String sqlString = "SELECT * FROM " + comboBoxList.getTableName();
                        ResultSet resultSet = connection.select(sqlString);
                        resultSet.beforeFirst();
                        FileWriter writer = new FileWriter(comboBoxList.getTableName() + ".txt", false);
                        switch (comboBoxList.getTableName()) {
                            case "products": {
                                writer.write("id | name | price | category id | rating |");
                                writer.write("\n");
                                while (resultSet.next()) {
                                    writer.write(resultSet.getString("idproducts") + "   ");
                                    writer.write(resultSet.getString("name") + "   ");
                                    writer.write(resultSet.getString("price") + "   ");
                                    writer.write(resultSet.getString("category_id") + "   ");
                                    writer.write(resultSet.getString("rating") + "   ");
                                    writer.write("\n");
                                }
                                writer.close();
                                break;
                            }
                            case "category": {
                                writer.write("id | name |");
                                writer.write("\n");
                                while (resultSet.next()) {
                                    writer.write(resultSet.getString("idcategory") + "   ");
                                    writer.write(resultSet.getString("name") + "   ");
                                    writer.write("\n");
                                }
                                writer.close();
                                break;
                            }
                            case "sales": {
                                writer.write("id | product id | date | count |");
                                writer.write("\n");
                                while (resultSet.next()) {
                                    writer.write(resultSet.getString("idsales") + "   ");
                                    writer.write(resultSet.getString("product_id") + "   ");
                                    writer.write(resultSet.getString("date") + "   ");
                                    writer.write(resultSet.getString("count") + "   ");
                                    writer.write("\n");
                                }
                                writer.close();
                                break;
                            }
                        }
                        System.out.println("Таблица " + comboBoxList.getTableName() + " сохранена");
                        break;
                    }
                    case EXIT: {
                        os.writeObject(Operations.OK);
                        flag = false;
                        os.close();
                        is.close();
                        System.out.println("Клиент " + clientSocket.getInetAddress().toString() + " отключился");
                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String md5Custom(String str) {
        MessageDigest messageDigest = null;
        byte[] digest = new byte[0];

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes());
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            // тут можно обработать ошибку
            // возникает она если в передаваемый алгоритм в getInstance(,,,) не существует
            e.printStackTrace();
        }

        BigInteger bigInt = new BigInteger(1, digest);
        String md5Hex = bigInt.toString(16);

        while (md5Hex.length() < 32) {
            md5Hex = "0" + md5Hex;
        }

        return md5Hex;
    }

    public String findUser(String login) {
        try {
            String sqlString = "SELECT * FROM users WHERE login = '" + login + "'";
            ResultSet resultSet = connection.select(sqlString);
            resultSet.beforeFirst();
            int counter = 0;
            while (resultSet.next()) {
                counter++;
            }
            resultSet.first();

            if (counter >= 1) {
                return resultSet.getString("name");
            } else {
                return "error";
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public double selectProduct(String name) {
        try {
            ResultSet resultSet = null;
            String sqlString = "SELECT * FROM products WHERE name = '" + name + "'";
            resultSet = connection.select(sqlString);
            resultSet.first();
            double price = Double.parseDouble(resultSet.getString("price"));
            return price;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
