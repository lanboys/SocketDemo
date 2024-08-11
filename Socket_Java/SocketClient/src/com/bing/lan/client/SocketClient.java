package com.bing.lan.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SocketClient {

    public static void main(String[] args) {

        SocketClient socketClient = new SocketClient();

        socketClient.start();
    }

    private void start() {
        try (Socket socket = new Socket("127.0.0.1", 9898)) {

            System.out.println("client start");
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String clientMsg = bufferedReader.readLine();
                            System.out.println("收到了服务器消息：" + clientMsg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();


            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            String clientMsg;

            while (!"bye".equals(clientMsg = reader.readLine())) {
                //System.out.println("-------------------一次会话开始----------------------");
                System.out.println("客户端发送了：" + clientMsg);
                bufferedWriter.write(clientMsg + "\n");
                bufferedWriter.flush();
                // System.out.println("收到了服务器回复：" + bufferedReader.readLine());
                //System.out.println("-------------------一次会话结束----------------------");
            }
            System.out.println("client end");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
