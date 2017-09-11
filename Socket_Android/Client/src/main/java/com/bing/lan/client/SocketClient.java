package com.bing.lan.client;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SocketClient {

    private Socket mSocket;
    private BufferedWriter mBufferedWriter;
    private BufferedReader mBufferedReader;

    public static void main(String[] args) throws IOException {

        SocketClient socketServer = new SocketClient();

        socketServer.start();
    }

    public boolean start() {
        try {
            System.out.println("client start");
            mSocket = new Socket("192.168.2.180", 2048);

            mBufferedWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
            mBufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void send(String msg) throws IOException {

        if (TextUtils.isEmpty(msg)) {
            return;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(msg.getBytes())));
        String clientMsg;
        while (!"bye".equals(clientMsg = reader.readLine())) {
            mBufferedWriter.write("android client：" + clientMsg + "\n");
            mBufferedWriter.flush();

            System.out.println("客户端收到了服务端回复：" + mBufferedReader.readLine());
        }
        System.out.println("client end");
    }
}
