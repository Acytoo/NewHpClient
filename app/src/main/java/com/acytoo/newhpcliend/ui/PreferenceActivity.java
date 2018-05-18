package com.acytoo.newhpcliend.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.acytoo.newhpcliend.MyApplication;
import com.acytoo.newhpcliend.R;
import com.acytoo.newhpcliend.utils.FileManager;
import com.acytoo.newhpcliend.utils.ImageSaver;

public class PreferenceActivity extends AppCompatActivity {

    private ImageView img_profile;
    private Button btn_login;


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


        img_profile = findViewById(R.id.img_profile);
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginActivity = new Intent(MyApplication.getInstance(), LoginActivity.class);
                startActivity(loginActivity);
            }
        });


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        FileManager fileManager = new FileManager();
        String loginInfo = fileManager.loadFromInternal(this, "login.yl");
        if (loginInfo != null){
            try {
                Bitmap bitmap = new ImageSaver(MyApplication.getInstance()).
                        setFileName("profile.png").
                        setDirectoryName("images").
                        load();

                img_profile.setImageBitmap(bitmap);
            } catch (Exception e){
                Log.d("yllogin", e.toString());
            }
        }

    }

}
