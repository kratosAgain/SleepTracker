package com.gouravapp.janacare;


import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by gaurav on 08/10/16.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "sleepRecord";
    private static final String TIME_SPEED_TABLE = "timeTable";
    private static final String ENTRY_TABLE = "entryTable";
    private static final String START_TIME = "startTime";
    private static final String SLEEP_TYPE = "sleepType";
    private static final String ID = "id";
    private static final String TIME= "time";
    private static final String SPEED = "speed";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TIME_SPEED_TABLE + "("
                + ID + " INTEGER PRIMARY KEY," + TIME + " INTEGER,"
                + SPEED + " INTEGER" + ")";

        final String CREATE_ENTRY_TABLE = "CREATE TABLE "
                + ENTRY_TABLE + "(" + SLEEP_TYPE + " TEXT," + SLEEP_TYPE
                + " TEXT"  + ")";
        Log.d("table made ",CREATE_ENTRY_TABLE);
        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_ENTRY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TIME_SPEED_TABLE);

        // Create tables again
        onCreate(db);
    }


    public void addTime(TimeEntry timeentry){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID,timeentry.id);
        values.put(TIME,timeentry.time);
        values.put(SPEED,timeentry.speed);
        db.insert(TIME_SPEED_TABLE, null, values);
        db.close();
    }

    public void addEntry(String type,String time){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SLEEP_TYPE,type);
        values.put(START_TIME,time);
        db.insert(ENTRY_TABLE, null, values);
        db.close();
    }


    public TimeEntry getTime(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TIME_SPEED_TABLE, new String[] { ID,
                        TIME, SPEED }, ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        TimeEntry t = new TimeEntry(Integer.parseInt(cursor.getString(0)),Long.parseLong(cursor.getString(1)),Integer.parseInt(cursor.getString(2)));
        return t;
    }

    public List<TimeEntry> getAll(){
        List<TimeEntry> list = new ArrayList<TimeEntry>();
        String selectQuery = "SELECT  * FROM " + TIME_SPEED_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                TimeEntry t = new TimeEntry(Integer.parseInt(cursor.getString(0)),Long.parseLong(cursor.getString(1)),Integer.parseInt(cursor.getString(2)));
                list.add(t);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public List<String[]> getAllEntry(){
        List<String[]> map = new ArrayList();
        String selectQuery = "SELECT  * FROM " + ENTRY_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String str[] = new String[2];
                str[0]=cursor.getString(0);
                str[1]=cursor.getString(1);
                map.add(str);
            } while (cursor.moveToNext());
        }
        return map;
    }

    public int getCount() {
        String countQuery = "SELECT  * FROM " + TIME_SPEED_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        // return count
        return cursor.getCount();
    }

    public void delete(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TIME_SPEED_TABLE);
        //db.execSQL("delete from "+ ENTRY_TABLE);

    }



}
