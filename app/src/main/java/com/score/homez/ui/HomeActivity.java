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
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.score.homez.R;
import com.score.homez.db.DBSource;
import com.score.homez.utils.ActivityUtils;
import com.score.senz.ISenzService;
import com.score.senzc.enums.SenzTypeEnum;
import com.score.senzc.pojos.Senz;
import com.score.senzc.pojos.User;

import java.util.HashMap;


public class HomeActivity extends Activity implements View.OnClickListener {

    private static final String TAG = HomeActivity.class.getName();

    // use to track share timeout
    private SenzCountDownTimer senzCountDownTimer;
    private boolean isResponseReceived;

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
            Log.d("TAG", "Disconnected from senz service");
        }
    };

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerReceiver(senzMessageReceiver, new IntentFilter("DATA"));
        senzCountDownTimer = new SenzCountDownTimer(16000, 5000);
        isResponseReceived = false;

        initUi();
        setupActionBar();
        bindSenzService();
        dbSource = new DBSource(getApplicationContext());

        if (1 == dbSource.getStatus("GPIO_1")) {
            nightModeButton.setChecked(true);
            nightModeText.setText(night_on);
        } else {
            nightModeButton.setChecked(false);
            nightModeText.setText(night_off);
        }

        if (1 == dbSource.getStatus("GPIO_2")) {
            visitorModeButton.setChecked(true);
            visitorModeText.setText(visitor_on);
        } else {
            visitorModeButton.setChecked(false);
            visitorModeText.setText(visitor_off);
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
        if (tb == nightModeButton) {
            if (1 == dbSource.getStatus("GPIO_1")) {
                nightModeButton.setChecked(false);
                dbSource.setStatus("GPIO_1", 0);
                nightModeText.setText(night_off);
            } else {
                nightModeButton.setChecked(true);
                dbSource.setStatus("GPIO_1", 1);
                nightModeText.setText(night_on);
            }
        } else if (tb == visitorModeButton) {
            if (1 == dbSource.getStatus("GPIO_2")) {
                visitorModeButton.setChecked(false);
                dbSource.setStatus("GPIO_2", 0);
                visitorModeText.setText(visitor_off);
            } else {
                visitorModeButton.setChecked(true);
                dbSource.setStatus("GPIO_2", 1);
                visitorModeText.setText(visitor_on);
            }
        }
    }

    /**
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
        intent.setClassName("com.score.senz", "com.score.senz.services.RemoteSenzService");
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
            if (!isResponseReceived) {
                put();
                Log.d(TAG, "Response not received yet");
            }
        }

        @Override
        public void onFinish() {
            ActivityUtils.cancelProgressDialog();

            // display message dialog that we couldn't reach the user
            if (!isResponseReceived) {
                String message = "<font color=#000000>Seems we couldn't reach the </font> <font color=#eada00>" + "<b>" + "Homep" + "</b>" + "</font> <font color=#000000> at this moment</font>";
                displayInformationMessageDialog("#Share Fail", message);
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

        if (action.equalsIgnoreCase("DATA")) {
            Senz senz = intent.getExtras().getParcelable("SENZ");

            if (senz.getAttributes().containsKey("msg")) {
                // msg response received
                ActivityUtils.cancelProgressDialog();
                isResponseReceived = true;
                senzCountDownTimer.cancel();

                String msg = senz.getAttributes().get("msg");
                if (msg != null && msg.equalsIgnoreCase("PutDone")) {
                    //onPostShare();
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
            senzAttributes.put("gpio", "on");
            senzAttributes.put("time", ((Long) (System.currentTimeMillis() / 1000)).toString());

            // new senz
            String id = "_ID";
            String signature = "_SIGNATURE";
            SenzTypeEnum senzType = SenzTypeEnum.SHARE;
            User receiver = new User("", "homep");
            Senz senz = new Senz(id, signature, senzType, null, receiver, senzAttributes);

            senzService.send(senz);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
