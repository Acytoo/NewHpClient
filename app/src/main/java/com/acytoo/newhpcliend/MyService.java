package com.acytoo.newhpcliend;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Intent;
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
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!started) {
            started = true;
            new Thread(new Runnable() {
                @Override
                public void run() {

                    dbHandler = new MyDBHandler(MyApplication.getInstance(), null, null, 2);
                    Request request = new Request.Builder().url("wss://echo.websocket.org").build();
                    MyWebSocketListener listener = new MyWebSocketListener();
                    client = new OkHttpClient();
                    webSocket = client.newWebSocket(request, listener);
                    webSocket.send("yet another plan");

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
        super.onDestroy();
    }

    public static void output(final String txt){
        String plan = txt.split("&")[1];
        dbHandler.addPlan(new Plans(new Date().getTime(), 7, 0,
                "websocket", plan, 1,1,1 ));
        Log.d("planWebTest", "finish writing db");

    }
}
