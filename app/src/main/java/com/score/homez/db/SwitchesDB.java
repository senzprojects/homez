package com.score.homez.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.score.homez.utils.Switch;

import java.util.ArrayList;

/**
 * Created by Anesu on 1/1/2016.
 */
public class SwitchesDB extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME="Switches.db";
    private static final String TABLE_NAME="Switches";

    private static final String ID = "_id";
    private static final String NAME = "name";
    private static final String STATUS = "status";

    private static final int ID_INDEX = 1;
    private static final int NAME_INDEX = 2;
    private static final int STATUS_INDEX = 3;

    Cursor cursor;

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY, " +
            "TEXT," + NAME + " TEXT," + STATUS +  " TEXT);";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public SwitchesDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public void addSwitch(String name, int status)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(STATUS, status);
        db.insert(TABLE_NAME, null, values);
    }

    public ArrayList<Switch> getAllSwitches()
    {
        String SELECT_ALL = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Switch> switches = new ArrayList<>();
        Cursor cursor = db.rawQuery(SELECT_ALL, null);
        this.cursor = cursor;
        if(cursor != null)
        {
            if(cursor.moveToFirst())
            {
                do
                {
                    String name = cursor.getString(NAME_INDEX);
                    int id = cursor.getInt(ID_INDEX);
                    int status = Integer.parseInt(cursor.getString(STATUS_INDEX));
                    Switch aSwitch = new Switch(name, id, status);
                    switches.add(aSwitch);
                }
                while(cursor.moveToNext());
            }
        }
        else
        {
            return null;
        }

        return switches;
    }

    public void toggleSwitch(int id, String name, int newState)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(STATUS, newState+"");
        db.update(TABLE_NAME, content, NAME + "=?", new String[]{name});
    }

}

