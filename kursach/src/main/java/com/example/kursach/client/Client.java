package com.example.kursach.client;

import java.io.*;
import java.net.Socket;

public class Client {
    private Socket clientSocket;
    private ObjectInputStream is;
    private ObjectOutputStream os;

    public Client(Socket clientSocket){
        try {
            this.os = new ObjectOutputStream(clientSocket.getOutputStream());
            os.flush();
            this.is = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendObject(Object object){
        try {
            os.writeObject(object);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object readObject() throws IOException {
        try {
            return is.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            is.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
