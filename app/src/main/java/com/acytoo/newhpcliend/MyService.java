package com.acytoo.newhpcliend;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class MyService extends Service {

    private final IBinder myBinder = new MyLocalBinder();
    private int counter;
    private boolean interrupted = false;
    private boolean started = false;
    private static MyDBHandler dbHandler;
    private WebSocket webSocket;
    private OkHttpClient client;
    NetWorkStateReceiver netWorkStateReceiver;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public class MyLocalBinder extends Binder {
        MyService getService(){
            return MyService.this;
        }

    }

    /**
     * in this class, we return the internet service
     * @return
     */

    public String getCurrentDate(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        return df.format(new Date());
    }

    /**
     * onStartCommand() only called when startService() is called in your "main" activity, otherwise
     * this method won't called by system, Now I call the startService() first then bind it
     * Alec Chen
     * 12/5/2018 09:57
     * 这个服务还要检测网络状态， 当网络状态改变时，重新连接， 发送用户名， 密码。
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver, filter);

        if (!started) {
            started = true;
            new Thread(new Runnable() {
                @Override
                public void run() {

                    dbHandler = new MyDBHandler(MyApplication.getInstance(), null, null, 2);
                    wsConnect();

                    while (!interrupted) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.d("counter", "now the counter is " + counter);
                        counter++;
                    }
                }
            }).start();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        interrupted = true;
        client.dispatcher().executorService().shutdown();
        unregisterReceiver(netWorkStateReceiver);
        super.onDestroy();
    }

    public static void output(final String txt){
        String plan = txt.split("&")[1];
        dbHandler.addPlan(new Plans(new Date().getTime(), 7, 0,
                "websocket", plan, 1,1,1 ));
        Log.d("planWebTest", "finish writing db");

    }

    public void wsConnect(){
        String url = "ws://58.78.90.180:8080/websocket/server/stu_20154444";
        Request request = new Request.Builder().url("wss://echo.websocket.org").build();
        MyWebSocketListener listener = new MyWebSocketListener();
        client = new OkHttpClient();
        webSocket = client.newWebSocket(request, listener);
        //webSocket.send(MyCookieJar.getLastCookie());
        /**
         * 一共会有几个cookie?是否过期怎么判断
         *
         * 过期后如何处理？
         */
    }
}
