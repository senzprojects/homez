package com.score.homez;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.score.senzc.pojos.Senz;

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
                Log.d(TAG, "SHARE received");
                handleShareSenz(senz);
                break;
            case DATA:
                Log.d(TAG, "DATA received");
                handleDataSenz(senz);
                break;
        }
    }

    private void handleShareSenz(Senz senz) {
        // get gpio and save in database
    }

    private void handleDataSenz(Senz senz) {
        // rebroadcast senz
        Intent intent = new Intent("DATA");
        intent.putExtra("SENZ", senz);
        context.sendBroadcast(intent);
    }

}
