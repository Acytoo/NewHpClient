package com.acytoo.newhpcliend.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.acytoo.newhpcliend.MyApplication;
import com.acytoo.newhpcliend.R;
import com.acytoo.newhpcliend.ui.FullscreenActivity;
import com.acytoo.newhpcliend.utils.MyCookieJar;
import com.acytoo.newhpcliend.utils.MyDBHandler;
import com.acytoo.newhpcliend.utils.MyWebSocketListener;
import com.acytoo.newhpcliend.utils.NetWorkStateReceiver;
import com.acytoo.newhpcliend.utils.Plans;

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
        builder.setContentTitle("KeepAppAlive");
        builder.setContentText("DaemonService is runing...");
        startForeground(NOTICE_ID,builder.build());
//         如果觉得常驻通知栏体验不好
//         可以通过启动CancelNoticeService，将通知移除，oom_adj值不变
        Intent intent = new Intent(this,CancelNoticeService.class);
        startService(intent);


        Log.d("netchanged", "finish create the servive");


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
        Log.d("netchanged", "in the Myservive on startCommod, after register the networkstate receiver");

        if (!started) {
            started = true;
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
                        Log.d("counter", "now the counter is " + counter);
                        counter++;
                    }
                }
            }).start();
        }
        Log.d("netchanged", "in onstartConmond, after if");

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("netchanged", "onDestroy called");
        interrupted = true;
        //client.dispatcher().executorService().shutdown();
        unregisterReceiver(netWorkStateReceiver);

        try {
            NotificationManager mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mManager.cancel(NOTICE_ID);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("netchanged", "my service on destroy exception " + e.toString());
        }


        // 重启自己
        Intent intent = new Intent(getApplicationContext(),MyService.class);
        startService(intent);
        super.onDestroy();
        Log.d("netchanged", "finish ondestroy");
    }

    public static void output(final String txt){
        String plan = txt.split("&")[1];
        dbHandler.addPlan(new Plans(new Date().getTime(), 7, 0,
                "websocket", plan, 1,1,1 ));
        Log.d("planWebTest", "finish writing db");

    }

    public static void wsConnect(){

        final String identifier = MyCookieJar.getLastCookie();
        Log.d("netchanged", "start the wsConnect serive");
        if (identifier != null){

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String url = "ws://58.78.90.180:8080/websocket/server/stu_20154444";
                    //"wss://echo.websocket.org"
                    WebSocket webSocket;
                    OkHttpClient client;

                    Request request = new Request.Builder().url(url).build();
                    MyWebSocketListener listener = new MyWebSocketListener();
                    client = new OkHttpClient();
                    webSocket = client.newWebSocket(request, listener);

                    Log.d("netchanged", "cookies: " + identifier);
                    //webSocket.send(MyCookieJar.getLastCookie());

                    /**
                     * 一共会有几个cookie?是否过期怎么判断
                     *
                     * 过期后如何处理？
                     * 是根据网址区分
                     * 掉线处理
                     */
                }
            }).start();
        }
        Log.d("netchanged", "finish the wsConnect serive");
    }
}
