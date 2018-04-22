package com.acytoo.newhpcliend;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyService extends Service {

    private final IBinder myBinder = new MyLocalBinder();

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

    public String getCurrentDate(){
        SimpleDateFormat df = new SimpleDateFormat("yy.MM.dd", Locale.CHINA);
        return df.format(new Date());
    }
}
