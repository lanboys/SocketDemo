package com.bing.lan.client;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnConnect;
    private Button mBtnSend;
    private TextView mTvStatus;
    private EditText mContent;
    private SocketClient mSocketServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnConnect = (Button) findViewById(R.id.connect);
        mTvStatus = (TextView) findViewById(R.id.status);

        mBtnSend = (Button) findViewById(R.id.send);
        mContent = (EditText) findViewById(R.id.content);

        mBtnConnect.setOnClickListener(this);
        mBtnSend.setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                switch (v.getId()) {

                    case R.id.connect:

                        mSocketServer = new SocketClient();
                        final String status = mSocketServer.start() ? "连接成功" : "连接失败";
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTvStatus.setText(status);
                            }
                        });

                        break;
                    case R.id.send:
                        try {
                            if (mSocketServer != null) {
                                mSocketServer.send(mContent.getText().toString().trim());
                            }
                            Toast.makeText(MainActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();

                            Toast.makeText(MainActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                        }

                        break;
                }
            }
        }).start();
    }
}
