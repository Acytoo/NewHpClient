package com.acytoo.newhpcliend;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;

import static java.lang.StrictMath.abs;

public class FullscreenActivity extends AppCompatActivity implements GestureDetector.OnGestureListener,
GestureDetector.OnDoubleTapListener{

    private TextView testMessage;
    private GestureDetectorCompat gestureDetector;



    MyService myNewService; //This will be the pointer to the new service.
    boolean isBound = false;

    public void showTime(){
        String currentTime = myNewService.getCurrentTime();
        Context myNewContext = getApplicationContext();

        Toast myNewToast = Toast.makeText(myNewContext, currentTime, Toast.LENGTH_LONG);
        myNewToast.show();
    }


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





    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        testMessage = findViewById(R.id.fullscreen_content);
        this.gestureDetector = new GestureDetectorCompat(this, this);
        gestureDetector.setOnDoubleTapListener(this);




        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);



        final Intent serviceIntent = new Intent(FullscreenActivity.this, MyService.class);
        bindService(serviceIntent, myNewConnection, Context.BIND_AUTO_CREATE);
        //The service should started.





        Date c = Calendar.getInstance().getTime();
        String temp = c.toString();
        TextView dateToday = findViewById(R.id.showDate);
        dateToday.setText(temp);

        Bundle receivedInfo = getIntent().getExtras();
        String dateInfo = "Today";
        if (receivedInfo == null)
            return;
        dateInfo = receivedInfo.getString("dateInfo");
        TextView dateToShow = findViewById(R.id.dateToShow);
        dateToShow.setText(dateInfo);


    }


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


        Context context = getApplicationContext();
        CharSequence text = "Now you can make a new plan";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();



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
                    anotherDay.putExtra("dateInfo", "Tomorrow");
                    startActivity(anotherDay);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                    break;
                } else {
                    anotherDay.putExtra("dateInfo", "Yesterday");
                    startActivity(anotherDay);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                    break;
                }
            }
            if (moveY > 0){
                anotherDay.putExtra("dateInfo", "Level Down");
                startActivity(anotherDay);
                overridePendingTransition(R.anim.go_up, R.anim.go_up);
                break;
            }
            anotherDay.putExtra("dateInfo", "Level Up");
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
