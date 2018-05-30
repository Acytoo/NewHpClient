package com.acytoo.newhpcliend.utils;

import android.support.annotation.Nullable;
import android.util.Log;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import static com.acytoo.newhpcliend.service.MyService.output;
import static com.acytoo.newhpcliend.ui.ChatActivity.chatAdd;

public class MyWebSocketListener extends WebSocketListener {

    public static  volatile WebSocket myWebSocket;

    public MyWebSocketListener() {
        super();
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        Log.d("wsconnect", "onOpen");
        myWebSocket = webSocket;
        myWebSocket.send("on'open");


    }


    /**
     * 所接受数据类型可以不同， 根据第一个split结果来进行不同处理
     * @param webSocket
     * @param text
     */
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        try {
            Log.d("wsconnect", "wsreceive :" + text);
            if (text.charAt(0) == '#') {
                output("Receiving :&" + text.replace("#", ""));
            } else {
                chatAdd(text.replace("______", " : "));
            }
        } catch (Exception e){
            Log.d("wsconnect", "exception : " + e.toString());
        }

    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        super.onMessage(webSocket, bytes);
        Log.d("wsconnect", "wsreceive bytes :" + bytes);
        output("Receiving :&" + bytes.hex());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        Log.d("wsconnect", "Closing");
        super.onClosing(webSocket, code, reason);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        Log.d("wsconnect", "Closed");
        super.onClosed(webSocket, code, reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
        Log.d("wsconnect", "Fail");
        super.onFailure(webSocket, t, response);
    }

    public static void send(String Text) {
        myWebSocket.send(Text);
    }
}
