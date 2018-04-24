package com.acytoo.newhpcliend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**This handler handle the database, the database for infomation
 * such as everyday plan, I will give a example
 *  ID      DATE        TODOs
 *  0       2018.4.1    wash clothes
 *  1       2018.4.1    have a new hair cut
 *  2       2018.4.2    go to gym
 *  3       2018.4.6    english test
 *
 *  Alec Chen 20 4 2018 16:33
 *  King Acytoo
 private int _id;            //id, the primary key
 private long _plan_time;      //the date of your plan
 private int _priority;       //priority, how important the plan is
 private long _plan_set_time;  //When did the user set his plan
 private String _source;      //Where does the plan come from, school or teacher, class or himself
 private String _todos;          //basically the content of the plan
 private boolean _done;          //if the plan is done, finished
 private boolean _auto_delete;   //if the plan can be deleted automatically
 private long _alarm_time;    //the time when the user may want an alarm to warn them
 */
public class MyDBHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 2;                    //Current I stop using this version.
    private static final String DATABASE_NAME = "info.db";             //must have a .db extension
    private static final String TABLE_PLANS = "plans";               //the table name of your info

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_PLAN_TIME = "_plan_time";
    private static final String COLUMN_PRIORITY = "_priority";
    private static final String COLUMN_PLAN_SET_TIME = "_plan_set_time";
    private static final String COLUMN_SOURCE = "_source";
    private static final String COLUMN_TODOS = "_todos";
    private static final String COLUMN_DONE = "_done";
    private static final String COLUMN_AUTO_DELETE = "_auto_delete";
    private static final String COLUMN_ALARM_TIME = "_alarm_time";

    //private Date date;
    private Calendar calendar;
    private SimpleDateFormat df;

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, version);
    }

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_PLANS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PLAN_TIME + " INTEGER, " +
                COLUMN_PRIORITY + " INTEGER, " +
                COLUMN_PLAN_SET_TIME + " INTEGER, " +
                COLUMN_SOURCE + " TEXT, " +
                COLUMN_TODOS + " TEXT, " +
                COLUMN_DONE + " INTEGER, " +
                COLUMN_AUTO_DELETE + " INTEGER, " +
                COLUMN_ALARM_TIME + " INTEGER " +
                ");";
        db.execSQL(query);
        Log.i("really", "here10");//why onCreate not running ? ? ?
        calendar = Calendar.getInstance();
        df = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //When you change the version(that means that you changed the structure of your database
        //This method will be called
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLANS);
        onCreate(db);
    }

    public void addPlan(Plans plan){
        ContentValues values = new ContentValues();

        values.put(COLUMN_PLAN_TIME, plan.get_plan_time());
        values.put(COLUMN_PRIORITY, plan.get_priority());
        values.put(COLUMN_PLAN_SET_TIME, new Date().getTime());
        values.put(COLUMN_SOURCE, plan.get_source());
        values.put(COLUMN_TODOS, plan.get_todos());
        values.put(COLUMN_DONE, plan.get_done());
        values.put(COLUMN_AUTO_DELETE, plan.get_auto_delete());
        values.put(COLUMN_ALARM_TIME, plan.get_alarm_time());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_PLANS, null, values);
        db.close();
    }

    public void deletePlan(String todo){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PLANS + " WHERE " + COLUMN_TODOS + "=\"" + todo + "\";" );
    }


    /**
     * Following method will return all the plans in a String for me to test the
     * database and will not be used in the matured app
     * Alec Chen 20 4 2018 17:03
     * I am hungry ...
     */
    public String databaseToString(){
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PLANS + " WHERE 1 ORDER BY "+ COLUMN_PLAN_TIME + " ASC;";
        //Cursor points to a location in your results
        Cursor c = db.rawQuery(query, null);
        //Move the cursor to the first row in your database
        c.moveToFirst();
        while (!c.isAfterLast()){
            if (c.getString(c.getColumnIndex(COLUMN_PLAN_TIME)) != null){
                Calendar ncalendar = Calendar.getInstance();
                SimpleDateFormat ndf = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
                ncalendar.setTimeInMillis(c.getLong(c.getColumnIndex(COLUMN_PLAN_TIME)));
                dbString += ndf.format(ncalendar.getTime()) + " " + c.getString(c.getColumnIndex(COLUMN_TODOS)) + "\n";
            }
            c.moveToNext();
        }
        c.close();      //Close the cursor and the database
        db.close();
        return dbString;
    }

    public String getDatePlans(long today){
        String datePlans = "";
        SQLiteDatabase db = getWritableDatabase();
        long tomorrow = today + 24 * 3600 * 1000;
        String query = "SELECT * FROM " + TABLE_PLANS + " WHERE " + COLUMN_PLAN_TIME + " >= " + today + " AND "
                + COLUMN_PLAN_TIME + " < " + tomorrow +
                " ORDER BY "+ COLUMN_PLAN_TIME + " ASC;";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while (!c.isAfterLast()){
            if (c.getString(c.getColumnIndex(COLUMN_TODOS)) != null){
                datePlans += c.getString(c.getColumnIndex(COLUMN_TODOS)) + "\n";
                //datePlans += "\n";
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        return datePlans;
    }

    public String getSomePlans(long startTime, long endTime){
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PLANS + " WHERE " + COLUMN_PLAN_TIME + " >= " + startTime + " AND "
                + COLUMN_PLAN_TIME + " < " + endTime +
                " ORDER BY "+ COLUMN_PLAN_TIME + " ASC;";
        Cursor c = db.rawQuery(query, null);
        //Move the cursor to the first row in your database
        c.moveToFirst();
        while (!c.isAfterLast()){
            if (c.getString(c.getColumnIndex(COLUMN_PLAN_TIME)) != null){
                Calendar tempca = Calendar.getInstance();
                SimpleDateFormat ndf = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
                tempca.setTimeInMillis(c.getLong(c.getColumnIndex(COLUMN_PLAN_TIME)));
                dbString += ndf.format(tempca.getTime()) + " " + c.getString(c.getColumnIndex(COLUMN_TODOS)) + "\n";
            }
            c.moveToNext();
        }
        c.close();      //Close the cursor and the database
        db.close();
        return dbString;
    }




}
