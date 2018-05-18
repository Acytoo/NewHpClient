package com.acytoo.newhpcliend.utils;

import android.content.Context;
import android.util.Log;

import com.acytoo.newhpcliend.MyApplication;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class MyCookieJar implements CookieJar {


    private List<Cookie> cookies;
    private static final String FILE_NAME = "HotelCalifornia";


    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        this.cookies = cookies;
        String storedCookie = "";
        Log.d("ytsave", "store cookies");
        Log.d("ytsave", "building new Cookie Jar");
        for (Cookie cookie : cookies) {
            storedCookie = cookie.name() + "~" + cookie.value() + "~" +
                    cookie.domain() + "~" + cookie.expiresAt() + "~" +
                    cookie.path() + "~" + cookie.secure() + "~" +
                    cookie.httpOnly() + "~" + cookie.hostOnly() + "~" + cookie.persistent() + "&";
        }

        FileOutputStream outputStream;
        try {
            outputStream = MyApplication.getInstance().openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            outputStream.write(storedCookie.getBytes());
            Log.d("ytsave", "store cookies : " + storedCookie.toString());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {

        if (cookies != null) {
            return cookies;
        }
        String coo;
        StringBuilder sb = new StringBuilder();
        List<Cookie> savedCookies = new ArrayList<Cookie>();
        try {
            FileInputStream fileInputStream = MyApplication.getInstance().openFileInput(FILE_NAME);
            if (fileInputStream == null){
                FileOutputStream fileOutputStream = MyApplication.getInstance().openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
                fileOutputStream.write("".getBytes());
                fileOutputStream.close();
                return new ArrayList<Cookie>();
            }
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while ((coo = bufferedReader.readLine()) != null) {
                sb.append(coo);
            }
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        coo = sb.toString();
        if (coo.length() < 2) {
            return new ArrayList<Cookie>();
        }
        String cookieSet[] = coo.split("&");
        for (String eachCookie : cookieSet) {
            Cookie tempCookie = null;
            try {
                tempCookie = new Cookie.Builder().name(eachCookie.split("~")[0]).value(eachCookie.split("~")[1])
                        .domain(eachCookie.split("~")[2]).expiresAt(Long.parseLong(eachCookie.split("~")[3]))
                        .path(eachCookie.split("~")[4]).secure().hostOnlyDomain(eachCookie.split("~")[2])
                        .build();
            } catch (Exception e){
                e.printStackTrace();
            }
            savedCookies.add(tempCookie);
        }
        return savedCookies;
    }

    /**
     * 会有多少个cookie?
     * @return
     */

    public static String getLastCookie(){
        String coo;
        StringBuilder sb = new StringBuilder();

        try {
            FileInputStream fileInputStream = MyApplication.getInstance().openFileInput(FILE_NAME);
            Log.d("netchanged", "it content is " + fileInputStream.available());


            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while ((coo = bufferedReader.readLine()) != null) {
                sb.append(coo);
            }
            fileInputStream.close();
            coo = sb.toString();
            return "_" + coo.split("~")[0] + "_" + coo.split("~")[1];
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("netchanged", e.toString());

        }
        return null;

    }

    /*
    private final Map<String, List<Cookie>> cookiesMap = new HashMap<String, List<Cookie> >();
    @Override
    public void saveFromResponse(HttpUrl arg0, List<Cookie> arg1) {
        Log.d("ytsave", "start save@#$@#$@#$@#$@#$@#$@#$@#$@");
        String host = arg0.host();
        cookiesMap.put(host, arg1);
        for(Cookie cookie:arg1) {
            if(cookie.name().equals("20154445") ) {
                Log.d("ytsave", "find cookies: " + cookie.value());
            }
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl arg0) {
        List<Cookie> cookiesList = cookiesMap.get(arg0.host() );
        return cookiesList != null ? cookiesList : new ArrayList<Cookie>();
    }*/
}
