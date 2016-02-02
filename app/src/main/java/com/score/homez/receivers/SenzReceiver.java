package com.score.homez.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.score.homez.handlers.SenzHandler;
import com.score.senzc.pojos.Senz;

/**
 * Broadcast receiver to receive senz messages which broadcast from SenzService
 *
 * @author eranga bandara(erangaeb@gmail.com)
 */
public class SenzReceiver extends BroadcastReceiver {

    private static final String TAG = SenzReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // extract broadcasting senz
        Senz senz = intent.getExtras().getParcelable("SENZ");

        Log.d(TAG, "Broadcast Senz received from" + senz.getSender());
        SenzHandler.getInstance(context).handleSenz(senz);
    }
}
