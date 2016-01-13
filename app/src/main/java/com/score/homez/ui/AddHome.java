package com.score.homez.ui;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.score.homez.R;
import com.score.homez.db.SwitchesDB;

public class AddHome extends ActionBarActivity implements View.OnClickListener{

    Button create_btn;
    EditText name;
    String table;
    String table_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_home);

        getIntentData();
        initUi();
    }

    private void initUi(){
        create_btn = (Button) findViewById(R.id.create);
        name = (EditText) findViewById(R.id.home_name);
        create_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.create){
            table = name.getText().toString().replace(" ", "_");
            SwitchesDB db = new SwitchesDB(this);
            db.createHomeTable(table);
            Toast.makeText(this, table.replace("_", " ") + " has been created", Toast.LENGTH_LONG).show();
            toHome();
        }
    }

    private void toHome()
    {
        Intent intent = new Intent(AddHome.this, HomeActivity.class);
        intent.putExtra("home", table_name);
        startActivity(intent);
    }

    public void viewHomes() {
        Intent intent = new Intent(AddHome.this, Homes.class);
        intent.putExtra("home", table_name);
        finish();
        startActivity(intent);
    }

    public void viewModes() {
        Intent intent = new Intent(AddHome.this, Modes.class);
        intent.putExtra("home",table_name);
        finish();;
        startActivity(intent);
    }

    public void viewHelp() {
        Intent intent = new Intent(AddHome.this, HelpPage.class);
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
    public void onBackPressed(){
        Intent intent = new Intent(AddHome.this, HomeActivity.class);
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
