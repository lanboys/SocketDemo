package com.bing.lan.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

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

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                while (true) {
                                    Thread.sleep(1000);
                                    bufferedWriter.write("我是服务器心跳包: " + format.format(new Date(System.currentTimeMillis())) + "\n");
                                    bufferedWriter.flush();
                                }
                            } catch (Exception e) {
                                //e.printStackTrace();
                            }
                        }
                    }).start();

                    String clientMsg;
                    while ((clientMsg = bufferedReader.readLine()) != null) {
                        System.out.println("客户端 " + socket.hashCode() + " 发来了消息：" + clientMsg);
                        bufferedWriter.write("小娜: "+clientMsg + "\n");
                        bufferedWriter.flush();
                    }

                    System.out.println("客户端: " + socket.hashCode() + " 断开连接了");
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
        }).start();
    }
}
