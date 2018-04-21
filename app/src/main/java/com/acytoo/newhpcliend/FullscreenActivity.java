package com.acytoo.newhpcliend;


import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
 * 我他妈的就是天才， 就是， 其他的我都不听
 *
 */

public class FullscreenActivity extends AppCompatActivity implements GestureDetector.OnGestureListener,
GestureDetector.OnDoubleTapListener{

    private TextView testMessage;
    private GestureDetectorCompat gestureDetector;
    private MyDBHandler dbHandler;
    private TextView plansText;
    private SimpleDateFormat df;
    private Date receivedDate;

    MyService myNewService; //This will be the pointer to the new service.
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


        /*
         * Let's judge whether the sdk is higher than 21, since under android 5.0, there is no Immersive Mode
         * Then we can hide both the ugly bar.
         * Alec Chen 21.4.2018 13:43
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
        /*After creating the interface, we can add some other function
        This way make the app show faster.
         */

        dbHandler = new MyDBHandler(this, null, null, 1);

        /*
         * In the future this service might connect to the internet and fetch
         * the information, such as class arrangement
         * Alec Chen 20.4.2018 17.16
         */
        final Intent serviceIntent = new Intent(FullscreenActivity.this, MyService.class);
        bindService(serviceIntent, myNewConnection, Context.BIND_AUTO_CREATE);
        /*Then the service started, but it take time to start, so we'd better not using its service in onCreate*/
        df = new SimpleDateFormat("YY.MM.dd", Locale.CHINA);
        setContentView(R.layout.activity_fullscreen);
        testMessage = findViewById(R.id.fullscreen_content);
        plansText = findViewById(R.id.plansText);
        this.gestureDetector = new GestureDetectorCompat(this, this);
        gestureDetector.setOnDoubleTapListener(this);


        TextView dateToday = findViewById(R.id.showDate);
        String todayInfo = this.getString(R.string.todayInfo) + df.format(new Date());
        dateToday.setText(todayInfo);


        Bundle receivedDateInfo = getIntent().getExtras();
        if (receivedDateInfo == null){
            receivedDate = new Date();
        }
        else{
            receivedDate = new Date(receivedDateInfo.getLong("dateLong"));
        }

        TextView dateToShow = findViewById(R.id.dateToShow);
        String dateOfShowingPlans = df.format(receivedDate) + this.getString(R.string.dateToShow);
        dateToShow.setText(dateOfShowingPlans);

        showPlans(df.format(receivedDate));

    }
    @Override
    protected void onStart() {
        super.onStart();
        showPlans();
        showPlans(df.format(receivedDate));
    }


    public Date getLastDay(Date givenDate){
        /*When given a date, we can get the previous date*/
        Calendar ca = Calendar.getInstance();
        ca.setTime(givenDate);
        ca.add(Calendar.DATE, -1);
        return ca.getTime();
    }
    public Date getNextDay(Date givenDate){
        /*When given a date, we can get the following date*/
        Calendar ca = Calendar.getInstance();
        ca.setTime(givenDate);
        ca.add(Calendar.DATE, 1);
        return ca.getTime();
    }


    public void showTime(){
        String currentDate = myNewService.getCurrentDate();
        Context myNewContext = getApplicationContext();
        Toast myNewToast = Toast.makeText(myNewContext, currentDate, Toast.LENGTH_LONG);
        myNewToast.show();
    }

    public void showPlans(){
        String plans = dbHandler.databaseToString();
        testMessage.setText(plans);
    }

    public void showPlans(String dateString){
        String plans = dbHandler.getDatePlans(dateString);
        plansText.setText(plans);
    }









    // All the methods bellow are gesture detect functions



    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        testMessage.setText("onSingleTapConfirmed");
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        testMessage.setText("onDoubleTap");
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        testMessage.setText("onDoubleTapEvent");
        Intent loginActivity = new Intent();
        loginActivity.setClass(FullscreenActivity.this, LoginActivity.class);
        startActivity(loginActivity);
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        testMessage.setText("onDown");
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        testMessage.setText("onShowPress");

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        showPlans();
        showTime();
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        testMessage.setText("onScroll");
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        testMessage.setText("onLongPress");

        Intent editActivity = new Intent(FullscreenActivity.this, EditPlanActivity.class);
        startActivity(editActivity);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {


        /**
         * we can define a min move length and a min move velocity to decrease miss taken.
         * Alec Chen
         */
        double minDistance = 120.0;
        testMessage.setText("onFling");

        float moveX = e1.getX(0) - e2.getX(0);
        float moveY = e1.getY(0) - e2.getY(0);

        //Calculate the direction user's finger fling, if its horizontal: Flag = 1, else, Flag = 0


        //I hope there is a faster way to calculate this, though it may not important.
        // Alec Chen 2018 4 4 21.04
        double absX = abs(moveX);
        double absY = abs(moveY);
        Intent anotherDay = new Intent();
        anotherDay.setClass(FullscreenActivity.this, FullscreenActivity.class);

        while (absX > minDistance || absY > minDistance){
            if (absX > absY) {
                if (moveX > 0) {
                    //anotherDay.putExtra("dateInfo", "Tomorrow");
                    Date temp = getNextDay(receivedDate);
                    anotherDay.putExtra("dateLong", temp.getTime());
                    startActivity(anotherDay);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                    break;
                } else {
                    //anotherDay.putExtra("dateInfo", "Yesterday");
                    Date temp = getLastDay(receivedDate);
                    anotherDay.putExtra("dateLong", temp.getTime());
                    startActivity(anotherDay);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                    break;
                }
            }
            if (moveY > 0){
                //anotherDay.putExtra("dateInfo", "Level Down");
                startActivity(anotherDay);
                overridePendingTransition(R.anim.go_up, R.anim.go_up);
                break;
            }
            //anotherDay.putExtra("dateInfo", "Level Up");
            startActivity(anotherDay);
            overridePendingTransition(R.anim.go_down, R.anim.go_down);
            break;
        }

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
