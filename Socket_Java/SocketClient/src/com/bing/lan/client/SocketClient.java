package com.bing.lan.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SocketClient {

    public static void main(String[] args) {

        SocketClient socketServer = new SocketClient();

        socketServer.start();
    }

    private void start() {
        try (Socket socket = new Socket("127.0.0.1", 2048)) {

            System.out.println("client start");
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            String clientMsg;

            while (!"bye".equals(clientMsg = reader.readLine())) {
                System.out.println("-------------------会话开始----------------------");
                bufferedWriter.write("client：" + clientMsg + "\n");
                bufferedWriter.flush();
                System.out.println("客户端收到了服务端回复：" + bufferedReader.readLine());
                System.out.println("-------------------会话结束----------------------");
            }
            System.out.println("client end");
        } catch (Exception e) {

        }
    }
}
