package com.score.homez.ui;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.score.homez.db.DBSource;
import com.score.homez.ui.HomeActivity.SenzCountDownTimer;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.score.homez.R;

import com.score.homez.utils.ActivityUtils;
import com.score.homez.utils.Switch;
import com.score.senzc.enums.SenzTypeEnum;
import com.score.senzc.pojos.Senz;
import com.score.senzc.pojos.User;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anesu on 1/1/2016.
 */
public class SwitchAdapter extends ArrayAdapter<String> {

    private static final String TAG = ArrayAdapter.class.getName();

    Context context;
    int resource;
    ArrayList<Switch> switches;
    DBSource db;
    private SenzCountDownTimer senzCountDownTimer;
    Typeface typeface;
    int prev = -1;


    public SwitchAdapter(Context context, int resource, ArrayList<Switch> switches,SenzCountDownTimer senzCountDownTimer) {

        super(context, resource);
        this.context = context;
        this.resource = resource;
        db = new DBSource(context);
        this.switches = switches;
        this.senzCountDownTimer=senzCountDownTimer;
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/vegur_2.otf");

    }

    @Override
    public int getCount() {
        return switches.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);

        }
            final String name = switches.get(position).getSwitchName();
            int status = switches.get(position).getStatus();
            final TextView title = (TextView) convertView.findViewById(R.id.name);
            title.setTypeface(typeface, Typeface.BOLD);
            final ToggleButton toggle = (ToggleButton) convertView.findViewById(R.id.toggle);

            toggle.setChecked(status == 1);
            setTitle(toggle.isChecked(), title, name);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggle.setChecked(isChecked);
                setTitle(toggle.isChecked(), title, name);


//                db.toggleSwitch(name, isChecked == true ? 1 : 0);  /// added status to db

                Log.d("state_changed ", name+ " : " +position+" : "+isChecked+" and waiting for responce");
               // senzCountDownTimer.start();

            }
        });

        ObjectAnimator anim = ObjectAnimator.ofFloat(convertView, "translationY", prev > position ? -250 : 250, 0);

        anim.setDuration(400);
        anim.start();

        prev = position;

        return convertView;
    }
    private void setTitle(boolean isChecked, TextView title,String name)
    {
        if(isChecked == false)
        {
            String indicator = "<font color='#F80000'> [OFF]</font>";
            title.setText(Html.fromHtml(name + indicator));
        }
        else
        {
            String indicator = "<font color='#006600'> [ON]</font>";
            title.setText(Html.fromHtml(name + indicator));
        }
    }
}
