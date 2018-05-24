package com.acytoo.newhpcliend.utils;


import android.support.annotation.Nullable;
import android.util.Log;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import static com.acytoo.newhpcliend.service.MyService.output;

public class ChatListener extends WebSocketListener {

    public ChatListener() {
        super();
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);

    }


    /**
     * 所接受数据类型可以不同， 根据第一个split结果来进行不同处理
     * @param webSocket
     * @param text
     */
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        Log.d("wsconnect", "wsreceive :" + text);
        //output("Receiving :&" + text);
        /*

        这里是不是有一个Handler， 处理收到的信息


         */
        chatReceiveHandler(text);

    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        super.onMessage(webSocket, bytes);
        Log.d("wsconnect", "wsreceive :" + bytes);
        //output("Receiving :&" + bytes.hex());
        chatReceiveHandler(bytes.hex());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        super.onClosing(webSocket, code, reason);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
    }
}
