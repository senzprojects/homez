package com.score.homez.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.score.homez.R;
import com.score.homez.db.DBSource;
import com.score.homez.utils.ActivityUtils;
import com.score.homez.utils.NetworkUtil;
import com.score.homez.utils.Switch;
import com.score.senz.ISenzService;
import com.score.senzc.enums.SenzTypeEnum;
import com.score.senzc.pojos.Senz;
import com.score.senzc.pojos.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class  HomeActivity extends Activity {

    private static final String TAG = HomeActivity.class.getName();

    //layout components
    private ArrayList<Switch> switches;
    private ListView list;
    DBSource db;

    // use to track share timeout
    private SenzCountDownTimer senzCountDownTimer;
    //private boolean isResponseReceivedPut;
    private boolean isResponseReceivedGet;

    // we use custom font here
    private Typeface typeface;

    // service interface
    private ISenzService senzService = null;

    // service connection
    private ServiceConnection senzServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d("TAG", "Connected with senz service");
            senzService = ISenzService.Stub.asInterface(service);
        }

        public void onServiceDisconnected(ComponentName className) {
            senzService = null;
            Log.d(TAG, "Disconnected from senz service");
        }
    };

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        db= new DBSource(this);
        registerReceiver(senzMessageReceiver, new IntentFilter("com.score.senzc.DATA"));
        senzCountDownTimer = new SenzCountDownTimer(16000, 5000);
        //isResponseReceivedPut = true;
        isResponseReceivedGet = true;

        initUi();
        setupActionBar();
        bindSenzService();

        if(NetworkUtil.isAvailableNetwork(this)){
            if(db.getAllSwitches().size()>0) {
                Log.e(TAG, "Switches are exist in  DB");
                isResponseReceivedGet = false;
                senzCountDownTimer.start();


            } else {
                String message = "<font color=#000000>Switches are NOT SHARED from </font> <font color=#eada00>" + "<b>" + "SmartHome" + "</b>" + "</font> <font color=#000000> <br> Please SHARE Them</font>";
                displayInformationMessageDialog("#SHARE NOT RECIEVED", message);
            }

        }
        else {
            Toast.makeText(this,"No Network Connection Available",Toast.LENGTH_LONG).show();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(senzServiceConnection);
        unregisterReceiver(senzMessageReceiver);
    }


    /**     }
     * Initialize UI components
     */
    protected void initUi() {

        this.switches=db.getAllSwitches();
        if(switches.size()==0) { //if switches are not added to db from received share message; add sample switches for test
            for (int i = 0; i < 9; i++) {
                Switch s = new Switch("Switch "+i, i, 0);
                this.switches.add(s);
            }
        }
        else{// added more switches for checking scroll
            for (int i = 5; i < 9; i++) {
                Switch s = new Switch("Switch "+i, i, 0);
                this.switches.add(s);
            }
        }
        list = (ListView) findViewById(R.id.list_view);
        SwitchAdapter adapter = new SwitchAdapter(this, R.layout.single_toggle, this.switches,senzCountDownTimer);
        list.setAdapter(adapter);
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

        getActionBar().setTitle("Switch board");
    }

    /**
     * Bind with senz service
     */
    private void bindSenzService() {
        Intent intent = new Intent();
        intent.setClassName("com.score.senzservices", "com.score.senzservices.services.RemoteSenzService");
        bindService(intent, senzServiceConnection, Context.BIND_AUTO_CREATE);
    }



    /**
     * Keep track with share response timeout
     */
    class SenzCountDownTimer extends CountDownTimer {

        public SenzCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // if response not received yet, resend share
            if (!isResponseReceivedGet) {
                ActivityUtils.cancelProgressDialog();
                ActivityUtils.showProgressDialog(HomeActivity.this, "Please wait ...");
                get();
                Log.d(TAG, "Get Response not received yet");
            }else {
                ActivityUtils.cancelProgressDialog();
                ActivityUtils.showProgressDialog(HomeActivity.this,"Please wait ...");
                put();
                Log.d(TAG, "Put Response not received yet");
            }
        }

        @Override
        public void onFinish() {
            if(!isResponseReceivedGet)
                isResponseReceivedGet=true;
            ActivityUtils.cancelProgressDialog();
            String message = "<font color=#000000>Seems we couldn't reach the </font> <font color=#eada00>" + "<b>" + "SmartHome" + "</b>" + "</font> <font color=#000000> at this moment</font>";
            displayInformationMessageDialog("#GETTING STATUS FAIL", message);

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

    private BroadcastReceiver senzMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Got message from Senz service");
            handleMessage(intent);
        }
    };

    /**
     * Handle broadcast message receives
     * Need to handle registration success failure here
     *
     * @param intent intent
     */
    private void handleMessage(Intent intent) {
        String action = intent.getAction();

        if (action.equalsIgnoreCase("com.score.senzc.DATA")) {
            Senz senz = intent.getExtras().getParcelable("SENZ");

            if (senz.getAttributes().containsKey("msg")) {
                // msg response received
                ActivityUtils.cancelProgressDialog();
                senzCountDownTimer.cancel();

                String msg = senz.getAttributes().get("msg");
//                if (msg != null && msg.equalsIgnoreCase("PutDone")) {ToDo if msg is not equals
//                    //onPostShare();
                if (msg != null && msg.equalsIgnoreCase("PutDone")) {
                    Log.d(TAG, "DATA #msg PutDone Recieved");
                    //isResponseReceivedPut = true;
                    for(Map.Entry<String, String> entry : senz.getAttributes().entrySet()) {
                        String key = entry.getKey();
                        //Log.d(TAG, key + " : " + entry.getValue() + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                        if (!key.contains("app") && !key.contains("time") && !key.contains("msg")) {
                            int value = Integer.parseInt(entry.getValue());
                            db.toggleSwitch(key, value);
                        }
                    }
                    list.deferNotifyDataSetChanged();
                }
                else if (msg != null && msg.equalsIgnoreCase("GetResponse")) {
                    isResponseReceivedGet = true;
                    Toast.makeText(this.getBaseContext(), "Status Received", Toast.LENGTH_SHORT).show();
                    for(Map.Entry<String, String> entry : senz.getAttributes().entrySet()) {
                        String key = entry.getKey();
                        //Log.d(TAG, key+" : "+entry.getValue()+"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                        if(!key.contains("app") && !key.contains("time") && !key.contains("msg")){
                            int value = Integer.parseInt(entry.getValue());
                            db.toggleSwitch(key, value);
                        }
                    }
                    initUi();

                } else {
                    String message = "<font color=#000000>Seems we couldn't PUT </font> <font color=#eada00>" + "<b>" + "gpio" + "</b>" + "</font>";
                    displayInformationMessageDialog("#Share Fail", message);
                }
            }
        }
    }

    /**
     * Share current sensor
     * Need to send share query to server via web socket
     */
    private void put() {
        try {
            // create senz attributes
            ArrayList<Switch> data= db.getAllSwitches();
            // create senz attributes
            HashMap<String, String> senzAttributes = new HashMap<>();
            for (Switch sw: data){
                senzAttributes.put(sw.getSwitchName(),sw.getStatus() == 1 ? "on":"off");
            }
            Log.d(TAG, "put ============  attributes : " + senzAttributes);
            senzAttributes.put("time", ((Long) (System.currentTimeMillis() / 1000)).toString());

            // new senz
            String id = "_ID";
            String signature = "_SIGNATURE";
            SenzTypeEnum senzType = SenzTypeEnum.PUT;
            User receiver = new User("", "homepi");/////ToDo  Get user name from login details
            Senz senz = new Senz(id, signature, senzType, null, receiver, senzAttributes);
            senzService.send(senz);


        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void get() {

        try {
            ArrayList<Switch> data= db.getAllSwitches();

            // create senz attributes
            HashMap<String, String> senzAttributes = new HashMap<>();
            for (Switch sw: data){
                senzAttributes.put(sw.getSwitchName(),"");
            }
            Log.d(TAG, "get ============  attributes : " + senzAttributes);
            //senzAttributes.put("all","");
            senzAttributes.put("time", ((Long) (System.currentTimeMillis() / 1000)).toString());

            // new senz
            String id = "_ID";
            String signature = "_SIGNATURE";
            SenzTypeEnum senzType = SenzTypeEnum.GET;
            User receiver = new User("", "homepi");/////ToDo  Get user name from login details
            Senz senz = new Senz(id, signature, senzType, null, receiver, senzAttributes);
            senzService.send(senz);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
