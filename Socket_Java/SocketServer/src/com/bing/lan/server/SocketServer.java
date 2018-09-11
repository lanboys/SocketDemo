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
        socketServer.startServer1();
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
            System.out.println("Server end :"+e);
        }
    }

    private void startServer1() {
        try {
            ServerSocket serverSocket = new ServerSocket(9898);
            for (int i = 0; i < 4; i++) {
                int finalI = i;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Server " + finalI + " start");
                        Socket accept;
                        while (true) {
                            try {
                                accept = serverSocket.accept();
                                handleSocket(accept);
                                System.out.println("Server " + finalI + " accept");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        } catch (Exception e) {
            System.out.println("Server end :"+e);
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
                                Date mDate = new Date();

                                while (true) {
                                    Thread.sleep(1000);
                                    mDate.setTime(System.currentTimeMillis());
                                    bufferedWriter.write("我是服务器心跳包: " + format.format(mDate) + "\n");
                                    bufferedWriter.flush();
                                }
                            } catch (Exception e) {
                                //e.printStackTrace();
                            }
                        }
                    }).start();

                    //int read = bufferedReader.read();

                    //char[] cbuf = new char[8];
                    //
                    //while (true) {
                    //    System.out.println("---------------------");
                    //
                    //    StringBuffer stringBuffer = new StringBuffer();
                    //    int read = 0;
                    //    while (-1 != bufferedReader.read(cbuf)) {
                    //        stringBuffer.append(cbuf);
                    //        System.out.println("客户端: " + Arrays.toString(cbuf));
                    //    }
                    //    System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxx");
                    //
                    //    String s = stringBuffer.toString();
                    //    System.out.println("客户端2222: " + s);
                    //    if (!s.contains("心跳包")) {
                    //        System.out.println("客户端 " + socket.hashCode() + " 发来了消息：" + s);
                    //        bufferedWriter.write("小娜: " + s + "\n");
                    //        bufferedWriter.flush();
                    //    }
                    //}

                    String clientMsg;
                    while ((clientMsg = bufferedReader.readLine()) != null) {

                        if (!clientMsg.contains("心跳包")) {
                            System.out.println("客户端 " + socket.hashCode() + " 发来了消息：" + clientMsg);
                            bufferedWriter.write("小娜: " + clientMsg + "\n");
                            bufferedWriter.flush();
                        }
                    }

                    System.out.println("客户端: " + socket.hashCode() + " 断开连接了");
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
        }).start();
    }
}
