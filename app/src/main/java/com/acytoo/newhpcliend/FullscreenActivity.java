package com.acytoo.newhpcliend;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Presentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
    private TextView plansText;
    private SimpleDateFormat df;
    private Calendar calendar;
    private static Calendar caForEnd;
    private TextView dateToday;
    private Level level;


    public enum Level{
        DAY, WEEK, MONTH
    }

    MyService myNewService;
    boolean isBound = false;

    private ServiceConnection myNewConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MyLocalBinder binder = (MyService.MyLocalBinder) service;
            myNewService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("testActivity", "onCreate");
        setContentView(R.layout.activity_fullscreen);
        init();

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
        plansText = findViewById(R.id.plansText);
        dateToday = findViewById(R.id.showDate);
        calendar = Calendar.getInstance();
        caForEnd = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.setTimeZone(TimeZone.getDefault());    //get your TimeZone
        calendar.set(Calendar.MILLISECOND, 0);  //We need to set the millisecond to 0
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        level = Level.DAY;
        startMyService();
        showNotification();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //String todayInfo = this.getString(R.string.todayInfo) + " " + df.format(new Date());
        String todayInfo = df.format(new Date());
        dateToday.setText(todayInfo);
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
        plansText.setText(plans);

    }

    private void startMyService(){
        if (!isBound) {
            //Start the service
            final Intent serviceIntent = new Intent(FullscreenActivity.this, MyService.class);
            startService(serviceIntent);
            bindService(serviceIntent, myNewConnection, Context.BIND_AUTO_CREATE);
            Log.i("counter", "is bound? " + isBound);
            //The service should started
        }
    }

    private void showNotification() {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, FullscreenActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, null)
                .setContentTitle("今日首要任务")
                .setContentText(dbHandler.getMostImportantToday(calendar.getTimeInMillis()))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentIntent(pendingIntent);
        Notification notification = builder.build();
        NotificationManager manager =(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1,notification);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("testActivity", "onDestroy");
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

        TextView dateToShow = findViewById(R.id.dateToShow);
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
        dateToShow.setText(dateOfShowingPlans);
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
        //testMessage.setText("onDoubleTap");
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
                    } else if (level == Level.WEEK) {
                        calendar.add(Calendar.WEEK_OF_YEAR, 1);
                    } else {
                        calendar.add(Calendar.MONTH, 1);
                    }
                    break;
                } else {
                    if (level == Level.DAY) {
                        calendar.add(Calendar.DATE, -1);
                    } else if (level == Level.WEEK) {
                        calendar.add(Calendar.WEEK_OF_YEAR, -1);
                    } else {
                        calendar.add(Calendar.MONTH, -1);
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
