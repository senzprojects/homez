package com.score.homez;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteConstraintException;
import android.net.sip.SipSession;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.os.IBinder;
import android.widget.ListView;

import com.score.homez.db.DBSource;
import com.score.homez.ui.SwitchAdapter;
import com.score.homez.utils.NotificationUtils;
import com.score.homez.utils.Switch;
import com.score.senz.ISenzService;
import com.score.senzc.enums.SenzTypeEnum;
import com.score.senzc.pojos.Senz;
import com.score.senzc.pojos.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by eranga on 11/26/15.
 */
public class SenzHandler {

    private static final String TAG = SenzHandler.class.getName();

    private static Context context;
    private static SenzHandler instance;
    //layout components
    SwitchAdapter adapter;
    private ArrayList<Switch> switches;
    private ListView list;

    private SenzHandler() {
    }

    public static SenzHandler getInstance(Context context) {
        if (instance == null) {
            instance = new SenzHandler();
            SenzHandler.context = context;
        }
        return instance;
    }


    public void handleSenz(Senz senz) {
        switch (senz.getSenzType()) {
            case SHARE:
                Log.e(TAG, "SHARE received");
                handleShareSenz(senz);
                break;
            case DATA:
                Log.e(TAG, "DATA received");
                handleDataSenz(senz);
                break;
        }
    }

    private void handleDataSenz(Senz senz) {
        DBSource db= new DBSource(context);
        //if DATA Senz
        for (Map.Entry<String, String> entry : senz.getAttributes().entrySet()) {
            String key = entry.getKey();
            String svalue = entry.getValue();
            int value;
            Log.d(TAG, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + svalue);
            if (key.contains("s1") || key.contains("s2")) {
                if (svalue.equals("on")) value = 5;
                else value = 3;
                db.toggleSwitch(key, value);
            }
        }
        //switches=db.getAllSwitches();
        //Log.d(TAG, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + switches.toString());

        // adapter.setToggleList(switches);
    //    list.deferNotifyDataSetChanged();
    }


    private void handleShareSenz(Senz senz) {
        //SwitchesDB db = new SwitchesDB(context);
        DBSource db= new DBSource(context);
        // if senz already exists in the db, SQLiteConstraintException should throw
        // get gpio and save in database
        //Log.e(TAG,"============="+senz.getAttributes().size());
            try {
                db.deleteTable();
                Log.e(TAG, senz.getAttributes() + "=======================================");

                for(Map.Entry<String, String> entry : senz.getAttributes().entrySet()) {
                    String key = entry.getKey();
                    if (!key.contains("homez") && !key.contains("time")) {
                        db.createSwitch(key);
                    }
                    if (key.contains("homez")) {
                        Log.e(TAG, "=================App =  " + key + "  User " + senz.getSender().getUsername() + " added to DB======================");
                        db.deleteUser(senz.getSender().getUsername());
                        db.createUser(senz.getSender().getUsername());
                        db.resetUserStatus();
                        db.setUserStatus(senz.getSender().getUsername(), 1);
                    }
                }
                NotificationUtils.showNotification(context, context.getString(R.string.new_senz), "SmartHome Switches are Shared from @" + senz.getSender().getUsername());
                Log.e(TAG, "Swithes and User are Added To Homes DB");
            }  catch (SQLiteConstraintException e) {
                Log.e(TAG, e.toString());
            }

        

    }

/*
    private void handleDataSenz(Senz senz) {
        // rebroadcast senz
        //Intent intent = new Intent("com.score.senzc.DATA");
        Intent intent = new Intent("com.score.senz.NEW_SENZ");
        intent.putExtra("SENZ", senz);

        //context.sendBroadcast(intent);
    }
*/
}
