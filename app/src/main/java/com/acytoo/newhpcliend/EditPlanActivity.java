package com.acytoo.newhpcliend;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class EditPlanActivity extends AppCompatActivity {

    private EditText dateInput;
    private EditText planInput;
    private Button addButton;
    private Button deleteButton;
    private Button showAllButton;
    private TextView planBoard;
    private MyDBHandler dbHandler;
    //private String dateToEdit;
    private SimpleDateFormat df;
    private long planTimeInMillis;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_plan);

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
        df = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        Bundle getDateInfo = getIntent().getExtras();
        calendar = Calendar.getInstance();
        if (getDateInfo == null){
            calendar.setTime(new Date());
        }
        else{
            calendar.setTimeInMillis(getDateInfo.getLong("dateLong"));
        }

        addButton = findViewById(R.id.addButton);
        deleteButton = findViewById(R.id.deleteButton);
        showAllButton = findViewById(R.id.showAllButton);
        dateInput = findViewById(R.id.editDate);
        planInput = findViewById(R.id.editPlan);
        planBoard = findViewById(R.id.planBoard);
        dbHandler = new MyDBHandler(this, null, null, 2);
        //showTodaysPlans();
        //dateInput.setText(dateToEdit);
        dateInput.setText(df.format(calendar.getTime()));
        addButton.setOnClickListener(
                new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        //Log.i("nothing", dateInput.getText().toString());
                        planTimeInMillis = calendar.getTimeInMillis();
                        Log.i("nodate", df.format(calendar.getTime()));
                        Plans plan = new Plans(planTimeInMillis, 1, new Date().getTime(), "self", planInput.getText().toString(),
                                0, 0, 0);
                        dbHandler.addPlan(plan);
                        showTodaysPlans();
                    }
                }
        );

        deleteButton.setOnClickListener(
                new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        String deletePlan = planInput.getText().toString();
                        dbHandler.deletePlan(deletePlan);
                        showTodaysPlans();
                    }
                }
        );
        showAllButton.setOnClickListener(
                new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        showAllPlans();
                    }
                }
        );
        //Finish the listener for buttons

        dateInput.setInputType(InputType.TYPE_NULL);
        dateInput.setOnFocusChangeListener(
                new View.OnFocusChangeListener(){
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus){
                            calendar = showDatePickerDialog();
                            Log.i("calendar", "on focus change " + df.format(calendar.getTime()));
                            //planTimeInMillis = calendar.getTimeInMillis();

                        }
                    }
                }
        );
        dateInput.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        calendar = showDatePickerDialog();
                        Log.i("calendar", "on click " + df.format(calendar.getTime()));

                    }
                }
        );

    }

    public void showTodaysPlans(){
        String plans;
        plans = dbHandler.getSomePlans(calendar.getTimeInMillis(),calendar.getTimeInMillis()+24*3600*1000);
        planBoard.setText(plans);
        planInput.setText("");
    }
    public void showAllPlans(){
        String allPlans = dbHandler.databaseToString();
        planBoard.setText(allPlans);
    }


    private Calendar showDatePickerDialog() {
        final Calendar ca = Calendar.getInstance();
        new DatePickerDialog(EditPlanActivity.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub

                calendar.set(year, monthOfYear, dayOfMonth);
                //Log.i("calendar", df.format(ca.getTime()));

                String stringMon, stringDay;
                //Log.i("calendar", "those ints " + Integer.toString(dayOfMonth));
                if (monthOfYear+1 < 10){
                    stringMon = "0" + Integer.toString(monthOfYear+1);
                }
                else{
                    stringMon = Integer.toString(monthOfYear+1);
                }
                if (dayOfMonth < 10){
                    stringDay = "0" + Integer.toString(dayOfMonth);
                }
                else{
                    stringDay = Integer.toString(dayOfMonth);
                }
                //dateInput.setText(year+"/"+stringMon+"/"+stringDay);
                dateInput.setText(df.format(calendar.getTime()));
            }
        }, ca.get(Calendar.YEAR), ca.get(Calendar.MONTH), ca.get(Calendar.DAY_OF_MONTH)).show();
        return ca;

    }


}
