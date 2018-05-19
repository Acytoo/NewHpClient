package com.acytoo.newhpcliend.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.acytoo.newhpcliend.service.MyService;


public class NetWorkStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            //获得ConnectivityManager对象
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取所有网络连接的信息
            Network[] networks = connectivityManager.getAllNetworks();

            //用于存放网络连接信息
            String string = "";
            //通过循环将网络信息逐个取出来

            for (Network network : networks){
                //获取ConnectivityManager对象对应的NetworkInfo对象
                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
                //sb.append(networkInfo.getTypeName() + " connect is " + networkInfo.isConnected());
                string += networkInfo.getTypeName() + " connect is " + networkInfo.isConnected() + "\n";
                if (networkInfo.isConnected()) {
                    MyService.wsConnect();
                    Log.d("netchanged", "internet connected");
                    return;
                }
            }
            Toast.makeText(context, "failed to connect to the network, you won't receive new message", Toast.LENGTH_LONG).show();
            Toast.makeText(context, string, Toast.LENGTH_LONG).show();

            //start the re sign method here.
        } catch (Exception e){
            e.printStackTrace();
        }
        Log.d("netchanged", "finish the network detector");
    }

}
