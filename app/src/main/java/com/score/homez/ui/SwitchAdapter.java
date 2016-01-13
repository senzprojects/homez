package com.score.homez.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.score.homez.R;
import com.score.homez.db.SwitchesDB;
import com.score.homez.utils.Switch;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Anesu on 1/1/2016.
 */
public class SwitchAdapter extends ArrayAdapter<String> {
    Context context;
    int resource;
    ArrayList<Switch> switches;
    SwitchesDB db;
    Typeface typeface;
    String table;
    int prev = -1;
    public SwitchAdapter(Context context, int resource,String table) {
        super(context, resource);
        this.context = context;
        this.table = table;
        this.resource = resource;
        db = new SwitchesDB(context);
        switches = new ArrayList<>();
        for(Switch aSwitch : db.getAllSwitches(table))
        {
            if(!aSwitch.getSwitchName().equals("Night Mode") && !aSwitch.getSwitchName().equals("Visitor Mode")){
                switches.add(aSwitch);
            }
        }
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
        final ToggleButton toggle  = (ToggleButton) convertView.findViewById(R.id.toggle);
        toggle.setChecked(status==1);

        setTitle(toggle.isChecked(), title, name);

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggle.setChecked(isChecked);
                setTitle(toggle.isChecked(), title, name);
                db.toggleSwitch(table, name, isChecked==true ? 1 : 0);
                Log.i("state_changed", name + " has been toggled");
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
