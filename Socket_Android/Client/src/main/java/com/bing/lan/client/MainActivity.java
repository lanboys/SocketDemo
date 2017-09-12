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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.bing.lan.client.R.id.status;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SocketClient.ServerListener {

    private Button mBtnReset;
    private Button mBtnConnect;
    private Button mBtnDisconnect;
    private Button mBtnSend;
    private TextView mTvStatus;
    private EditText mIp;
    private EditText mPort;
    private EditText mContent;
    private SocketClient mSocketClient;
    RecyclerView mRecyclerView;
    private MessageListAdapter mAdapter;
    private List<Message> mMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnReset = (Button) findViewById(R.id.reset);
        mBtnConnect = (Button) findViewById(R.id.connect);
        mBtnDisconnect = (Button) findViewById(R.id.disconnect);
        mTvStatus = (TextView) findViewById(status);

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
        //
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

                break;
            case R.id.disconnect:
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

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mSocketClient = new SocketClient();
                        mSocketClient.setServerListener(MainActivity.this);
                        final String status = mSocketClient.startConnect(mIp.getText().toString().trim(),
                                Integer.valueOf(mPort.getText().toString().trim())) ? "连接成功" : "连接失败";

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTvStatus.setText(status);
                            }
                        });
                    }
                }).start();

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

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mSocketClient.sendMsg(trim);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mContent.setText("");
                                    mMessages.add(new Message(trim, Message.MESSAGE_CLIENT));

                                    mAdapter.notifyDataSetChanged();

                                    Toast.makeText(MainActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();

                break;
        }
    }

    @Override
    public void onServerMessage(final String serverMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMessages.add(new Message(serverMessage, Message.MESSAGE_SERVER));
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}
