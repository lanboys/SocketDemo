package com.bing.lan.client;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SocketClient.SocketListener {

    RecyclerView mRecyclerView;
    Button mIvHeartBeat;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Button mBtnReset;
    private Button mBtnConnect;
    private Button mBtnDisconnect;
    private Button mBtnSend;
    private TextView mTvStatus;
    private EditText mIp;
    private EditText mPort;
    private EditText mContent;
    private SocketClient mSocketClient;
    private MessageListAdapter mAdapter;
    private List<Message> mMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mBtnReset = (Button) findViewById(R.id.reset);
        mBtnConnect = (Button) findViewById(R.id.connect);
        mBtnDisconnect = (Button) findViewById(R.id.disconnect);
        mTvStatus = (TextView) findViewById(R.id.status);
        mIvHeartBeat = (Button) findViewById(R.id.iv_heart_beat);

        mBtnSend = (Button) findViewById(R.id.send);
        mContent = (EditText) findViewById(R.id.content);
        mIp = (EditText) findViewById(R.id.ip);
        mPort = (EditText) findViewById(R.id.port);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        mBtnConnect.setOnClickListener(this);
        mBtnSend.setOnClickListener(this);
        mBtnReset.setOnClickListener(this);
        mBtnDisconnect.setOnClickListener(this);

        initRecyclerView();
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //https://github.com/yqritc/RecyclerView-FlexibleDivider
        mRecyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .color(Color.TRANSPARENT)
                        .size(10)
                        .margin(10, 10)
                        .build());

        mAdapter = new MessageListAdapter();

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setDataAndRefresh(mMessages);
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onRecyclerViewItemClick(View v, int position) {
                mContent.setText(mMessages.get(position).getMsg());
            }
        });

        mRecyclerView.setFocusable(true);
        mRecyclerView.requestFocus();
    }

    @Override
    public void onClick(final View v) {

        switch (v.getId()) {
            case R.id.reset:
                mTvStatus.setText("未连接");
                mIp.setText("192.168.2.186");
                mPort.setText("9898");
                if (mSocketClient != null) {
                    mSocketClient.stopConnect();
                    mTvStatus.setText("未连接");
                    mSocketClient = null;
                }
                mMessages.clear();
                mAdapter.notifyDataSetChanged();
                mIvHeartBeat.setSelected(false);

                break;
            case R.id.disconnect:
                mIvHeartBeat.setSelected(false);
                if (mSocketClient != null) {
                    mSocketClient.stopConnect();
                    mTvStatus.setText("断开连接");
                    mSocketClient = null;
                }
                break;
            case R.id.connect:
                if (mSocketClient != null) {
                    Toast.makeText(MainActivity.this, "已连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                mSocketClient = new SocketClient(MainActivity.this);
                mSocketClient.setSocketListener(MainActivity.this);
                mSocketClient.startConnect(mIp.getText().toString().trim(),
                        Integer.valueOf(mPort.getText().toString().trim()));

                break;
            case R.id.send:
                if (mSocketClient == null) {
                    Toast.makeText(MainActivity.this, "请先连接服务器", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String trim = mContent.getText().toString().trim();
                if (TextUtils.isEmpty(trim)) {
                    Toast.makeText(MainActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                mSocketClient.sendMsg(trim);
                break;
        }
    }

    @Override
    public void onStartConnect(boolean isSuccess) {
        mTvStatus.setText(isSuccess ? "连接成功" : "连接失败");

        mRecyclerView.setFocusable(true);
        mRecyclerView.requestFocus();
    }

    @Override
    public void onServerMessage(final Message serverMessage) {

        if (Message.MESSAGE_SERVER != serverMessage.getType()) {
            mIvHeartBeat.setSelected(!mIvHeartBeat.isSelected());
            mSocketClient.sendMsg("我是客户端心跳包: " + format.format(new Date(System.currentTimeMillis())));
        } else {
            mMessages.add(serverMessage);
            mAdapter.notifyDataSetChanged();
            mRecyclerView.smoothScrollToPosition(mMessages.size() - 1);
        }
    }

    @Override
    public void onSendMessage(boolean isSuccess, Message clientMessage) {
        if (isSuccess) {

            if (Message.MESSAGE_CLIENT != clientMessage.getType()) {
                Toast.makeText(MainActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                mContent.setText("");
                mMessages.add(clientMessage);
                mAdapter.notifyDataSetChanged();
            }
        } else {
            Toast.makeText(MainActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStopConnect() {
        Toast.makeText(MainActivity.this, "连接断开", Toast.LENGTH_SHORT).show();
    }
}
