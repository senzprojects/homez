package com.score.homez.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.score.homez.R;
import com.score.senz.ISenzService;

/**
 * Switch activity
 */
public class SwitchActivity extends Activity {

    // we use custom font here
    private Typeface typeface;

    // layout components
    private ToggleButton switchButton;
    private TextView switchText;

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
        setContentView(R.layout.switch_layout);

        initUi();
        setupActionBar();
        bindSenzService();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(senzServiceConnection);
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
     * Initialize UI components
     */
    private void initUi() {
        typeface = Typeface.createFromAsset(getAssets(), "fonts/vegur_2.otf");

        switchButton = (ToggleButton) findViewById(R.id.switch_night_mode);
        switchText = (TextView) findViewById(R.id.switch_text);
        switchText.setTypeface(typeface, Typeface.BOLD);
    }

    /**
     * Set up action bar
     */
    private void setupActionBar() {
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
        TextView yourTextView = (TextView) findViewById(titleId);
        yourTextView.setTextColor(getResources().getColor(R.color.white));
        yourTextView.setTypeface(typeface);

        getActionBar().setTitle("Night mode");
    }

    /**
     * Bind with senz service
     */
    private void bindSenzService() {
        Intent intent = new Intent();
        intent.setClassName("com.score.senz", "com.score.senz.services.RemoteSenzService");
        bindService(intent, senzServiceConnection, Context.BIND_AUTO_CREATE);
    }

}

  //      context=getApplicationContext();
    //    DBSource dbSource = new DBSource(context);
      //  dbSource.createSwitch("GPIO_01");
        //System.out.println("********************************************" + dbSource.getStatus("GPIO_01"));
        //dbSource.setStatus("GPIO_01", 1);
     //   System.out.println("/*/*/*/****************************" + dbSource.getStatus("GPIO_01"));
     //   dbSource.updateSwitch("GPIO_01", "GPIO_2");
     //   System.out.println("/*/*/*/****************************" + dbSource.getStatus("GPIO_2"));