package com.acytoo.newhpcliend;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
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
    private String dateToEdit;
    private SimpleDateFormat df;

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
        df = new SimpleDateFormat("yy.MM.dd", Locale.CHINA);
        Bundle getDateInfo = getIntent().getExtras();
        if (getDateInfo == null){
            dateToEdit = df.format(new Date());
        }
        else{
            dateToEdit = df.format(new Date(getDateInfo.getLong("dateToEdit")));
        }
        addButton = findViewById(R.id.addButton);
        deleteButton = findViewById(R.id.deleteButton);
        showAllButton = findViewById(R.id.showAllButton);
        dateInput = findViewById(R.id.editDate);
        planInput = findViewById(R.id.editPlan);
        planBoard = findViewById(R.id.planBoard);
        dbHandler = new MyDBHandler(this, null, null, 1);
        showTodaysPlans();
        dateInput.setText(dateToEdit);
        addButton.setOnClickListener(
                new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Log.i("nothing", dateInput.getText().toString());
                        Plans plan = new Plans(dateInput.getText().toString(), planInput.getText().toString());
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

    }

    /**
     * Today I will simply test the function
     * further function, such show that particular day's plan will be added tomorrow
     * may be the day after tomorrow?
     * Alec Chen 20.4.2018 19:08
     * I am really a fucking working-hard boy, ah?
     */
    public void showTodaysPlans(){
        String todaysPlans = dbHandler.getDatePlans(dateToEdit);
        planBoard.setText(todaysPlans);
        planInput.setText("");
    }
    public void showAllPlans(){
        String allPlans = dbHandler.databaseToString();
        planBoard.setText(allPlans);
    }


}
