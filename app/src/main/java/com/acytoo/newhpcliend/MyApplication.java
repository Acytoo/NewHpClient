package com.acytoo.newhpcliend;

/**
 * 这是一个Application类， 用来提供程序运行时的context
 */

public class MyApplication extends android.app.Application {
    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        instance = this;
    }
}