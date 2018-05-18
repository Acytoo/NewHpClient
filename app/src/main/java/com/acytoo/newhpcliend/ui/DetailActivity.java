package com.acytoo.newhpcliend.ui;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.acytoo.newhpcliend.R;
import com.acytoo.newhpcliend.utils.MyDBHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.acytoo.newhpcliend.ui.FullscreenActivity.getNextDayMillis;
import static com.acytoo.newhpcliend.ui.FullscreenActivity.getNextMonthMillis;
import static com.acytoo.newhpcliend.ui.FullscreenActivity.getNextWeekMillis;

public class DetailActivity extends AppCompatActivity {

    private SimpleDateFormat df;
    private Calendar calendar;
    //private static Calendar caForEnd;
    private ArrayList<String> mPlans = new ArrayList<>();
    private ArrayList<Integer> mID = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private FullscreenActivity.Level level;      //0, 1, 2, Day, Week, Month
    private MyDBHandler dbHandler;
    String plans;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        /*
         *Will edit later since the min sdk is 21
         */
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        }
        url = "https://acytoo.github.io/HPSRC/priority_";
        df = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        Bundle getDateInfo = getIntent().getExtras();
        calendar = Calendar.getInstance();
        //caForEnd = Calendar.getInstance();
        if (getDateInfo == null){
            calendar.setTime(new Date());
            level = FullscreenActivity.Level.DAY;
        }
        else{
            calendar.setTimeInMillis(getDateInfo.getLong("dateLong"));
            level = FullscreenActivity.Level.values()[getDateInfo.getInt("level")];
        }

        dbHandler = new MyDBHandler(this, null, null, 2);

        //initPlans();

    }

    public void clearArrayList(){
        mPlans.clear();
        mImageUrls.clear();
        mID.clear();
    }
    public void initPlans(){
        if (level == FullscreenActivity.Level.DAY){
            plans = dbHandler.getSomePlansSpecialFormat(calendar.getTimeInMillis(), getNextDayMillis(calendar.getTimeInMillis()));
        } else if (level == FullscreenActivity.Level.WEEK){
            plans = dbHandler.getSomePlansSpecialFormat(calendar.getTimeInMillis(), getNextWeekMillis(calendar.getTimeInMillis()));
        } else{
            plans = dbHandler.getSomePlansSpecialFormat(calendar.getTimeInMillis(), getNextMonthMillis(calendar.getTimeInMillis()));
        }

        String parts[] = plans.split("&");
        for (int i=0; i<parts.length; i++){
            mImageUrls.add(url + parts[i].split("#")[1] + ".png");
            mPlans.add(parts[i].split("#")[2]);
            mID.add(Integer.parseInt(parts[i].split("#")[0]));

        }
        initRecyclerView();
    }

    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mPlans, mImageUrls, mID);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        clearArrayList();
        initPlans();
    }
}
