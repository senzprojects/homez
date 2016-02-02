package com.score.homez.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by namal on 11/16/15.
 */
public class HomezDbHelper extends SQLiteOpenHelper {

    private static final String TAG = HomezDbHelper.class.getName();
    private static HomezDbHelper dbHelper;

    private static final int DB_VERSION = 5;
    private static final String DB_NAME = "HomeZ.db";

    private static final String SQL_CREATE_SWITCH =
            "CREATE TABLE " + HomezDbContract.Switch.TABLE_NAME + " (" +
                    HomezDbContract.Switch._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + ", " +
                    HomezDbContract.Switch.COLUMN_NAME_NAME + " TEXT UNIQUE NOT NULL, " +
                    HomezDbContract.Switch.COLUMN_NAME_STATUS + " INT NOT NULL DEFAULT 0" +
                    ")";
    private static final String SQL_CREATE_USER =
            "CREATE TABLE " + HomezDbContract.User.TABLE_NAME + " (" +
                    HomezDbContract.User._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + "," +
                    HomezDbContract.User.COLUMN_NAME_USERNAME + " TEXT UNIQUE NOT NULL" +
                    ")";

    private static final String SQL_DELETE_SWITCH =
            "DROP TABLE IF EXIST " + HomezDbContract.Switch.TABLE_NAME;

    private static final String SQL_DELETE_USER =
            "DROP TABLE IF EXIST " + HomezDbContract.Switch.TABLE_NAME;

    public HomezDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    synchronized static HomezDbHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new HomezDbHelper(context.getApplicationContext());
        }
        return (dbHelper);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "On create: create DBHelper , DB version" + DB_VERSION);
        Log.d(TAG, SQL_CREATE_SWITCH);
        Log.d(TAG, SQL_CREATE_USER);
        db.execSQL(SQL_CREATE_SWITCH);
        db.execSQL(SQL_CREATE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "OnUpgrade : upgrading db helper, db version - " + DB_VERSION);
        db.execSQL(SQL_DELETE_SWITCH);
        db.execSQL(SQL_DELETE_USER);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
