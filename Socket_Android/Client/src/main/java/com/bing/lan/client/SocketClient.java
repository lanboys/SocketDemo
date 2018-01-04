package com.bing.lan.client;

import android.app.Activity;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SocketClient {

    private Activity mContext;
    private boolean isConnect = true;
    private Message mClientMsg;
    private boolean isSendMsg = false;
    private Socket mSocket;
    private BufferedWriter mBufferedWriter;
    private BufferedReader mBufferedReader;
    private SocketListener mSocketListener;

    private boolean isHeartBeat = false;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Date mDate = new Date();
    private static final int HEARTBEAT_INTERVAL_TIME = 200;

    private static final String HEARTBEAT_TEXT = "心跳包";

    public void setHeartBeat(boolean heartBeat) {
        isHeartBeat = heartBeat;
    }

    public SocketClient() {

    }

    public SocketClient(Activity activity) {
        mContext = activity;
    }

    public static void main(String[] args) throws IOException {

        SocketClient socketClient = new SocketClient();
        socketClient.startConnect("192.168.1.12", 9898);
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
                                    Thread.sleep(HEARTBEAT_INTERVAL_TIME);

                                    //mBufferedReader.readLine() 会阻塞线程 所以再开一条线程等待服务器消息
                                    if (mSocketListener != null && mBufferedReader != null && (mServerMsg[0] = mBufferedReader.readLine()) != null) {
                                        mContext.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //System.out.println("接收的消息: " + mServerMsg);

                                                if (mServerMsg[0].contains(HEARTBEAT_TEXT)) {
                                                    if (isHeartBeat) {
                                                        mSocketListener.onServerHeartBeat();
                                                        mDate.setTime(System.currentTimeMillis());
                                                        sendMsg("我是客户端心跳包: " + format.format(mDate));
                                                    }
                                                } else {
                                                    mSocketListener.onServerMessage(new Message(mServerMsg[0], Message.MESSAGE_SERVER));
                                                }

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
                        Thread.sleep(HEARTBEAT_INTERVAL_TIME);

                        if (isSendMsg) {
                            System.out.println("客户端发送了：" + mClientMsg.getMsg());

                            if (mBufferedWriter != null) {
                                mBufferedWriter.write(mClientMsg.getMsg() + "\n");
                                mBufferedWriter.flush();

                                if (mSocketListener != null) {
                                    mContext.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //System.out.println("发送的消息: " + mClientMsg);
                                            if (!mClientMsg.getMsg().contains(HEARTBEAT_TEXT)) {
                                                mSocketListener.onSendMessage(true, mClientMsg);
                                            }
                                        }
                                    });
                                }
                                isSendMsg = false;
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
            mClientMsg = new Message(msg, Message.MESSAGE_CLIENT);
            isSendMsg = true;
        } catch (Exception e) {
            //e.printStackTrace();
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

        void onServerHeartBeat();

        void onStopConnect();
    }
}
