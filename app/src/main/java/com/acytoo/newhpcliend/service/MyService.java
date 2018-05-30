package com.acytoo.newhpcliend.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.acytoo.newhpcliend.MyApplication;
import com.acytoo.newhpcliend.R;
import com.acytoo.newhpcliend.utils.HttpManager;
import com.acytoo.newhpcliend.utils.MyCookieJar;
import com.acytoo.newhpcliend.utils.MyDBHandler;
import com.acytoo.newhpcliend.utils.MyWebSocketListener;
import com.acytoo.newhpcliend.utils.NetWorkStateReceiver;
import com.acytoo.newhpcliend.utils.Plans;
import com.acytoo.newhpcliend.utils.TestWebsocket;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class MyService extends Service {


    private int counter;
    private boolean interrupted = false;
    private boolean started = false;
    private static MyDBHandler dbHandler;
    static OkHttpClient client;
    public static volatile WebSocket webSocket;
    public static String flag = "abc";
    public static Handler handler = new Handler();

    NetWorkStateReceiver netWorkStateReceiver;

    public static final int NOTICE_ID = 100;

    public MyService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Climb");
        builder.setContentText("And climb is all there is");
        startForeground(NOTICE_ID,builder.build());
//         如果觉得常驻通知栏体验不好
//         可以通过启动CancelNoticeService，将通知移除，oom_adj值不变
        Intent intent = new Intent(this,CancelNoticeService.class);
        startService(intent);

    }



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

        HttpManager httpManager = new HttpManager();
        httpManager.syncSlogan();

        if (!started) {
            started = true;
            if (netWorkStateReceiver == null) {
                netWorkStateReceiver = new NetWorkStateReceiver();
            }
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(netWorkStateReceiver, filter);

            new Thread(new Runnable() {
                @Override
                public void run() {

                    dbHandler = new MyDBHandler(MyApplication.getInstance(), null, null, 2);
                    //wsConnect();

                    while (!interrupted) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //Log.d("wsconnect", "now the counter is " + counter);
                        counter++;
//                        if (counter > 20 && counter < 22) {
//                            //webSocket.send("manual");
//                            //sendChat("counter");
//                            //Log.d("wsconnect", "send counter");
//                        }
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

        try {
            NotificationManager mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mManager.cancel(NOTICE_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 重启自己
        Intent intent = new Intent(getApplicationContext(),MyService.class);
        startService(intent);
        super.onDestroy();
    }

    /**
     * 需要手动刷新界面去查看是否有新通知
     * @param txt
     */
    public static void output(final String txt){
        String plan = txt.split("&")[1];
        dbHandler.addPlan(new Plans(new Date().getTime(), 7, 0,
                "指导员", plan, 1,1,1 ));

    }

    public static void wsConnect(){

        final String identifier = MyCookieJar.getLastCookie();

        if (identifier != null){

            String url = "ws://58.87.90.180:8080/newServer/websocket/server/stu_20154479";
            String url1 = "ws://58.87.90.180:8080/newServer/websocket/server/";
            String testUrl = "wss://echo.websocket.org";

            //Log.d("wsconnect", identifier);
            Request request = new Request.Builder().url(url1 + identifier).build();
            //Request request = new Request.Builder().url("wss://echo.websocket.org").build();
            MyWebSocketListener listener = new MyWebSocketListener();
            client = new OkHttpClient();
            webSocket = client.newWebSocket(request, listener);
            TestWebsocket.flag = "changed";
            Log.d("wsconnect", "service test flag " + TestWebsocket.flag);

            if (TestWebsocket.webSocket == null){
                Log.d("wsconnect", "wsconnect : is empty");
                TestWebsocket.webSocket = webSocket;
            }
            flag = "KingJoffrey";

            Log.d("wsconnect", "cookies: " + identifier);
            //webSocket.send("hello");


            //Log.d("wsconnect", "connection established and sent hello message");


        }
    }

    public static void sendChat(String text) {
        //Log.d("wsconnect", "in send Chat, start send " + text);
        try {
            if (webSocket == null) {
                Log.d("wsconnect", "empty");
            }
            webSocket.send(text);
            Log.d("wsconnect", "in send Chat, finish send " + text);
        } catch (Exception e){
            Log.d("wsconnect", e.toString());
        }

    }
}
