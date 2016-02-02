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
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.score.homez.R;
import com.score.senz.ISenzService;

/**
 * Switch activity
 */
public class SwitchActivity extends Activity implements View.OnClickListener {

    // we use custom font here
    private Typeface typeface;

    // layout components
    private RelativeLayout nightModeLayout;
    private RelativeLayout visitorModeLayout;
    private TextView nightModeText;
    private TextView visitorModeText;

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
        setContentView(R.layout.switch_board_layout);

        bindSenzService();
        initUi();
        setupActionBar();
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
     * Bind with senz service
     */
    private void bindSenzService() {
        Intent intent = new Intent();
        intent.setClassName("com.score.senz", "com.score.senz.services.RemoteSenzService");
        bindService(intent, senzServiceConnection, Context.BIND_AUTO_CREATE);
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
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
        TextView yourTextView = (TextView) findViewById(titleId);
        yourTextView.setTextColor(getResources().getColor(R.color.white));
        yourTextView.setTypeface(typeface);

        getActionBar().setTitle("Homez");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(View v) {

    }
}
