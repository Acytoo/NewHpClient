package com.acytoo.newhpcliend.ui;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.acytoo.newhpcliend.MyApplication;
import com.acytoo.newhpcliend.R;
import com.acytoo.newhpcliend.service.MyService;
import com.acytoo.newhpcliend.utils.FileManager;
import com.acytoo.newhpcliend.utils.HttpManager;
import com.acytoo.newhpcliend.utils.MyDBHandler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static java.lang.StrictMath.abs;

/**
 * Current I use double click on the main screen
 * Will changed to setting in the future, including a screen
 * of you login or not, and some plugins setting
 * I decide to make the weather and someother function as plugins,
 * there plugins are small apps that don't have a start icon and can be laughed from my main app
 * working alone is fucking good, especially when all other teammates all working on server part.
 *
 * Ale Chen 20.4.2018 17.05
 *
 * Maybe we need a check for date input
 *
 * I mean the date input must have a fixed format
 *
 * Alec Chen 20.4.2018 17.07
 *
 * So this app will get tht current date on onStart(), then the date is s very important parameter,
 * I can calculate the date I will display using this date.
 * Alec Chen 21.4.2018 13:40
 * 显示的信息以listview的形式展示出来， 方便检测点击的是哪个项目
 *
 * 单击为修改， 以及查看详细（详细包括优先级， 闹钟，自动删除， 来源）
 *
 * 除了显示， 其他部分都是使用long类型表示
 *
 */

public class FullscreenActivity extends AppCompatActivity implements GestureDetector.OnGestureListener,
GestureDetector.OnDoubleTapListener{

    private GestureDetectorCompat gestureDetector;
    private MyDBHandler dbHandler;
    private TextView txt_plan_board;
    private SimpleDateFormat df;
    private Calendar calendar;
    private static Calendar caForEnd;
    private Level level;
    private boolean login;
    public Handler temperatureHandler;
    private TextView txt_temperature;
    private TextView txt_welcome;
    private int color;
    private ConstraintLayout layout;





    public enum Level{
        DAY, WEEK, MONTH
    }
//
//    LocationListener locationListener =  new LocationListener() {
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle arg2) {
//
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//
//        }
//
//        @Override
//        public void onLocationChanged(Location location) {
//            //如果位置发生变化,重新显示
//            showLocation(location);
//
//        }
//    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("testActivity", "onCreate");
        setContentView(R.layout.activity_fullscreen);
        layout = findViewById(R.id.main_layout);
        color = 0xff000000;

        init();

        temperatureHandler = new Handler();
        final HttpManager httpManager = new HttpManager();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String weather = httpManager.getTemperature();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txt_temperature.setText(weather);
                        }
                    });
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();





