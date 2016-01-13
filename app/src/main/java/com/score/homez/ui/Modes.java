package com.score.homez.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.score.homez.R;
import com.score.homez.db.SwitchesDB;
import com.score.homez.utils.Switch;

import java.util.ArrayList;
import java.util.Arrays;

public class Modes extends ActionBarActivity implements ToggleButton.OnCheckedChangeListener {

    ToggleButton visitor, night;
    TextView night_txt, visitor_txt;
    String table_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modes);

        getIntentData();
        setupActionBar();
        setupModes();
    }

    private void setupActionBar() {
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(table_name.replace("_", " ") + " Modes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        String table_name = intent.getStringExtra("home");

        if (table_name != null) {
            this.table_name = table_name;
        } else {
            this.table_name = "Main_Home";
        }
    }


    private void setupModes() {
        visitor = (ToggleButton) findViewById(R.id.switch_visitor_mode);
        night = (ToggleButton) findViewById(R.id.switch_night_mode);

        visitor_txt = (TextView) findViewById(R.id.text_visitor_mode);
        night_txt = (TextView) findViewById(R.id.text_night_mode);
        visitor.setOnCheckedChangeListener(this);
        night.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.switch_visitor_mode) {
            if (isChecked == false) {
                String indicator = "<font color='#F80000'> [OFF]</font>";
                visitor_txt.setText(Html.fromHtml("Visitor Mode" + indicator));
            } else {
                String indicator = "<font color='#006600'> [ON]</font>";
                visitor_txt.setText(Html.fromHtml("Visitor Mode" + indicator));
            }
        } else if (buttonView.getId() == R.id.switch_night_mode) {
            if (isChecked == false) {
                String indicator = "<font color='#F80000'> [OFF]</font>";
                night_txt.setText(Html.fromHtml("Night Mode" + indicator));
            } else {
                String indicator = "<font color='#006600'> [ON]</font>";
                night_txt.setText(Html.fromHtml("Night Mode" + indicator));
            }
        }
    }
    public void viewHomes() {
        Intent intent = new Intent(Modes.this, Homes.class);
        intent.putExtra("home", table_name);
        finish();
        startActivity(intent);
    }

    public void viewModes() {
        Intent intent = new Intent(Modes.this, Modes.class);
        intent.putExtra("home",table_name);
        finish();;
        startActivity(intent);
    }

    public void viewHelp() {
        Intent intent = new Intent(Modes.this, HelpPage.class);
        finish();
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Modes.this, HomeActivity.class);
        intent.putExtra("home", table_name);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
        }else if(id == R.id.action_help){
            viewHelp();
        }
        else if(id == R.id.action_homes){
            viewHomes();
        }
        else if(id == R.id.action_modes){
            viewModes();
        }


        return super.onOptionsItemSelected(item);
    }
}
