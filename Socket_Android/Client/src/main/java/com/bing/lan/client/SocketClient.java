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

    private ServerListener mServerListener;

    interface ServerListener {

        void onServerMessage(String serverMessage);
    }

    public static void main(String[] args) throws IOException {

        SocketClient socketClient = new SocketClient();
        socketClient.startConnect("192.168.2.186", 9898);
    }

    public boolean startConnect(String host, int port) {
        try {
            System.out.println("client startConnect");
            mSocket = new Socket(host, port);

            mBufferedWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
            mBufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));

            if (mServerListener != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String clientMsg;
                            while ((clientMsg = mBufferedReader.readLine()) != null && mServerListener != null) {
                                mServerListener.onServerMessage(clientMsg);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void stopConnect() {

        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mBufferedWriter != null) {
            try {
                mBufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mBufferedReader != null) {
            try {
                mBufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mBufferedReader = null;
        mSocket = null;
        mBufferedWriter = null;
        mServerListener = null;
    }

    public void sendMsg(String msg) throws IOException {

        if (TextUtils.isEmpty(msg)) {
            return;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(msg.getBytes())));
        String clientMsg = reader.readLine();
        System.out.println("客户端发送了：" + clientMsg);
        mBufferedWriter.write(clientMsg + "\n");
        mBufferedWriter.flush();
        //System.out.println("收到了服务器回复：" + mBufferedReader.readLine());
    }

    public void setServerListener(ServerListener serverListener) {
        mServerListener = serverListener;
    }
}
