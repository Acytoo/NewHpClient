package com.acytoo.newhpcliend.utils;

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

    private static final int DATABASE_VERSION = 2;                    //Current using this version.
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
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
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
    public void editPlan(int id, Plans plan){
        String query = "UPDATE " + TABLE_PLANS +
                " SET " +
                COLUMN_PLAN_TIME + " = " + plan.get_plan_time() + ", " +
                COLUMN_PRIORITY + " = " + plan.get_priority() + ", " +
                COLUMN_PLAN_SET_TIME + " = " + plan.get_plan_set_time() + ", " +
                COLUMN_TODOS + " = \"" + plan.get_todos() + " \", " +
                COLUMN_DONE + " = " + plan.get_done() + ", " +
                COLUMN_AUTO_DELETE + " = " + plan.get_auto_delete() +
                " WHERE " +
                COLUMN_ID + " = " + id;
        Log.i("alec", query);
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

    public void deletePlan(String todo){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PLANS + " WHERE " + COLUMN_TODOS + "=\"" + todo + "\";" );
        db.close();
    }

    public void deleteById(int id){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PLANS + " WHERE " + COLUMN_ID + " = " + id);
        db.close();
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
            if (c.getString(c.getColumnIndex(COLUMN_TODOS)) != null){
                Calendar ncalendar = Calendar.getInstance();
                SimpleDateFormat ndf = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
                ncalendar.setTimeInMillis(c.getLong(c.getColumnIndex(COLUMN_PLAN_TIME)));
                dbString += ndf.format(ncalendar.getTime()) + " " + c.getString(c.getColumnIndex(COLUMN_TODOS)) + "\n";
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        return dbString;
    }

    public String getMostImportantToday(long today){
        //the smaller priority number is, the more important it is.
        String topPlan = "";
        SQLiteDatabase db = getWritableDatabase();
        long tomorrow = today + 24 * 3600 * 1000;
        String query = "SELECT " + COLUMN_TODOS + " FROM " + TABLE_PLANS + " WHERE " + COLUMN_PLAN_TIME +
                " >= " + today + " AND " + COLUMN_PLAN_TIME + " < " + tomorrow +
                " ORDER BY "+ COLUMN_PRIORITY + " ASC;";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while (!c.isAfterLast()){
            topPlan = c.getString(c.getColumnIndex(COLUMN_TODOS));
            c.moveToLast();
            c.moveToNext();
        }
        c.close();
        db.close();
        return topPlan;
    }

    public String getDatePlans(long today){
        String datePlans = "";
        SQLiteDatabase db = getWritableDatabase();
        long tomorrow = today + 24 * 3600 * 1000;
        String query = "SELECT * FROM " + TABLE_PLANS + " WHERE " + COLUMN_PLAN_TIME + " >= " + today + " AND "
                + COLUMN_PLAN_TIME + " < " + tomorrow +
                " ORDER BY "+ COLUMN_PRIORITY + " ASC;";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while (!c.isAfterLast()){
            if (c.getString(c.getColumnIndex(COLUMN_TODOS)) != null){
                datePlans += c.getString(c.getColumnIndex(COLUMN_TODOS)) + "\n";
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        return datePlans;
    }

    public String getPlanById(int id){
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PLANS + " WHERE " + COLUMN_ID + " = " + id;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            dbString = c.getLong(c.getColumnIndex(COLUMN_PLAN_TIME)) + "#" +
                    c.getInt(c.getColumnIndex(COLUMN_PRIORITY)) + "#" +
                    c.getString(c.getColumnIndex(COLUMN_TODOS)) + "#" +
                    c.getInt(c.getColumnIndex(COLUMN_DONE)) + "#" +
                    c.getInt(c.getColumnIndex(COLUMN_AUTO_DELETE));
            c.moveToNext();
        }
        c.close();
        db.close();
        return dbString;
    }

    public String getSomePlans(long startTime, long endTime){
        StringBuilder stringBuilder = new StringBuilder();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PLANS + " WHERE " + COLUMN_PLAN_TIME + " >= " + startTime + " AND "
                + COLUMN_PLAN_TIME + " < " + endTime +
                " ORDER BY "+ COLUMN_PLAN_TIME + " ASC;";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while (!c.isAfterLast()){
            if (c.getString(c.getColumnIndex(COLUMN_PLAN_TIME)) != null){
                Calendar tempca = Calendar.getInstance();
                SimpleDateFormat ndf = new SimpleDateFormat("MM/dd", Locale.CHINA);
                tempca.setTimeInMillis(c.getLong(c.getColumnIndex(COLUMN_PLAN_TIME)));
                stringBuilder.append(ndf.format(tempca.getTime())).append(" ")
                        .append(c.getString(c.getColumnIndex(COLUMN_TODOS))).append("\n");
            }
            c.moveToNext();
        }
        c.close();      //Close the cursor and the database
        db.close();
        return stringBuilder.toString();
    }


    /**
     * Usually this method return the String for a single day
     *  format: priority hour:minute todos source done
     * @param startTime
     * @param endTime
     * @return
     */

    public String getSomePlansNoDate(long startTime, long endTime){
        //usually this is the plan of a single day
        StringBuilder stringBuilder = new StringBuilder();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PLANS + " WHERE " + COLUMN_PLAN_TIME + " >= " + startTime + " AND "
                + COLUMN_PLAN_TIME + " < " + endTime +
                " ORDER BY "+ COLUMN_PRIORITY + " ASC;";
        Cursor c = db.rawQuery(query, null);
        //Move the cursor to the first row in your database
        c.moveToFirst();
        while (!c.isAfterLast()){
            if (c.getString(c.getColumnIndex(COLUMN_PLAN_TIME)) != null){
                Calendar tempca = Calendar.getInstance();
                tempca.setTimeInMillis(c.getLong(c.getColumnIndex(COLUMN_PLAN_TIME)));
                SimpleDateFormat timedf = new SimpleDateFormat("h:mm a", Locale.CHINA);
                stringBuilder.append(String.format("%1s %tR %-9s %3s %1s\n",
                        c.getInt(c.getColumnIndex(COLUMN_PRIORITY)),
                        //timedf.format(tempca.getTime()),
                        tempca,
                        c.getString(c.getColumnIndex(COLUMN_TODOS)),
                        c.getString(c.getColumnIndex(COLUMN_SOURCE)),
                        c.getInt(c.getColumnIndex(COLUMN_DONE))));


//                stringBuilder.append(c.getInt(c.getColumnIndex(COLUMN_PRIORITY))).append(" ")
//                        .append(timedf.format(tempca.getTime())).append(" ")
//                        .append(c.getString(c.getColumnIndex(COLUMN_TODOS))).append(" ")
//                        .append(c.getString(c.getColumnIndex(COLUMN_SOURCE))).append(" ")
//                        .append(c.getInt(c.getColumnIndex(COLUMN_DONE))).append("\n");

            }
            c.moveToNext();
        }
        c.close();      //Close the cursor and the database
        db.close();
        return stringBuilder.toString();
    }

    /**
     * Format: [id]#[priority]#[date]#[plan]#[source]#[done]&, so I can split the result String
     * @param startTime
     * @param endTime
     * @return
     */
    public String getSomePlansSpecialFormat(long startTime, long endTime){
        StringBuilder stringBuilder = new StringBuilder();
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
                stringBuilder.append(c.getInt(c.getColumnIndex(COLUMN_ID))).append("#")
                        .append(c.getInt(c.getColumnIndex(COLUMN_PRIORITY))).append("#")
                        .append(ndf.format(tempca.getTime())).append("#")
                        .append(c.getString(c.getColumnIndex(COLUMN_TODOS))).append("#")
                        .append(c.getString(c.getColumnIndex(COLUMN_SOURCE))).append("#")
                        .append(c.getInt(c.getColumnIndex(COLUMN_DONE))).append("#_&");

            }
            c.moveToNext();
        }
        c.close();      //Close the cursor and the database
        db.close();
        return stringBuilder.toString();
    }


    /**
     * This method can auto delete the expired plan, I will find the expired plans first
     * than I can delete them.
     * Only the plans that are done and set to auto delete, we can delete the plan, no matter its priority
     *
     */
    public void autoDelete(){
        SQLiteDatabase db = getWritableDatabase();
        long pastTime = new Date().getTime() - 30*24*3600*1000; //This won't overflow!!!
        String query = "SELECT * FROM " + TABLE_PLANS + " WHERE " + COLUMN_PLAN_TIME + " <= " +
                pastTime;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while (!c.isAfterLast()){
            if (c.getInt(c.getColumnIndex(COLUMN_AUTO_DELETE)) == 1 && c.getInt(c.getColumnIndex(COLUMN_DONE)) == 1){
                db.execSQL("DELETE FROM " + TABLE_PLANS + " WHERE " + COLUMN_ID + "=\"" + c.getInt(c.getColumnIndex(COLUMN_ID)) + "\";" );
            }
            c.moveToNext();
        }
        c.close();
        db.close();
    }

}
