package com.acytoo.newhpcliend.utils;

import android.support.annotation.Nullable;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import static com.acytoo.newhpcliend.service.MyService.output;

public class MyWebSocketListener extends WebSocketListener {

    private WebSocket webSocket;

    public MyWebSocketListener() {
        super();
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        //webSocket.send("tested Plans");
    }


    /**
     * 所接受数据类型可以不同， 根据第一个split结果来进行不同处理
     * @param webSocket
     * @param text
     */
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        output("Receiving :&" + text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        super.onMessage(webSocket, bytes);
        output("Receiving :&" + bytes.hex());
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
