package com.acytoo.newhpcliend;

import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpManager {

    private MyCookieJar cookieJar;
    private OkHttpClient okHttpClient;

    public HttpManager() {
        cookieJar = new MyCookieJar();
        okHttpClient = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();
    }



    public void doLogin(String username, String password) {
        String jsonStr = "{\n" +
                "    \"username\": \" " + username + "\",\n" +
                "    \"password\": \" " + password + "\"\n" +
                "}";
        Log.d("YLjson", jsonStr);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonStr);

        Request request = new Request.Builder()
                .url("https://reqres.in/api/users")
                .post(body)
                .build();

        Log.d("YLjson", "started");

        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            Log.d("YLjson", "result " + (response.body().string())); //json2pojo already explained
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doRegister(String stuID, String username, String password) {
        String jsonStr = "{\n" +
                "    \"stuID\": \" " + stuID + "\",\n" +
                "    \"username\": \" " + username + "\",\n" +
                "    \"password\": \" " + password + "\"\n" +
                "}";
        Log.d("YLjson", jsonStr);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonStr);

        Request request = new Request.Builder()
                .url("https://reqres.in/api/users")
                .post(body)
                .build();

        Log.d("YLjson", "started");

        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            Log.d("YLjson", "result " + (response.body().string())); //json2pojo already explained
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
