package com.score.homez.handlers;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.RemoteException;
import android.util.Log;

import com.score.homez.R;
import com.score.homez.db.HomezDbSource;
import com.score.homez.pojos.Switchz;
import com.score.homez.services.SenzServiceConnection;
import com.score.homez.utils.NotificationUtils;
import com.score.homez.utils.PreferenceUtils;
import com.score.senz.ISenzService;
import com.score.senzc.enums.SenzTypeEnum;
import com.score.senzc.pojos.Senz;
import com.score.senzc.pojos.User;

import java.util.HashMap;

/**
 * Created by eranga on 11/26/15.
 */
public class SenzHandler {

    private static final String TAG = SenzHandler.class.getName();

    private static Context context;

    private static SenzHandler instance;

    private static SenzServiceConnection serviceConnection;

    private SenzHandler() {
    }

    public static SenzHandler getInstance(Context context) {
        if (instance == null) {
            instance = new SenzHandler();
            SenzHandler.context = context.getApplicationContext();

            serviceConnection = new SenzServiceConnection(context);

            // bind to senz service
            Intent serviceIntent = new Intent();
            serviceIntent.setClassName("com.score.senz", "com.score.senz.services.RemoteSenzService");
            SenzHandler.context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
        return instance;
    }

    public void handleSenz(Senz senz) {
        switch (senz.getSenzType()) {
            case SHARE:
                Log.e(TAG, "SHARE received");
                if (senz.getAttributes().containsKey("NightMode") || senz.getAttributes().containsKey("NightMode")) {
                    Log.e(TAG, "SHARE received with NightMode and VisitorMode");
                    handleShareSenz(senz);
                }

                break;
            case DATA:
                Log.e(TAG, "DATA received");
                handleDataSenz(senz);
                break;
        }
    }

    private void handleShareSenz(final Senz senz) {
        serviceConnection.executeAfterServiceConnected(new Runnable() {
            @Override
            public void run() {
                // service instance
                ISenzService senzService = serviceConnection.getInterface();

                // if switches already exists in the db, SQLiteConstraintException should throw
                try {
                    HomezDbSource dbSource = new HomezDbSource(context);

                    // create user first
                    dbSource.createUser(senz.getSender().getUsername());
                    PreferenceUtils.saveUser(context, new User("1", senz.getSender().getUsername()));
                    Log.d(TAG, "created user with " + senz.getSender().getUsername());

                    // create switches then
                    for (String key : senz.getAttributes().keySet()) {
                        dbSource.createSwitch(new Switchz(key, 0));

                        Log.d(TAG, "created switch with " + key);
                    }

                    NotificationUtils.showNotification(context, context.getString(R.string.new_senz), "HomeZ received from @" + senz.getSender().getUsername());
                    sendResponse(senzService, senz.getSender(), true);
                } catch (SQLiteConstraintException e) {
                    sendResponse(senzService, senz.getSender(), false);
                    Log.e(TAG, e.toString());
                }
            }
        });
    }

    private void sendResponse(ISenzService senzService, User receiver, boolean isDone) {
        Log.d(TAG, "send response");
        try {
            // create senz attributes
            HashMap<String, String> senzAttributes = new HashMap<>();
            senzAttributes.put("time", ((Long) (System.currentTimeMillis() / 1000)).toString());
            if (isDone) senzAttributes.put("msg", "ShareDone");
            else senzAttributes.put("msg", "AlreadyShared");

            String id = "_ID";
            String signature = "";
            SenzTypeEnum senzType = SenzTypeEnum.DATA;
            Senz senz = new Senz(id, signature, senzType, null, receiver, senzAttributes);

            senzService.send(senz);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void handleDataSenz(Senz senz) {
        Log.d(TAG, "Nothing to handle data senz here, already broadcast");
    }

}
