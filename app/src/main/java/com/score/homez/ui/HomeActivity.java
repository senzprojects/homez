package com.score.homez.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.score.homez.R;
import com.score.homez.db.HomezDbSource;
import com.score.homez.exceptions.NoUserException;
import com.score.homez.pojos.Switchz;
import com.score.homez.utils.ActivityUtils;
import com.score.homez.utils.NetworkUtil;
import com.score.homez.utils.PreferenceUtils;
import com.score.senz.ISenzService;
import com.score.senzc.enums.SenzTypeEnum;
import com.score.senzc.pojos.Senz;
import com.score.senzc.pojos.User;

import java.util.ArrayList;
import java.util.HashMap;


public class HomeActivity extends Activity implements View.OnClickListener {

    private static final String TAG = HomeActivity.class.getName();

    // we use custom font here
    private Typeface typeface;

    // layout components
    private RelativeLayout nightModeLayout;
    private RelativeLayout visitorModeLayout;
    private TextView nightModeText;
    private TextView visitorModeText;

    // service interface
    private ISenzService senzService = null;

    // service bind status
    private boolean isServiceBound = false;

    // switch
    private ArrayList<Switchz> switchzList;
    private boolean isNightModeSelected = true;

    // use to track share timeout
    private SenzCountDownTimer senzCountDownTimer;
    private boolean isResponseReceived;

