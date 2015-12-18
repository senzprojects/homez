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

import com.score.homez.db.DBSource;
import com.score.homez.ui.HomeActivity;
import com.score.homez.utils.NotificationUtils;
import com.score.senz.ISenzService;
import com.score.senzc.enums.SenzTypeEnum;
import com.score.senzc.pojos.Senz;
import com.score.senzc.pojos.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by eranga on 11/26/15.
 */
public class SenzHandler {

    private static final String TAG = SenzHandler.class.getName();

    private static Context context;

    private static SenzHandler instance;

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

    private void handleShareSenz(Senz senz) {
        DBSource dbSource = new DBSource(context);
        // if senz already exists in the db, SQLiteConstraintException should throw
        // get gpio and save in database
        //Log.e(TAG,"============="+senz.getAttributes().size());
            try {
                for(Map.Entry<String, String> entry : senz.getAttributes().entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if(!key.equals("time"))
                        dbSource.createSwitch(key);
                }
                NotificationUtils.showNotification(context, context.getString(R.string.new_senz), "SmartHome Switches are Shareed from @" + senz.getSender().getUsername());
                Log.e(TAG, "Swithes are Added To Homes DB");
            }  catch (SQLiteConstraintException e) {
                Log.e(TAG, e.toString());
            }

        

    }


    private void handleDataSenz(Senz senz) {
        // rebroadcast senz
        Intent intent = new Intent("com.score.senzc.DATA");
        intent.putExtra("SENZ", senz);

        //context.sendBroadcast(intent);
    }

}
