package com.acytoo.newhpcliend.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageSaver {



    private String directoryName = "images";
    private String fileName = "image.png";
    private Context context;
    private boolean external;

    public ImageSaver(Context context) {
        this.context = context;
    }

    public ImageSaver setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public ImageSaver setExternal(boolean external) {
        this.external = external;
        return this;
    }

    public ImageSaver setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
        return this;
    }

    public void save(Bitmap bitmapImage) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(createFile());
            //Log.d("yllogin", "size " + bitmapImage.getDensity());
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            Log.d("yllogin", "finish save");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("yllogin", "1234123421341234123" + e.toString());
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("yllogin", e.toString());
            }
        }
    }

    @NonNull
    private File createFile() {

        File directory;
        if(external){
            Log.d("yllogin", "finsih createfile789");
            directory = getAlbumStorageDir(directoryName);
        }
        else {
            Log.d("yllogin", "finsih createfile123");
            directory = context.getDir(directoryName, Context.MODE_PRIVATE);
        }
        if(!directory.exists() && !directory.mkdirs()){
            Log.e("yllogin","Error creating directory " + directory);
        }
        Log.d("yllogin", "finsih createfile");
        return new File(directory, fileName);
    }

    private File getAlbumStorageDir(String albumName) {
        return new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }


    public Bitmap load() {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(createFile());
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
