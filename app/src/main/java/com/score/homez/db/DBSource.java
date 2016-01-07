package com.score.homez.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.score.homez.utils.Switch;

import java.util.ArrayList;

/**
 * Created by namal on 11/16/15.
 */
public class DBSource {

    private static final String TAG = DBSource.class.getName();
    private static Context context;

    public DBSource(Context context) {
        Log.d(TAG, "Init: DB source");
        this.context = context;
    }

    public void createSwitch(String name) {
        Log.d(TAG, "AddSwitch: adding switch - " + name);
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBContract.Switch.COLUMN_NAME_NAME, name);
        db.insertOrThrow(DBContract.Switch.TABLE_NAME, DBContract.Switch.COLUMN_NAME_NAME, values);
        db.close();
    }
    public  void deleteTable(){
        Log.d(TAG, "Dumping Database");
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
        db.delete(DBContract.Switch.TABLE_NAME,null,null);
    }

    public void updateSwitch(String name,String newName) {
        Log.d(TAG, "UpdateSwitch: updating switch - " + name);
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBContract.Switch.COLUMN_NAME_NAME, newName);

        db.update(DBContract.Switch.TABLE_NAME, values, DBContract.Switch.COLUMN_NAME_NAME + " = ?", new String[]{name});
        db.close();
    }

    public void setStatus(String name,int status) {
        Log.d(TAG, "setStatus : setting status - " + name + " - " + Integer.toString(status));
        SQLiteDatabase db=DBHelper.getInstance(context).getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(DBContract.Switch.COLUMN_NAME_STATUS, status);

        db.update(DBContract.Switch.TABLE_NAME, values, DBContract.Switch.COLUMN_NAME_NAME + " = ?", new String[]{name});
        db.close();

    }

    public int getStatus(String name){ //ToDo :early added if not exist create before get, functionality is still exist and seems not nessecerry any more
        Log.d(TAG, "get Status : gettng status - " + name);
        SQLiteDatabase db =new  DBHelper(context).getReadableDatabase();

        Cursor cursor = db.query(DBContract.Switch.TABLE_NAME,
                null,DBContract.Switch.COLUMN_NAME_NAME+" = ?",
                new String[]{String.valueOf(name)},
                null,   //groupby
                null,  //having
                null    //orderby
                 );
        if(cursor.moveToFirst()){
            int status = cursor.getInt(cursor.getColumnIndex(DBContract.Switch.COLUMN_NAME_STATUS));
            cursor.close();
            db.close();

            Log.d(TAG, "have switch so return status : switch - " + name + " status - " + status);
            return status;
        }
        else {
            this.createSwitch(name);
            int status=this.getStatus(name);
            Log.d(TAG, "havent switch : so create - " + name+ " status - "+status);
            return status;
        }
    }

    public ArrayList<Switch> getSwitches(){
        Log.d(TAG, "get all switches from DB ");
        SQLiteDatabase db =new  DBHelper(context).getReadableDatabase();

        ArrayList<Switch> data= new ArrayList<Switch>();

        Cursor cursor = db.query(DBContract.Switch.TABLE_NAME,
                new String[]{DBContract.Switch.COLUMN_NAME_NAME},null,//where colomn=value
                null,
                null,   //groupby
                null,  //having
                null    //orderby
        );

        if(cursor.moveToFirst()) {
            cursor.moveToFirst();
            do {
                String name = cursor.getString(cursor.getColumnIndex(DBContract.Switch.COLUMN_NAME_NAME));
                int id = cursor.getInt(cursor.getColumnIndex(DBContract.Switch.COLUMN_NAME_ID));
                int status = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBContract.Switch.COLUMN_NAME_STATUS)));
                Switch aSwitch = new Switch(name, id, status);
                data.add(aSwitch);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return data;
    }

    public ArrayList<Switch> getAllSwitches()
    {
        String SELECT_ALL = "SELECT * FROM " + DBContract.Switch.TABLE_NAME;
        SQLiteDatabase db =new  DBHelper(context).getReadableDatabase();
        ArrayList<Switch> switches = new ArrayList<>();
        Cursor cursor = db.rawQuery(SELECT_ALL, null);
        //this.cursor = cursor;
        if(cursor != null)
        {
            if(cursor.moveToFirst())
            {
                do
                {
                    String name = cursor.getString(cursor.getColumnIndex(DBContract.Switch.COLUMN_NAME_NAME));
                    int id = cursor.getInt(cursor.getColumnIndex(DBContract.Switch.COLUMN_NAME_ID));
                    int status = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBContract.Switch.COLUMN_NAME_STATUS)));
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
        db.close();
        return switches;
    }

    public void toggleSwitch(String name, int newState)
    {
        SQLiteDatabase db =new  DBHelper(context).getReadableDatabase();
        ContentValues content = new ContentValues();
        content.put(DBContract.Switch.COLUMN_NAME_STATUS, newState+"");
        db.update(DBContract.Switch.TABLE_NAME, content, DBContract.Switch.COLUMN_NAME_NAME + "=?", new String[]{name});

        db.close();
    }


}