package com.acytoo.newhpcliend.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.acytoo.newhpcliend.MyApplication;
import com.acytoo.newhpcliend.R;
import com.acytoo.newhpcliend.utils.FileManager;
import com.acytoo.newhpcliend.utils.ImageSaver;

public class PreferenceActivity extends AppCompatActivity {

    private ImageView img_profile;
    private Button btn_login;
    private TextView txt_sid_preference;
    private TextView txt_name_preference;
    private TextView txt_school_preference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }


        txt_name_preference = findViewById(R.id.txt_name_preference);
        txt_school_preference = findViewById(R.id.txt_school_preference);
        txt_sid_preference = findViewById(R.id.txt_sid_preference);
        img_profile = findViewById(R.id.img_profile);
        btn_login = findViewById(R.id.btn_login);


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginActivity = new Intent(MyApplication.getInstance(), LoginActivity.class);
                startActivity(loginActivity);
            }
        });
//
//        FileManager fileManager = new FileManager();
//        String loginInfo = fileManager.loadFromInternal(this, "login.yl");
//        if (loginInfo != null){
//            try {
//                Bitmap bitmap = new ImageSaver(MyApplication.getInstance()).
//                        setFileName("profile.png").
//                        setDirectoryName("images").
//                        load();
//
//                Log.d("yllogin", "set Image");
//                img_profile.setImageBitmap(bitmap);
//            } catch (Exception e){
//                Log.d("yllogin", e.toString());
//            }

//        }


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d("ytcycle", "onPostResume");
        FileManager fileManager = new FileManager();
        String loginInfo = fileManager.loadFromInternal(this, "login.yl");
        if (loginInfo != null){
            try {
                Bitmap bitmap = new ImageSaver(MyApplication.getInstance()).
                        setFileName("profile.png").
                        setDirectoryName("images").
                        load();

                Log.d("yllogin", "set Image");
                img_profile.setImageBitmap(bitmap);
            } catch (Exception e){
                Log.d("yllogin", e.toString());
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ytcycle", "onResume");
        FileManager fileManager = new FileManager();
        String loginInfo = fileManager.loadFromInternal(this, "login.yl");
        if (loginInfo != null){
            try {
                Bitmap bitmap = new ImageSaver(MyApplication.getInstance()).
                        setFileName("profile.png").
                        setDirectoryName("images").
                        load();

                Log.d("yllogin", "set Image");
                img_profile.setImageBitmap(bitmap);
            } catch (Exception e){
                Log.d("yllogin", e.toString());
            }

        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.d("ytcycle", "onRestart");
//        FileManager fileManager = new FileManager();
//        String loginInfo = fileManager.loadFromInternal(this, "login.yl");
//        if (loginInfo != null){
//            try {
//                Bitmap bitmap = new ImageSaver(MyApplication.getInstance()).
//                        setFileName("profile.png").
//                        setDirectoryName("images").
//                        load();
//
//                Log.d("yllogin", "set Image");
//                img_profile.setImageBitmap(bitmap);
//            } catch (Exception e){
//                Log.d("yllogin", e.toString());
//            }

//        }
//    }

    //    @Override
//    protected void onResume() {
//        super.onResume();
//
//
//
//    }

}
