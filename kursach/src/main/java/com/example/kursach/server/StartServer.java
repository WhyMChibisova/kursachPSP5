package com.example.kursach.server;

public class StartServer {
    public static final int PORT_WORK = 1024;

    public static void main(String[] args) {
        MultiThreadedServer server = new MultiThreadedServer(PORT_WORK);
        new Thread(server).start();
    }
}
