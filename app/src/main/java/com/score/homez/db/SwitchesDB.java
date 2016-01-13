package com.score.homez.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.score.homez.utils.Switch;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Anesu on 1/1/2016.
 */
public class SwitchesDB extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "Switches.db";
    private static final String TABLE_NAME = "Switches";

    private static final String ID = "_id";
    private static final String NAME = "name";
    private static final String STATUS = "status";

    private static final int ID_INDEX = 1;
    private static final int NAME_INDEX = 2;
    private static final int STATUS_INDEX = 3;

    public SwitchesDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL(DROP_TABLE);
        //onCreate(db);
    }

    public void deleteHomeTable(String name) {
        final String DROP_TABLE = "DROP TABLE IF EXISTS " + name;
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(DROP_TABLE);
    }

    public void addSwitch(String table_name, String name, int status) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(STATUS, status);
        db.insert(table_name, null, values);
    }

    public void createHomeTable(String name) {
        final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + name + "(" + ID + " INTEGER PRIMARY KEY, " +
                "TEXT," + NAME + " TEXT," + STATUS + " TEXT);";

        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(CREATE_TABLE);
    }

    public ArrayList<String> getAllHomes() {
        ArrayList<String> homes = new ArrayList<>();
        String GET_TABLES = "SELECT name FROM sqlite_master WHERE type='table'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(GET_TABLES, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                if (!name.equals("android_metadata")) {
                    homes.add(name);
                }
                cursor.moveToNext();
            }
        }
        return homes;
    }

    public ArrayList<Switch> getAllSwitches(String name) {
        String SELECT_ALL = "SELECT * FROM " + name;
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Switch> switches = new ArrayList<>();
        Cursor cursor = db.rawQuery(SELECT_ALL, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String home_name = cursor.getString(NAME_INDEX);
                    int id = cursor.getInt(ID_INDEX);
                    int status = Integer.parseInt(cursor.getString(STATUS_INDEX));
                    Switch aSwitch = new Switch(home_name, id, status);
                    switches.add(aSwitch);
                }
                while (cursor.moveToNext());
            }
        } else {
            return null;
        }

        return switches;
    }

    public void toggleSwitch(String table_name, String name, int newState) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(STATUS, newState + "");
        db.update(table_name, content, NAME + "=?", new String[]{name});
    }

    public void clearDB(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(name, null, null);
    }

    public int getCount(String name) {
        try {
            String SELECT_ALL = "SELECT * FROM " + name;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(SELECT_ALL, null);
            return cursor.getCount();
        }catch (Exception e)
        {
            this.createHomeTable(name);
            return this.getCount(name);
        }
    }

}

