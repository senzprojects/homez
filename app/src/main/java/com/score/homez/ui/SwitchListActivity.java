package com.score.homez.ui;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.score.homez.R;
import com.score.homez.pojos.Switchz;

import java.util.ArrayList;

/**
 * Switch activity
 */
public class SwitchListActivity extends Activity {

    // we use custom font here
    private Typeface typeface;

    // switch list
    private ArrayList<Switchz> switchzList;

    private ListView switchListView;
    private SwitchListAdapter switchListAdapter;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.switch_list_layout);

        initUi();
        setupActionBar();
        displaySwitchList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    private void displaySwitchList() {
        switchzList = new ArrayList<>();
        switchzList.add(new Switchz("Night", 1));
        switchzList.add(new Switchz("Day", 1));
        switchzList.add(new Switchz("Visitor", 0));

        switchListAdapter = new SwitchListAdapter(switchzList, this);
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

}
