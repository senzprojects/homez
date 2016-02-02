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
import android.widget.ListView;
import android.widget.TextView;

import com.score.homez.R;
import com.score.homez.db.HomezDbSource;
import com.score.homez.pojos.Switch;
import com.score.homez.utils.ActivityUtils;
import com.score.homez.utils.SenzUtils;
import com.score.senz.ISenzService;
import com.score.senzc.pojos.Senz;

import java.util.ArrayList;

/**
 * Switch activity
 */
public class SwitchListActivity extends Activity {

    private static final String TAG = SwitchListActivity.class.getName();

    // we use custom font here
    private Typeface typeface;

    // switch list
    private ArrayList<Switch> switchList;

    private ListView switchListView;
    private SwitchListAdapter switchListAdapter;

    // service interface
    private ISenzService senzService = null;

    // service bind status
    private boolean isServiceBound = false;

    // keep track with weather response received
    private boolean isResponseReceived = false;

    // timers fot get/put
    private CountDownTimer getTimer;
    private CountDownTimer putTimer;

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

    // Receives data senz to here
    private BroadcastReceiver senzMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Got message from Senz service");
            handleMessage(intent);
        }
    };

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.switch_list_layout);

        bindSenzService();
        registerReceiver(senzMessageReceiver, new IntentFilter("com.score.senz.DATA_SENZ"));

        initUi();
        setupActionBar();
        popUpSwitchList();

        // TODO update switch status on startup
        //doGet(switchList);
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

        switchListView = (ListView) findViewById(R.id.switch_list);

        // add header and footer for list
        View headerView = View.inflate(this, R.layout.list_footer, null);
        View footerView = View.inflate(this, R.layout.list_footer, null);
        switchListView.addHeaderView(headerView);
        switchListView.addFooterView(footerView);
    }

    /**
     * Display switch list
     */
    private void popUpSwitchList() {
        // TODO get switch list via db
        //switchList = (ArrayList<Switch>) new HomezDbSource(this).getAllSwitches();

        // TODO create sample list now, remove this
        switchList = new ArrayList<>();
        switchList.add(new Switch("Night", 1));
        switchList.add(new Switch("Day", 1));
        switchList.add(new Switch("Visitor", 0));

        switchListAdapter = new SwitchListAdapter(switchList, this);
        switchListAdapter.notifyDataSetChanged();
        switchListView.setAdapter(switchListAdapter);
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

        getActionBar().setTitle("Homez");
    }

    /**
     * Send periodic PUT request via timer
     *
     * @param aSwitch
     */
    public void doPut(Switch aSwitch) {
        // create put senz
        final Senz senz = SenzUtils.createPutSenz(aSwitch, this);

        if (senz != null) {
            ActivityUtils.showProgressDialog(this, "Please wait...");
            putTimer = new CountDownTimer(16000, 5000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (!isResponseReceived) {
                        Log.d(TAG, "Response not received yet");

                        // send put
                        try {
                            senzService.send(senz);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFinish() {
                    ActivityUtils.hideSoftKeyboard(SwitchListActivity.this);
                    ActivityUtils.cancelProgressDialog();

                    // display message dialog that we couldn't reach the user
                    if (!isResponseReceived) {
                        String message = "<font color=#000000>Seems we couldn't reach the home </font> <font color=#eada00>" + "<b>" + "NAME" + "</b>" + "</font> <font color=#000000> at this moment</font>";
                        displayInformationMessageDialog("#PUT Fail", message);
                    }
                }
            };
            putTimer.start();
        }
    }

    /**
     * Send periodic GET senz via timer
     *
     * @param switchList
     */
    private void doGet(ArrayList<Switch> switchList) {
        // create get senz
        final Senz senz = SenzUtils.createGetSenz(switchList, this);

        if (senz != null) {
            getTimer = new CountDownTimer(16000, 5000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (!isResponseReceived) {
                        Log.d(TAG, "Response not received yet");

                        // send get
                        try {
                            senzService.send(senz);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFinish() {
                    ActivityUtils.hideSoftKeyboard(SwitchListActivity.this);
                    ActivityUtils.cancelProgressDialog();

                    // display message dialog that we couldn't reach the user
                    if (!isResponseReceived) {
                        String message = "<font color=#000000>Seems we couldn't reach the home </font> <font color=#eada00>" + "<b>" + "NAME" + "</b>" + "</font> <font color=#000000> at this moment</font>";
                        displayInformationMessageDialog("#PUT Fail", message);
                    }
                }
            };
            getTimer.start();
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
                // response received for PUT senz
                ActivityUtils.cancelProgressDialog();
                isResponseReceived = true;

                onPostPut(senz);
            } else if (senz.getAttributes().containsKey("#nightmode") || senz.getAttributes().containsKey("#visitormode")) {
                // response received for GET senz
                ActivityUtils.cancelProgressDialog();
                isResponseReceived = true;

                onPostGet(senz);
            }
        }
    }

    /**
     * Call after PUT response
     *
     * @param senz
     */
    private void onPostPut(Senz senz) {
        putTimer.cancel();

        String msg = senz.getAttributes().get("msg");
        if (msg != null && msg.equalsIgnoreCase("PutDone")) {
            // TODO update switch in db
            //new HomezDbSource(this).setSwitchStatus(new Switchz(name, status));

            // reload list
            switchList = (ArrayList<Switch>) new HomezDbSource(this).getAllSwitches();
            switchListAdapter = new SwitchListAdapter(switchList, this);
            switchListView.setAdapter(switchListAdapter);
        } else {
            String message = "<font color=#000000>Seems we couldn't access the switch </font> <font color=#eada00>" + "<b>" + "NAME" + "</b>" + "</font>";
            displayInformationMessageDialog("#PUT Fail", message);
        }
    }

    /**
     * Call after GET response
     *
     * @param senz
     */
    private void onPostGet(Senz senz) {
        getTimer.cancel();

        // TODO update switch status in DB
        //new HomezDbSource(this).setSwitchStatus(new Switchz(name, status));

        // reload list
        switchList = (ArrayList<Switch>) new HomezDbSource(this).getAllSwitches();
        switchListAdapter = new SwitchListAdapter(switchList, this);
        switchListView.setAdapter(switchListAdapter);
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
