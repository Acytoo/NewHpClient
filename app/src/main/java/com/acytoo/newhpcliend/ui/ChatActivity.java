package com.acytoo.newhpcliend.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.acytoo.newhpcliend.R;
import com.acytoo.newhpcliend.service.MyService;
//import com.acytoo.newhpcliend.utils.ChatListener;
import com.acytoo.newhpcliend.utils.MyWebSocketListener;
import com.acytoo.newhpcliend.utils.TestWebsocket;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

import static com.acytoo.newhpcliend.service.MyService.sendChat;

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
    private static TextView txt_chat_board;
    //WebSocket webSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        input_message_chat = findViewById(R.id.input_message_chat);
        btn_send_chat = findViewById(R.id.btn_send_chat);
        txt_chat_board = findViewById(R.id.txt_chat_board);
        //okHttpClient = new OkHttpClient();

        //String url = "";
        //Request request = new Request.Builder().url(url).build();
        //webSocket = okHttpClient.newWebSocket(request, new ChatListener());

        btn_send_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("wsconnect","debug + " + MyService.flag);
                send();
                if (TestWebsocket.webSocket == null) {
                    Log.d("wsconnect", "test websocket is empty");
                }

            }
        });

    }

    private void send() {
        String message = input_message_chat.getText().toString();
        Log.d("wsconnect", "message is " + message);
        if (MyService.webSocket == null ){
            Log.d("wsconnect", "empty pointer");
        }
        MyService.sendChat(message);


    }



    public static void chatAdd(String text) {
        Log.d("wsconnect", "receive chat message " + text);
        txt_chat_board.setText(txt_chat_board.getText() + "\n" + text);

    }

}
