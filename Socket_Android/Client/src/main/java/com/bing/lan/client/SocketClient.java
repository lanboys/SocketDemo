package com.bing.lan.client;

import android.app.Activity;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SocketClient {

    Activity mContext;
    boolean isConnect = true;
    Message mClientMsg;
    boolean isSendMsg = false;
    private Socket mSocket;
    private BufferedWriter mBufferedWriter;
    private BufferedReader mBufferedReader;
    private SocketListener mSocketListener;

    public SocketClient() {

    }

    public SocketClient(Activity activity) {
        mContext = activity;
    }

    public static void main(String[] args) throws IOException {

        SocketClient socketClient = new SocketClient();
        socketClient.startConnect("192.168.2.186", 9898);
    }

    public void setSocketListener(SocketListener socketListener) {
        mSocketListener = socketListener;
    }

    public void startConnect(final String host, final int port) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("client startConnect");
                    mSocket = new Socket(host, port);
                    mBufferedWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
                    mBufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                    isConnect = true;
                    if (mSocketListener != null) {
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mSocketListener.onStartConnect(true);
                            }
                        });
                    }

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final String[] mServerMsg = new String[1];

                                while (isConnect) {
                                    System.out.println("-----接收线程------");
                                    Thread.sleep(200);

                                    //mBufferedReader.readLine() 会阻塞线程 所以再开一条线程等待服务器消息
                                    if (mSocketListener != null && mBufferedReader != null && (mServerMsg[0] = mBufferedReader.readLine()) != null) {
                                        mContext.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //System.out.println("接收的消息: " + mServerMsg);
                                                mSocketListener.onServerMessage(new Message(mServerMsg[0], Message.MESSAGE_SERVER));
                                                mServerMsg[0] = null;
                                            }
                                        });
                                    }
                                }
                            } catch (Exception e) {
                                // e.printStackTrace();
                            }
                        }
                    }).start();

                    while (isConnect) {
                        System.out.println("--------------发送线程-----");
                        Thread.sleep(200);

                        if (mClientMsg != null) {
                            System.out.println("客户端发送了：" + mClientMsg.getMsg());

                            if (mBufferedWriter != null) {
                                mBufferedWriter.write(mClientMsg.getMsg() + "\n");
                                mBufferedWriter.flush();

                                if (mSocketListener != null) {
                                    mContext.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //System.out.println("发送的消息: " + mClientMsg);
                                            mSocketListener.onSendMessage(true, mClientMsg);
                                        }
                                    });
                                }
                                isSendMsg = false;
                                mClientMsg = null;
                            }
                        }

                        //if (mSocketListener != null && mBufferedReader != null && (mServerMsg = mBufferedReader.readLine()) != null) {
                        //    mContext.runOnUiThread(new Runnable() {
                        //        @Override
                        //        public void run() {
                        //            mSocketListener.onServerMessage(mServerMsg);
                        //            mServerMsg = null;
                        //        }
                        //    });
                        //}
                    }
                } catch (Exception e) {
                    //if (mSocketListener != null) {
                    //    mContext.runOnUiThread(new Runnable() {
                    //        @Override
                    //        public void run() {
                    //            mSocketListener.onStartConnect(false);
                    //        }
                    //    });
                    //}

                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void stopConnect() {

        isConnect = false;
        if (mSocketListener != null) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSocketListener.onStopConnect();
                }
            });
        }

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
        mSocketListener = null;
        mContext = null;
        isSendMsg = false;
        mClientMsg = null;
    }

    public void sendMsg(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        try {
            //StringBuilder clientMsgBuilder = new StringBuilder("");
            //BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(msg.getBytes())));
            //String clientMsg;
            //while ((clientMsg = reader.readLine()) != null) {
            //    clientMsgBuilder.append(clientMsg);
            //}
            //mClientMsg = clientMsgBuilder.toString();
            mClientMsg = new Message(msg, Message.MESSAGE_CLIENT);
            isSendMsg = true;
        } catch (Exception e) {
            e.printStackTrace();
            if (mSocketListener != null) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSocketListener.onSendMessage(false, null);
                    }
                });
            }
        }
    }

    interface SocketListener {

        void onStartConnect(boolean isSuccess);

        void onServerMessage(Message serverMessage);

        void onSendMessage(boolean isSuccess, Message clientMessage);

        void onStopConnect();
    }
}
