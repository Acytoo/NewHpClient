package com.acytoo.newhpcliend;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class EditPlanActivity extends AppCompatActivity {

    private EditText dateInput;
    private EditText planInput;
    private EditText timeInput;
    private Button btn_edit;
    private Button deleteButton;
    private TextView planBoard;
    private Spinner prioritySpinner;
    private MyDBHandler dbHandler;
    private SimpleDateFormat df;
    private SimpleDateFormat timedf;
    private long planTimeInMillis;
    private Calendar calendar;
    private int planPriority;
    private Switch doneSwitch;
    private Switch autoDeleteSwitch;
    enum DoneFlag {
        False, True
    }
    enum AutoDeleteFlag {
        False, True
    }
    private DoneFlag done;
    private AutoDeleteFlag autoDelete;
    private int id;

    /**
     * in future, autoDelete method can be put in onStart(), so we don't need
     * to call it manually.
     *
     * @param savedInstanceState
     */

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
        Log.i("alec", "onCreate");
        df = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        timedf = new SimpleDateFormat("h:mm a", Locale.CHINA);
        Bundle getIDInfo = getIntent().getExtras();
        calendar = Calendar.getInstance();
        if (getIDInfo == null){
            id = -1;
        }
        else{
            id = getIDInfo.getInt("id");
            Log.i("recid", "id :" + id);
        }
        planPriority = 0;
        done = DoneFlag.False;
        autoDelete = AutoDeleteFlag.False;

        btn_edit = findViewById(R.id.btn_edit);
        deleteButton = findViewById(R.id.deleteButton);
        dateInput = findViewById(R.id.editDate);
        planInput = findViewById(R.id.editPlan);
        timeInput = findViewById(R.id.editTime);
        planBoard = findViewById(R.id.planBoard);
        doneSwitch = findViewById(R.id.doneSwitch);
        autoDeleteSwitch = findViewById(R.id.autoDeleteSwitch);

        dbHandler = new MyDBHandler(this, null, null, 2);

        prioritySpinner = findViewById(R.id.prioritySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priority_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapter);

        init(id);

        prioritySpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        planPriority = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

        Log.i("alec", "after init");
        btn_edit.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Log.i("alec", "before if");
                        if (legal()){
                            Log.i("alec", "onClickListener");
                            planTimeInMillis = calendar.getTimeInMillis();
                            Plans plan = new Plans(planTimeInMillis, planPriority, new Date().getTime(), "self",
                                    planInput.getText().toString(),
                                    done.ordinal(), autoDelete.ordinal(), 0);
                            dbHandler.editPlan(id,plan);
                            showTodaysPlans();
                        }
                        else {
                            //planInput.setText("Please enter your plan here");
                        }
                    }
                }
        );

        deleteButton.setOnClickListener(
                new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        dbHandler.deleteById(id);
                        showTodaysPlans();
                    }
                }
        );


        dateInput.setInputType(InputType.TYPE_NULL);
        dateInput.setOnFocusChangeListener(
                new View.OnFocusChangeListener(){
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus){
                            showDatePickerDialog();
                        }
                    }
                }
        );
        dateInput.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        showDatePickerDialog();

                    }
                }
        );

        timeInput.setInputType(InputType.TYPE_NULL);
        timeInput.setOnFocusChangeListener(
                new View.OnFocusChangeListener(){
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus){
                            showTimePickerDialog();
                        }
                    }
                }
        );
        timeInput.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        showTimePickerDialog();
                    }
                }
        );

        doneSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            done = DoneFlag.True;
                        }
                        else{
                            done = DoneFlag.False;
                        }
                    }
                }
        );

        autoDeleteSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            autoDelete = AutoDeleteFlag.True;
                        }
                        else{
                            autoDelete = AutoDeleteFlag.False;
                        }
                    }
                }
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        showTodaysPlans();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        showTodaysPlans();
    }
    @Override
    protected void onResume(){
        super.onResume();
        showAllPlans();
    }

    public void init(int id){
        Log.i("alec", "init");
        String planSet = dbHandler.getPlanById(id);
        String parts[] = planSet.split("#");
        //Long planSetTimeMill = Long.parseLong(parts[0]);
        //int priority = Integer.parseInt(parts[1]);
        //String _todo = parts[2];
        //int done = Integer.parseInt(parts[3]);
        //int autoDelete = Integer.parseInt(parts[4]);

        calendar.setTimeInMillis(Long.parseLong(parts[0]));
        dateInput.setText(df.format(calendar.getTime()));
        timeInput.setText(timedf.format(calendar.getTime()));
        prioritySpinner.setSelection(Integer.parseInt(parts[1]));
        planInput.setText(parts[2]);
        doneSwitch.setChecked(Integer.parseInt(parts[3]) == 1);
        autoDeleteSwitch.setChecked(Integer.parseInt(parts[4]) == 1);
    }


    public void showTodaysPlans(){
        String plans;
        plans = dbHandler.getSomePlans(calendar.getTimeInMillis(),calendar.getTimeInMillis()+24*3600*1000);
        planBoard.setText(plans);
        //planInput.setText("");
    }
    public void showAllPlans(){
        String allPlans = dbHandler.databaseToString();
        planBoard.setText(allPlans);
    }
    private void showDatePickerDialog() {
        final Calendar ca = Calendar.getInstance();
        new DatePickerDialog(EditPlanActivity.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                calendar.set(year, monthOfYear, dayOfMonth);
                dateInput.setText(df.format(calendar.getTime()));
            }
        }, ca.get(Calendar.YEAR), ca.get(Calendar.MONTH), ca.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePickerDialog(){
        final Calendar ca = Calendar.getInstance();
        int hour = ca.get(Calendar.HOUR_OF_DAY);
        int minute = ca.get(Calendar.MINUTE);
        new TimePickerDialog(EditPlanActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                timeInput.setText(timedf.format(calendar.getTime()));
            }
        },hour,minute,false).show();
    }

    private boolean legal(){
        return (planInput.getText() != null);

    }


}
