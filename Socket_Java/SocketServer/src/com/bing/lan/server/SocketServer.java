package com.bing.lan.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {

    public static void main(String[] args) {

        SocketServer socketServer = new SocketServer();
        socketServer.startServer();
    }

    private void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(9898)) {
            System.out.println("Server start");
            Socket accept;
            while (true) {
                accept = serverSocket.accept();
                handleSocket(accept);
            }
        } catch (Exception e) {
            System.out.println("Server end");
        }
    }

    public void handleSocket(Socket socket) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("客户端: " + socket.hashCode() + " 连接上了");
                    BufferedReader bufferedReader = null;
                    bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    String clientMsg;
                    while ((clientMsg = bufferedReader.readLine()) != null) {
                        System.out.println("客户端 " + socket.hashCode() + " 发来了消息：" + clientMsg);
                        bufferedWriter.write(clientMsg + "(--服务器)\n");
                        bufferedWriter.flush();
                    }

                    System.out.println("客户端: " + socket.hashCode() + " 断开连接了");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
