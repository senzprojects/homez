package com.score.homez.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by namal on 11/16/15.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = DBHelper.class.getName();
    private static DBHelper dbHelper;

    private static final int DB_VERSION = 1;
    private static final String DB_NAME="HomeZ.db";

    private static final String SQL_CREATE_HOMEZ =
            "CREATE TABLE "+DBContract.Switch.TABLE_NAME+" ("+
            DBContract.Switch._ID + " INTEGER PRIMARY KEY AUTOINCREMENT"+", "+
            DBContract.Switch.COLUMN_NAME_NAME +" TEXT NOT NULL, "+
            DBContract.Switch.COLUMN_NAME_STATUS+" INT(1) NOT NULL DEFAULT 0"+
                    ")";

    private static final String SQL_DELETE_HOMEZ =
            "DROP TABLE IF EXIST "+DBContract.Switch.TABLE_NAME;




    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    synchronized static DBHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new DBHelper(context.getApplicationContext());
        }
        return (dbHelper);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "On create: create DBHelper , DB version" + DB_VERSION);
        Log.d(TAG, SQL_CREATE_HOMEZ);

        db.execSQL(SQL_CREATE_HOMEZ);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "OnUpgrade : upgrading db helper, db version - " + DB_VERSION);
        db.execSQL(SQL_DELETE_HOMEZ);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onUpgrade(db, oldVersion, newVersion);
    }
}
