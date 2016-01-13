package com.score.homez.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.score.homez.R;
import com.score.homez.db.SwitchesDB;
import com.score.homez.utils.HomesListAdapter;

public class Homes extends ActionBarActivity implements View.OnClickListener{

    ListView homes_list;
    ImageButton add;
    SwitchesDB db;
    String table_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homes);

        setupActionBar();
        initUi();
    }

    private void initUi(){
        db = new SwitchesDB(this);
        homes_list = (ListView) findViewById(R.id.pick_home);
        add = (ImageButton) findViewById(R.id.add);
        add.setOnClickListener(this);
        HomesListAdapter adapter = new HomesListAdapter(this, R.layout.home_row, db.getAllHomes());
        homes_list.setAdapter(adapter);
        homes_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Homes.this, HomeActivity.class);
                finish();
                intent.putExtra("home", db.getAllHomes().get(position));
                startActivity(intent);
            }
        });

    }

    private void setupActionBar() {
        getSupportActionBar().setTitle("All Homes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Homes.this, AddHome.class);
        startActivity(intent);
    }

    public void viewHomes() {
        Intent intent = new Intent(Homes.this, Homes.class);
        intent.putExtra("home", table_name);
        finish();
        startActivity(intent);
    }

    public void viewModes() {
        Intent intent = new Intent(Homes.this, Modes.class);
        intent.putExtra("home", table_name);
        finish();
        startActivity(intent);
    }

    public void viewHelp() {
        Intent intent = new Intent(Homes.this, HelpPage.class);
        finish();
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Homes.this, HomeActivity.class);
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
