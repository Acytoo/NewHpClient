package com.acytoo.newhpcliend.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.acytoo.newhpcliend.R;
import com.acytoo.newhpcliend.utils.ChatListener;
import com.acytoo.newhpcliend.utils.MyWebSocketListener;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

/**
 *
 * 在有需求的时候要先连接， 连接的设备参与及时通信
 * 不支持点对点通信，服务器会把发过去的内容全部返回
 *
 * 是否重写一个listener类？？？
 */
public class ChatActivity extends AppCompatActivity {

    private EditText input_message_chat;
    private Button btn_send_chat;
    private OkHttpClient okHttpClient;
    WebSocket webSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        input_message_chat = findViewById(R.id.input_message_chat);
        btn_send_chat = findViewById(R.id.btn_send_chat);
        okHttpClient = new OkHttpClient();

        String url = "";
        Request request = new Request.Builder().url(url).build();
        webSocket = okHttpClient.newWebSocket(request, new ChatListener());

        btn_send_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });

    }

    private void send() {
        String message = input_message_chat.getText().toString();
        webSocket.send(message);


    }

    private void onGet() {
        //setText
     }


}
