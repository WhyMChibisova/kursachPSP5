package com.example.kursach.server;

import com.example.kursach.database.ConnectionDB;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiThreadedServer implements Runnable {
    protected int serverPort = 1024;
    protected ServerSocket serverSocket = null;
    protected boolean isStopped = false;

    public MultiThreadedServer(int port){
        this.serverPort = port;
    }

    @Override
    public void run(){
        openServerSocket();
        while(! isStopped()){
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
                System.out.println("=======================================");
                System.out.println("Клиент " + clientSocket.getInetAddress() + " подключился.");
                //ConnectionDB connection = new ConnectionDB();
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println("Сервер закрыт.") ;
                    return;
                }
                throw new RuntimeException("Ошибка подключения клиента", e);
            }
            new Thread(new Worker(clientSocket, ConnectionDB.getInstance())).start();
        }
        System.out.println("Сервер закрыт.") ;
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка закрытия сервера", e);
        }
    }

    private void openServerSocket() {
        System.out.println("Открытие сокета сервера...");
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Не получается открыть порт " + this.serverPort, e);
        }
    }
}