//        try {
//            //获取地理位置管理器
//            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            //获取所有可用的位置提供器
//            List<String> providers = locationManager.getProviders(true);
//            if (providers.contains(LocationManager.GPS_PROVIDER)) {
//                //如果是GPS
//                locationProvider = LocationManager.GPS_PROVIDER;
//            } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
//                //如果是Network
//                locationProvider = LocationManager.NETWORK_PROVIDER;
//            } else {
//                Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            //获取Location
//            Location location = locationManager.getLastKnownLocation(locationProvider);
//            if (location != null) {
//                //不为空,显示地理位置经纬度
//                showLocation(location);
//            }
//            //监视地理位置变化
//            locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
//        } catch (SecurityException e){
//            e.printStackTrace();
//            Log.d("location", e.toString());
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//            Log.d("location", "hello "+e.toString());
//        }

    }

    private void showLocation(Location location){
        String locationStr = "维度：" + location.getLatitude() +"\n"
                + "经度：" + location.getLongitude();
        Log.d("location", locationStr);
    }





    private void init(){
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
        dbHandler = new MyDBHandler(this, null, null, 2);
        this.gestureDetector = new GestureDetectorCompat(this, this);
        gestureDetector.setOnDoubleTapListener(this);
        df = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        txt_plan_board = findViewById(R.id.txt_plan_board);
        txt_temperature = findViewById(R.id.txt_temperature);
        txt_welcome = findViewById(R.id.txt_welcome);
        calendar = Calendar.getInstance();
        caForEnd = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.setTimeZone(TimeZone.getDefault());    //get your TimeZone
        calendar.set(Calendar.MILLISECOND, 0);  //We need to set the millisecond to 0
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        level = Level.DAY;
        login = false;
        startMyService();
        showNotification();




    }

    @Override
    protected void onStart() {

        super.onStart();
        layout.setBackgroundColor(color);
        Log.d("color", "color is " + color);
        Log.i("testActivity", "onStart");
        setTitle();
        String plans;
        if (level == Level.DAY){
            plans = dbHandler.getSomePlansNoDate(calendar.getTimeInMillis(),getNextDayMillis(calendar.getTimeInMillis()));
        } else if (level == Level.WEEK){
            plans = dbHandler.getSomePlans(calendar.getTimeInMillis(),getNextWeekMillis(calendar.getTimeInMillis()));
        } else{
            plans = dbHandler.getSomePlans(calendar.getTimeInMillis(),getNextMonthMillis(calendar.getTimeInMillis()));
        }
        txt_plan_board.setText(plans);

        FileManager fileManager = new FileManager();
        String loginInfo = fileManager.loadFromInternal(this, "login.yl");
        if (loginInfo != null) {
            login = true;
            txt_welcome.setText("Welcome: " + loginInfo);
        }
        String slogan = fileManager.loadFromInternal(this, "slogan");
        if (slogan != null) {
            TextView txt_the_climb = findViewById(R.id.txt_the_climb);
            txt_the_climb.setText(slogan);
        }


    }

    private void startMyService(){

            final Intent serviceIntent = new Intent(FullscreenActivity.this, MyService.class);
            startService(serviceIntent);
    }

    private void showNotification() {

        try {
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, FullscreenActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, null)
                    .setContentTitle("今日首要任务")
                    .setContentText(dbHandler.getMostImportantToday(calendar.getTimeInMillis()))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setContentIntent(pendingIntent);
            Notification notification = builder.build();
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(1, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("testActivity", "onDestroy");
//        if (locationManager != null) {
//            locationManager.removeUpdates(locationListener);
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("testActivity", "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("testActivity", "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("testActivity", "onResume");
    }


    public static long getLastDayMillis(long givenMillis){
        caForEnd.setTimeInMillis(givenMillis);
        caForEnd.add(Calendar.DATE, -1);
        return caForEnd.getTimeInMillis();
    }
    public static long getNextDayMillis(long givenMillis){
        caForEnd.setTimeInMillis(givenMillis);
        caForEnd.add(Calendar.DATE, 1);
        return caForEnd.getTimeInMillis();
    }
    public static long getLastWeekMillis(long givenMillis){
        caForEnd.setTimeInMillis(givenMillis);
        caForEnd.add(Calendar.WEEK_OF_YEAR, -1);
        return caForEnd.getTimeInMillis();
    }
    public static long getNextWeekMillis(long givenMillis){
        caForEnd.setTimeInMillis(givenMillis);
        caForEnd.add(Calendar.WEEK_OF_YEAR, 1);
        return caForEnd.getTimeInMillis();
    }
    public static long getLastMonthMillis(long givenMillis){
        caForEnd.setTimeInMillis(givenMillis);
        caForEnd.add(Calendar.MONTH, -1);
        return caForEnd.getTimeInMillis();
    }
    public static long getNextMonthMillis(long givenMillis){
        caForEnd.setTimeInMillis(givenMillis);
        caForEnd.add(Calendar.MONTH, 1);
        return caForEnd.getTimeInMillis();
    }


    public void setTitle(){

        TextView txt_plan_date = findViewById(R.id.txt_plan_date);
        String dateOfShowingPlans;
        if (level == Level.DAY) {
            dateOfShowingPlans = df.format(calendar.getTime()) + this.getString(R.string.dateToShow);
        } else if (level == Level.WEEK){
            //dateOfShowingPlans = "Plans during week\n" + df.format(calendar.getTime());
            caForEnd.setTimeInMillis(getNextWeekMillis(calendar.getTimeInMillis()));
            dateOfShowingPlans = df.format(calendar.getTime()) + " - " + df.format(caForEnd.getTime());
        } else {
            //dateOfShowingPlans = "Plans during month\n" + df.format(calendar.getTime());
            caForEnd.setTimeInMillis(getNextMonthMillis(calendar.getTimeInMillis()));
            dateOfShowingPlans = df.format(calendar.getTime()) + " - " + df.format(caForEnd.getTime());
        }
        txt_plan_date.setText(dateOfShowingPlans);
    }


    /**
     * start the detailActivity, which show the plans of the given day in detail,
     * and hold the entry of edit, delete, add.
     * Alec Chen /08/05/2018
     * @param e
     * @return
     */

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Intent detailActivity = new Intent();
        detailActivity.setClass(this, DetailActivity.class);
        detailActivity.putExtra("level", level.ordinal());
        detailActivity.putExtra("dateLong", calendar.getTimeInMillis());
        startActivity(detailActivity);
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return true;
    }

    /**
     * When double click on the main screen, we will go to setting activity,
     * and login will be an item in setting
     *
     * Alec Chen
     * 25/4/2018
     * @param e
     * @return
     */
    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        //testMessage.setText("onDoubleTapEvent");

        /*
        Intent loginActivity = new Intent();
        loginActivity.setClass(FullscreenActivity.this, LoginActivity.class);
        startActivity(loginActivity);*/

        /*

        Intent settingActivity = new Intent();
        Log.d("counter", myNewService.getCurrentDate());

        settingActivity.setClass(FullscreenActivity.this, SettingsActivity.class);
        startActivity(settingActivity);*/

        Intent preferenceActivity = new Intent (MyApplication.getInstance(), PreferenceActivity.class);
        startActivity(preferenceActivity);
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        //testMessage.setText("onDown");
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        //testMessage.setText("onShowPress");

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //testMessage.setText("onScroll");
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        //testMessage.setText("onLongPress");
        Intent addActivity = new Intent(FullscreenActivity.this, AddPlanActivity.class);
        addActivity.putExtra("dateLong", calendar.getTimeInMillis());
        startActivity(addActivity);
    }


    /**
     * Modified method, hope to call onStart() or other function
     * including animation such as slide, but no other activity
     * since the app seems unstable if we create it too much
     * @param e1
     * @param e2
     * @param velocityX
     * @param velocityY
     * @return
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {


        double minDistance = 120.0;
        float moveX = e1.getX(0) - e2.getX(0);
        float moveY = e1.getY(0) - e2.getY(0);

        //Calculate the direction user's finger fling, if its horizontal: Flag = 1, else, Flag = 0

        double absX = abs(moveX);
        double absY = abs(moveY);
        Intent anotherDay = new Intent();
        anotherDay.setClass(FullscreenActivity.this, FullscreenActivity.class);

        while (absX > minDistance || absY > minDistance) {
            if (absX > absY) {
                if (moveX > 0) {
                    if (level == Level.DAY) {
                        calendar.add(Calendar.DATE, 1);
                        color += 0x0000002f;
                        //color = (color + 0x0000002f) % 0xffffffff;
                    } else if (level == Level.WEEK) {
                        calendar.add(Calendar.WEEK_OF_YEAR, 1);
                        color += 0x00002f00;
                        //color = (color + 0x00000f0f) % 0xffffffff;
                    } else {
                        calendar.add(Calendar.MONTH, 1);
                        color += 0x002f0000;
                        //color = (color + 0x000f0f0f) % 0xffffffff;
                    }
                    break;
                } else {
                    if (level == Level.DAY) {
                        calendar.add(Calendar.DATE, -1);
                        color -= 0x0000002f;
                        //color = (color + 0xffffffe0) % 0xffffffff;
                    } else if (level == Level.WEEK) {
                        calendar.add(Calendar.WEEK_OF_YEAR, -1);
                        color -= 0x00002f00;
                        //color = (color + 0xfffff0f0) % 0xffffffff;
                    } else {
                        calendar.add(Calendar.MONTH, -1);
                        color -= 0x002f0000;
                        //color = (color + 0xfff0f0f0) % 0xffffffff;
                    }
                    break;
                }
            }
            //Math.abs(level.ordinal()+2)%3 = Math.abs(level.ordinal()-1)%3
            //Alec Chen
            if (moveY > 0) {
                level = Level.values()[Math.abs(level.ordinal()+2)%3];
                //overridePendingTransition(R.anim.go_up, R.anim.go_up);
                break;
            }
            level = Level.values()[Math.abs(level.ordinal()+4)%3];
            //overridePendingTransition(R.anim.go_down, R.anim.go_down);
            break;
        }
        onStart();
        return true;

    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("no", null).show();
    }
}
