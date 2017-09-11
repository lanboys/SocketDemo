package com.bing.lan.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
            Socket accept = serverSocket.accept();
            System.out.println("客户端: " + accept.hashCode() + " 连接上了");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(accept.getInputStream()));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(accept.getOutputStream()));

            String clientMsg;

            while ((clientMsg = bufferedReader.readLine()) != null) {
                System.out.println(clientMsg);
                bufferedWriter.write("服务端收到了消息：" + clientMsg + "\n");
                bufferedWriter.flush();
            }
            System.out.println("Server end");
        } catch (Exception e) {

        }
    }
}
