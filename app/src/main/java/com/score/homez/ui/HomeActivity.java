package com.score.homez.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.score.homez.R;
import com.score.homez.db.DBSource;
import com.score.homez.utils.ActivityUtils;
import com.score.homez.utils.NetworkUtil;
import com.score.senz.ISenzService;
import com.score.senzc.enums.SenzTypeEnum;
import com.score.senzc.pojos.Senz;
import com.score.senzc.pojos.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class  HomeActivity extends Activity implements View.OnClickListener {

    private static final String TAG = HomeActivity.class.getName();

    //put message variables
    private String lastSwitch;
    private  String lastStatus;

    // use to track share timeout
    private SenzCountDownTimer senzCountDownTimer;
    private boolean isResponseReceivedPut;
    private boolean isResponseReceivedGet;

    // we use custom font here
    private Typeface typeface;


    private final Spanned night_on = Html.fromHtml("<font color='#4a4a4a'>Night Mode </font> <font color='#eada00'>[ON]</font>");
    private final Spanned night_off = Html.fromHtml("<font color='#4a4a4a'>Night Mode </font> <font color='red'>[OFF]</font>");
    private final Spanned visitor_on = Html.fromHtml("<font color='#4a4a4a'>Visitor Mode </font> <font color='#eada00'>[ON]</font>");
    private final Spanned visitor_off = Html.fromHtml("<font color='#4a4a4a'>Visitor Mode </font> <font color='red'>[OFF]</font>");

    // layout components
    private TextView nightModeText;
    private TextView visitorModeText;
    private ToggleButton nightModeButton;
    private ToggleButton visitorModeButton;

    DBSource dbSource;

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

        registerReceiver(senzMessageReceiver, new IntentFilter("com.score.senzc.DATA"));
        senzCountDownTimer = new SenzCountDownTimer(16000, 5000);
        isResponseReceivedPut = true;
        isResponseReceivedGet = true;

        initUi();
        setupActionBar();
        bindSenzService();
        dbSource = new DBSource(getApplicationContext());
        if(NetworkUtil.isAvailableNetwork(this)){
            if(dbSource.getSwitches().size()>0) {
                isResponseReceivedGet = false;
                senzCountDownTimer.start();

            } else {
                Toast toast = Toast.makeText(this.getBaseContext(), "SHARE Your Smart Home Switches to this device Before use", Toast.LENGTH_LONG);
                toast.getView().setBackgroundColor(Color.RED);
                toast.setGravity(Gravity.TOP, 0, 70);
                toast.getView().setPadding(10, 10, 10, 10);
                TextView text = (TextView) toast.getView().findViewById(android.R.id.message);
                text.setTextColor(Color.WHITE);
                text.setTextSize(18);
                toast.show();
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(View tb) {

        if(NetworkUtil.isAvailableNetwork(this)) {

            if (dbSource.getSwitches().size() > 0) {

                if (tb == nightModeButton) {
                    if (1 == dbSource.getStatus("s1")) {
                        lastSwitch = "s1";//ToDo check only swith instead of db for if
                        lastStatus = "off";
                        isResponseReceivedPut = false;
                        senzCountDownTimer.start();
                    } else {
                        lastSwitch = "s1";
                        lastStatus = "on";
                        isResponseReceivedPut = false;
                        senzCountDownTimer.start();
                    }
                } else if (tb == visitorModeButton) {
                    if (1 == dbSource.getStatus("s2")) {
                        lastSwitch = "s2";
                        lastStatus = "off";
                        isResponseReceivedPut = false;
                        senzCountDownTimer.start();
                    } else {
                        lastSwitch = "s2";
                        lastStatus = "on";
                        isResponseReceivedPut = false;
                        senzCountDownTimer.start();
                    }
                }
            } else {
                Toast toast = Toast.makeText(this.getBaseContext(), "SHARE Your Smart Home Switches to this device Before use", Toast.LENGTH_LONG);
                toast.getView().setBackgroundColor(Color.RED);
                toast.setGravity(Gravity.TOP, 0, 70);
                toast.getView().setPadding(10, 10, 10, 10);
                TextView text = (TextView) toast.getView().findViewById(android.R.id.message);
                text.setTextColor(Color.WHITE);
                text.setTextSize(18);
                toast.show();

            }
        }
        else{
            Toast.makeText(this,"No Network Conection Available",Toast.LENGTH_LONG).show();
        }
    }
    /**     }
     * Initialize UI components
     */
    private void initUi() {
        typeface = Typeface.createFromAsset(getAssets(), "fonts/vegur_2.otf");

        nightModeText = (TextView) findViewById(R.id.text_night_mode);
        visitorModeText = (TextView) findViewById(R.id.text_visitor_mode);

        nightModeButton = (ToggleButton) findViewById(R.id.switch_night_mode);
        visitorModeButton = (ToggleButton) findViewById(R.id.switch_visitor_mode);

        nightModeButton.setOnClickListener(this);
        visitorModeButton.setOnClickListener(this);

        nightModeText.setTypeface(typeface, Typeface.BOLD);
        visitorModeText.setTypeface(typeface, Typeface.BOLD);

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
    private class SenzCountDownTimer extends CountDownTimer {

        public SenzCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // if response not received yet, resend share
            if (!isResponseReceivedPut) {
                ActivityUtils.showProgressDialog(HomeActivity.this,"Please wait ...");
                put();
                Log.d(TAG, "Put Response not received yet");
            }
            if (!isResponseReceivedGet) {
                ActivityUtils.showProgressDialog(HomeActivity.this, "Please wait ...");
                get();
                Log.d(TAG, "Get Response not received yet");
            }
        }

        @Override
        public void onFinish() {
            ActivityUtils.cancelProgressDialog();

            // display message dialog that we couldn't reach the user
            if (!isResponseReceivedPut) {//ToDo generalize String senzclient - Homep
                String message = "<font color=#000000>Seems we couldn't reach the </font> <font color=#eada00>" + "<b>" + "Homep" + "</b>" + "</font> <font color=#000000> at this moment</font>";
                displayInformationMessageDialog("#PUT Fail", message);
                isResponseReceivedPut=true;
                nightModeButton.setChecked(1==dbSource.getStatus("s1"));
                visitorModeButton.setChecked(1==dbSource.getStatus("s2"));

            }
            if (!isResponseReceivedGet) {
                String message = "<font color=#000000>Seems we couldn't reach the </font> <font color=#eada00>" + "<b>" + "Homep" + "</b>" + "</font> <font color=#000000> at this moment</font>";
                displayInformationMessageDialog("#GET Fail", message);
                isResponseReceivedGet=true;
            }
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
                    isResponseReceivedPut = true;
                    for(Map.Entry<String, String> entry : senz.getAttributes().entrySet()) {
                        String key = entry.getKey();
                        int value;
                        if(key.equals("s1")){
                            value = Integer.parseInt(entry.getValue());
                            dbSource.setStatus(key,value);
                            nightModeButton.setChecked(1==dbSource.getStatus(key));
                            if(1==value) nightModeText.setText(night_on);
                            else nightModeText.setText(night_off);
                        }
                        if(key.equals("s2")){
                            value = Integer.parseInt(entry.getValue());
                            dbSource.setStatus(key,value);
                            visitorModeButton.setChecked(1==value);
                            if(1==value) visitorModeText.setText(visitor_on);
                            else visitorModeText.setText(visitor_off);
                        }
                    }

                }
                else if (msg != null && msg.equalsIgnoreCase("GetResponse")) {
                    isResponseReceivedGet = true;
                    Toast.makeText(this.getBaseContext(), "Status Received", Toast.LENGTH_SHORT).show();
                    for(Map.Entry<String, String> entry : senz.getAttributes().entrySet()) {
                        String key = entry.getKey();
                        int value;
                        if(key.equals("s1")){
                            //Log.d(TAG, "Get Response  === key   ;   vale  ==== "+key +" : "+entry.getValue());
                            value = Integer.parseInt(entry.getValue());
                            dbSource.setStatus(key,value);
                            nightModeButton.setChecked(1==value);
                            if(1==value) nightModeText.setText(night_on);
                            else nightModeText.setText(night_off);
                        }
                        if(key.equals("s2")){
                            //Log.d(TAG, "Get response === key   ;   vale ===== "+key +" : "+entry.getValue());
                            value = Integer.parseInt(entry.getValue());
                            dbSource.setStatus(key, value);
                            visitorModeButton.setChecked(1 == value);
                            if(1==value) visitorModeText.setText(visitor_on);
                            else visitorModeText.setText(visitor_off);
                        }
                    }

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
            HashMap<String, String> senzAttributes = new HashMap<>();
            Log.d(TAG, "put ============ "+lastSwitch+"    :    "+lastStatus);
            senzAttributes.put(lastSwitch, lastStatus);
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
            ArrayList<String> data= dbSource.getSwitches();

            // create senz attributes
            HashMap<String, String> senzAttributes = new HashMap<>();
            for (String sw: data){
                senzAttributes.put(sw,"");
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