    // service connection
    private ServiceConnection senzServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG, "Connected with senz service");
            senzService = ISenzService.Stub.asInterface(service);
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.d(TAG, "Disconnected from senz service");
            senzService = null;
        }
    };

    private BroadcastReceiver senzMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Got message from Senz service");
            handleMessage(intent);
        }
    };

    /**
     * Keep track with share response timeout
     */
    private class SenzCountDownTimer extends CountDownTimer {

        public SenzCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // if response not received yet, resend share
            if (!isResponseReceived) {
                switchMode(isNightModeSelected ? switchzList.get(0) : switchzList.get(1));
                Log.d(TAG, "Response not received yet");
            }
        }

        @Override
        public void onFinish() {
            ActivityUtils.hideSoftKeyboard(HomeActivity.this);
            ActivityUtils.cancelProgressDialog();

            // display message dialog that we couldn't reach the user
            if (!isResponseReceived) {
                String message = "<font color=#000000>Seems we couldn't reach the home </font> <font color=#eada00>" + "<b>" + "NAME" + "</b>" + "</font> <font color=#000000> at this moment</font>";
                displayInformationMessageDialog("#PUT Fail", message);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.switch_board_layout);

        // load switches from the db
        switchzList = (ArrayList<Switchz>) new HomezDbSource(this).getAllSwitches();

        bindSenzService();
        registerReceiver(senzMessageReceiver, new IntentFilter("com.score.senz.DATA_SENZ"));
        initUi();
        setupActionBar();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(senzMessageReceiver);
        if (isServiceBound) {
            unbindService(senzServiceConnection);
            isServiceBound = false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.stay_in, R.anim.right_out);
    }

    /**
     * Bind with senz service
     */
    private void bindSenzService() {
        if (!isServiceBound) {
            Intent intent = new Intent();
            intent.setClassName("com.score.senz", "com.score.senz.services.RemoteSenzService");
            bindService(intent, senzServiceConnection, BIND_AUTO_CREATE);
            isServiceBound = true;
        }
    }

    /**
     * Initialize UI components
     */
    private void initUi() {
        typeface = Typeface.createFromAsset(getAssets(), "fonts/vegur_2.otf");

        nightModeLayout = (RelativeLayout) findViewById(R.id.night_mode);
        visitorModeLayout = (RelativeLayout) findViewById(R.id.visitor_mode);

        nightModeText = (TextView) findViewById(R.id.night_mode_text);
        visitorModeText = (TextView) findViewById(R.id.visitor_mode_text);

        nightModeText.setTypeface(typeface, Typeface.BOLD);
        visitorModeText.setTypeface(typeface, Typeface.BOLD);

        nightModeLayout.setOnClickListener(this);
        visitorModeLayout.setOnClickListener(this);
    }

    /**
     * Set up action bar
     */
    private void setupActionBar() {
        // enable ActionBar app icon to behave as action to toggle nav drawer
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
        TextView yourTextView = (TextView) findViewById(titleId);
        yourTextView.setTextColor(getResources().getColor(R.color.white));
        yourTextView.setTypeface(typeface);

        getActionBar().setTitle("HomeZ");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(View v) {
        if (v == nightModeLayout) {
            isNightModeSelected = true;
            if (NetworkUtil.isAvailableNetwork(this)) {
                ActivityUtils.showProgressDialog(this, "Please wait...");
                senzCountDownTimer.start();
            } else {
                Toast.makeText(this, "No network connection available", Toast.LENGTH_LONG).show();
            }
        } else if (v == visitorModeLayout) {
            isNightModeSelected = false;
            if (NetworkUtil.isAvailableNetwork(this)) {
                ActivityUtils.showProgressDialog(this, "Please wait...");
                senzCountDownTimer.start();
            } else {
                Toast.makeText(this, "No network connection available", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void switchMode(Switchz switchz) {
        HashMap<String, String> senzAttributes = new HashMap<>();
        senzAttributes.put(switchz.getName(), switchz.getStatus() == 0 ? "on" : "off");
        senzAttributes.put("time", ((Long) (System.currentTimeMillis() / 1000)).toString());

        try {
            // get receiver
            User receiver = PreferenceUtils.getUser(this);

            // new senz
            String id = "_ID";
            String signature = "_SIGNATURE";
            SenzTypeEnum senzType = SenzTypeEnum.PUT;

            Senz senz = new Senz(id, signature, senzType, null, receiver, senzAttributes);
            senzService.send(senz);
        } catch (NoUserException | RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle broadcast message receives
     * Need to handle registration success failure here
     *
     * @param intent intent
     */
    private void handleMessage(Intent intent) {
        String action = intent.getAction();

        if (action.equalsIgnoreCase("com.score.senz.DATA_SENZ")) {
            Senz senz = intent.getExtras().getParcelable("SENZ");

            if (senz.getAttributes().containsKey("msg")) {
                // msg response received
                ActivityUtils.cancelProgressDialog();
                isResponseReceived = true;
                senzCountDownTimer.cancel();

                String msg = senz.getAttributes().get("msg");
                if (msg != null && msg.equalsIgnoreCase("PutDone")) {
                    onPostPut(senz);
                } else {
                    String message = "<font color=#000000>Seems we couldn't access the switch </font> <font color=#eada00>" + "<b>" + "NAME" + "</b>" + "</font>";
                    displayInformationMessageDialog("#PUT Fail", message);
                }
            }
        }
    }

    /**
     * @param senz
     */
    private void onPostPut(Senz senz) {
        // update switch status in db
        if (isNightModeSelected) {
            Switchz switchz = switchzList.get(0);
            switchz.setStatus(switchz.getStatus() == 0 ? 1 : 0);
            new HomezDbSource(this).setSwitchStatus(switchz);

            // repopulate switch list
            switchzList = (ArrayList<Switchz>) new HomezDbSource(this).getAllSwitches();
        }
    }

    /**
     * Display message dialog when user request(click) to delete invoice
     *
     * @param message message to be display
     */
    public void displayInformationMessageDialog(String title, String message) {
        final Dialog dialog = new Dialog(this);

        //set layout for dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.information_message_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);

        // set dialog texts
        TextView messageHeaderTextView = (TextView) dialog.findViewById(R.id.information_message_dialog_layout_message_header_text);
        TextView messageTextView = (TextView) dialog.findViewById(R.id.information_message_dialog_layout_message_text);
        messageHeaderTextView.setText(title);
        messageTextView.setText(Html.fromHtml(message));

        // set custom font
        messageHeaderTextView.setTypeface(typeface);
        messageTextView.setTypeface(typeface);

        //set ok button
        Button okButton = (Button) dialog.findViewById(R.id.information_message_dialog_layout_ok_button);
        okButton.setTypeface(typeface);
        okButton.setTypeface(null, Typeface.BOLD);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }


}
