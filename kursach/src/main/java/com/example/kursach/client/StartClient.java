package com.example.kursach.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class StartClient extends Application {
    private Socket clientSocket;
    protected static Client client;

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/kursach/authorization.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 400);
        stage.setTitle("Авторизация");
        stage.setScene(scene);
        stage.show();

        clientSocket = new Socket("127.0.0.1", 1024);
        client = new Client(clientSocket);
    }

    public static void main(String[] args) {
        launch();
    }
}
