package com.score.homez.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.score.homez.R;

public class HelpPage extends ActionBarActivity {

    String table_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_page);

        getIntentData();
        setupActionBar();
    }

    private void setupActionBar() {
        getSupportActionBar().setTitle("Help");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(HelpPage.this, HomeActivity.class);
        intent.putExtra("home", table_name);
        startActivity(intent);
    }

    public void viewHomes() {
        Intent intent = new Intent(HelpPage.this, Homes.class);
        intent.putExtra("home", table_name);
        finish();
        startActivity(intent);
    }

    public void viewModes() {
        Intent intent = new Intent(HelpPage.this, Modes.class);
        intent.putExtra("home",table_name);
        finish();;
        startActivity(intent);
    }

    public void viewHelp() {
        Intent intent = new Intent(HelpPage.this, HelpPage.class);
        finish();
        startActivity(intent);
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
        }else if(id == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

}
