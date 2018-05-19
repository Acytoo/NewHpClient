package com.acytoo.newhpcliend.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class FileManager {

    public FileManager(){}


    public void saveToInternal(Context context, String fileName, String content) {
        FileOutputStream outputStream;
        try {
            Log.d("ytsave", "start save login info to internal");
            outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(content.getBytes());
            Log.d("ytsave", "store content : " + content);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String loadFromInternal(Context context, String fileName) {
        String coo;
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fileInputStream = context.openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while ((coo = bufferedReader.readLine()) != null) {
                sb.append(coo);
            }
            fileInputStream.close();
            Log.d("ytsave", "read content : " + sb.toString());
            return sb.toString();
        } catch (Exception e) {
            Log.d("ytsave", "read errror " + e.toString());
            e.printStackTrace();
        }
        return null;

    }

    public static String encode(String password) {
        // MessageDigest专门用于加密的类
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(password.getBytes("UTF-8"));
            byte[] result = messageDigest.digest(); // 得到加密后的字符组数
            StringBuffer sb = new StringBuffer();
            for (byte b : result) {
                int num = b & 0xff; // 这里的是为了将原本是byte型的数向上提升为int型，从而使得原本的负数转为了正数
                String hex = Integer.toHexString(num); //这里将int型的数直接转换成16进制表示
                //16进制可能是为1的长度，这种情况下，需要在前面补0，
                if (hex.length() == 1) {
                    sb.append(0);
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
