package com.bing.lan.client;

/**
 * Created by 蓝兵 on 2017/9/12.
 */

public class Message {

    public static final int MESSAGE_CLIENT = 0;
    public static final int MESSAGE_SERVER = 1;

    private String msg;
    private int type;//1 server 0 client

    public Message(String msg, int type) {
        this.msg = msg;
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
