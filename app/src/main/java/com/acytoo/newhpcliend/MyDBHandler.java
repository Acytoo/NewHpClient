package com.acytoo.newhpcliend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


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
 */
public class MyDBHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;             //Current I stop using this version.
    private static final String DATABASE_NAME = "info.db";    //must have a .db extension
    public static final String TABLE_PLANS = "plans";        //the table name of your info
    public static final String COLUMN_ID = "_id";           //all these three values are worked like
    public static final String COLUMN_DATE = "_date";       //my explains are listed above
    public static final String COLUMN_TODOS = "_todos";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_PLANS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_TODOS + " TEXT " +
                ");";
        db.execSQL(query);
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
        values.put(COLUMN_DATE, plan.get_date());
        values.put(COLUMN_TODOS, plan.get_todos());
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
     * databas and will not be used in the matured app
     * Alec Chen 20 4 2018 17:03
     * I am hungry ...
     */
    public String databaseToString(){
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PLANS + " WHERE 1 ORDER BY "+ COLUMN_DATE + " ASC;";
        //Cursor points to a location in your results
        Cursor c = db.rawQuery(query, null);
        //Move the cursor to the first row in your database
        c.moveToFirst();

        while (!c.isAfterLast()){
            if (c.getString(c.getColumnIndex("_date")) != null){
                dbString += c.getString(c.getColumnIndex("_date")) + "  " + c.getString(c.getColumnIndex("_todos"));
                dbString += "\n";
            }
            c.moveToNext();
        }
        c.close();      //Close the cursor and the database
        db.close();
        return dbString;
    }

    public String getDatePlans(String dateString){
        String datePlans = "";
        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT " + COLUMN_TODOS + " FROM " + TABLE_PLANS + " WHERE " + COLUMN_DATE + " = ?;";
        Cursor c = db.rawQuery(query, new String[]{dateString});
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
    /*
    public SQLiteDatabase sortPlansByDate(SQLiteDatabase db){
        String query = "SELECT * FROM " + TABLE_PLANS + " ORDER BY " + COLUMN_DATE;

    }*/

}
