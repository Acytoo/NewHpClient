package com.acytoo.newhpcliend.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.acytoo.newhpcliend.MyApplication;
import com.acytoo.newhpcliend.ui.LoginActivity;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.acytoo.newhpcliend.utils.FileManager.encode;

public class HttpManager {

    //private MyCookieJar cookieJar;
    private OkHttpClient okHttpClient;

    public static Handler mainThreadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d("yllogin", "handle messge");
            super.handleMessage(msg);
            String messge = msg.obj.toString();
            //txt_result.setText(txt_result.getText() + "\n" + msg.obj.toString());
        }
    };

    public HttpManager() {
        Log.d("YLjson", "building new httpmanager");
        MyCookieJar myCookieJar = new MyCookieJar();
        okHttpClient = new OkHttpClient.Builder()
                .cookieJar(myCookieJar)
                .build();
    }

    public HttpManager(CookieJar cookieJar) {
        okHttpClient = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();
    }

    public void doLogin(String sid, String password) {
        FileManager fileManager = new FileManager();
        Log.d("YLjson", "building new httpmanager11111");
        String jsonStr = "{\n" +
                "    \"stuID\": \"" + sid + "\",\n" +
                "    \"password\": \"" + encode(password) + "\"\n" +
                "}";
        Log.d("YLjson", jsonStr);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonStr);

        Request request = new Request.Builder()
                .url("http://58.87.90.180:8080/newServer/LoginOfStu")
                .post(body)
                .build();

        Log.d("YLjson", "started");

        Response response;
        try {
            response = okHttpClient.newCall(request).execute();
            String result = response.body().string();
            if (result.equals("yes")){
                fileManager.saveToInternal(MyApplication.getInstance(), "login.yl", sid);
            }
            else {
                LoginActivity.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         * 此处根据返回值不同进行不同的提醒
                         */
                        Toast.makeText(MyApplication.getInstance(), "Fail to login: wrong user password", Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (IOException e) {
            Log.d("ytsave", "catch exception " + e.toString());

            e.printStackTrace();
        }
    }

    public void doRegister(String stuID, String username, String password, String captha) {
        Log.d("yllogin", "start login to server");

        String jsonStr = "{\n" +
                "    \"stuID\": \"" + stuID + "\",\n" +
                "    \"username\": \"" + username + "\",\n" +
                "    \"password\": \"" + encode(password) + "\"\n" +
                "}";
        Log.d("YLjson", jsonStr);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonStr);

        Request request = new Request.Builder()
                .url("http://58.87.90.180:8080/newServer/RegisterOfStu")
                .post(body)
                .build();

        Response response;
        try {
            response = okHttpClient.newCall(request).execute();
            Log.d("yllogin", "result:#" + (response.body().string()) + "#");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("yllogin", e.toString());
        }
    }

    public void loginAAO(final String uid, final String passwd, final String code) {

        String str = "https://zhjw.neu.edu.cn/ACTIONLOGON.APPPROCESS?mode=";
        FormBody form = new FormBody.Builder()
                .add("WebUserNO", uid)
                .add("Password", passwd)
                .add("Agnomen", code)
                .add("submit7", "%B5%C7%C2%BC")
                .build();
        try {
            Log.d("yllogin", "res " + (doSyncPost(okHttpClient, str, form)));
            updateResult(doSyncPost(okHttpClient, str, form));
        } catch (IOException e) {
            Log.d("yllogin", "error " + e.toString());
            e.printStackTrace();
        }
    }

    private void updateResult(String myResponse) {
        Message msg = Message.obtain();
        msg.obj = myResponse;
        mainThreadHandler.sendMessage(msg);
    }

    public String doSyncPost(OkHttpClient client, String url, RequestBody body) throws IOException {
        Log.d("yllogin", "url = " + url);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }



    public boolean doGetImage(String url) {

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            InputStream is = response.body().byteStream();
            Log.d("yllogin", "img size  " + is.read());
            if (is.read() < 20) {
                return false;
            }
            new ImageSaver(MyApplication.getInstance())
                    .setFileName("profile.png")
                    .setDirectoryName("images")
                    .save(BitmapFactory.decodeStream(is));
            return true;

        } catch (IOException e) {
            /**
             * 17:19 15/5/2018
             * 需崖判断登录教务处失败所获取的信息，
             * 或者图片获取失败的 信息
             *
             */
            e.printStackTrace();
            Log.d("yllogin", "degetimg : " + e.toString());
        }
        return false;
    }

    public Bitmap getCaptha(String url){

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            InputStream is = response.body().byteStream();
            return BitmapFactory.decodeStream(is);

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("yllogin", "degetimg : " + e.toString());
        }
        return null;
    }

}
