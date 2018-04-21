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

public class EditPlanActivity extends AppCompatActivity {

    private EditText dateInput;
    private EditText planInput;
    private Button addButton;
    private Button deleteButton;
    private TextView planBoard;
    private MyDBHandler dbHandler;

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
        addButton = findViewById(R.id.addButton);
        deleteButton = findViewById(R.id.deleteButton);
        dateInput = findViewById(R.id.editDate);
        planInput = findViewById(R.id.editPlan);
        planBoard = findViewById(R.id.planBoard);
        dbHandler = new MyDBHandler(this, null, null, 1);
        showTodaysPlans();

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

    }

    /**
     * Today I will simply test the function
     * further function, such show that particular day's plan will be added tomorrow
     * may be the day after tomorrow?
     * Alec Chen 20.4.2018 19:08
     * I am really a fucking working-hard boy, ah?
     */
    public void showTodaysPlans(){
        String todaysPlans = dbHandler.databaseToString();
        planBoard.setText(todaysPlans);
        dateInput.setText("");
        planInput.setText("");
    }


}
