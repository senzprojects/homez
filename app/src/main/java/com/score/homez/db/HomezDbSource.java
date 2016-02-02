package com.score.homez.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.score.homez.pojos.Switch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by namal on 11/16/15.
 */
public class HomezDbSource {

    private static final String TAG = HomezDbSource.class.getName();
    private static Context context;

    public HomezDbSource(Context context) {
        Log.d(TAG, "Init: DB source");
        this.context = context;
    }

    public void createSwitch(Switch aSwitch) {
        Log.d(TAG, "AddSwitch: adding switch - " + aSwitch.getName());
        SQLiteDatabase db = HomezDbHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(HomezDbContract.Switch.COLUMN_NAME_NAME, aSwitch.getName());
        values.put(HomezDbContract.Switch.COLUMN_NAME_STATUS, aSwitch.getStatus());
        db.insertOrThrow(HomezDbContract.Switch.TABLE_NAME, HomezDbContract.Switch.COLUMN_NAME_NAME, values);
        db.close();
    }

    public void setSwitchStatus(Switch aSwitch) {
        Log.d(TAG, "Set status of switch " + aSwitch.getName() + " to " + aSwitch.getStatus());
        SQLiteDatabase db = HomezDbHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(HomezDbContract.Switch.COLUMN_NAME_STATUS, aSwitch.getStatus());

        db.update(HomezDbContract.Switch.TABLE_NAME,
                values,
                HomezDbContract.Switch.COLUMN_NAME_NAME + " = ?",
                new String[]{aSwitch.getName()});
        db.close();
    }

    public List<Switch> getAllSwitches() {
        Log.d(TAG, "getting all switched");
        List<Switch> switchList = new ArrayList<>();

        SQLiteDatabase db = HomezDbHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(HomezDbContract.Switch.TABLE_NAME, null, null, null, null, null, null);

        // switch attributes
        String _switchName;
        int _status;

        // extract attributes
        while (cursor.moveToNext()) {
            _switchName = cursor.getString(cursor.getColumnIndex(HomezDbContract.Switch.COLUMN_NAME_NAME));
            _status = cursor.getInt(cursor.getColumnIndex(HomezDbContract.Switch.COLUMN_NAME_STATUS));

            switchList.add(new Switch(_switchName, _status));
        }

        // clean
        cursor.close();
        db.close();

        Log.d(TAG, "switch count " + switchList.size());

        return switchList;
    }

    /**
     * Insert user to database
     *
     * @param username user
     */
    public void createUser(String username) {
        Log.d(TAG, "AddUser: adding user - " + username);
        SQLiteDatabase db = HomezDbHelper.getInstance(context).getWritableDatabase();

        // content values to inset
        ContentValues values = new ContentValues();
        values.put(HomezDbContract.User.COLUMN_NAME_USERNAME, username);

        // Insert the new row, if fails throw an error
        db.insertOrThrow(HomezDbContract.User.TABLE_NAME, HomezDbContract.User.COLUMN_NAME_USERNAME, values);
        db.close();
    }

}