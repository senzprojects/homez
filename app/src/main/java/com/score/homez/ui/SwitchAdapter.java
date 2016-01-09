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
import android.provider.Settings;
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
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

/**
 * Created by Anesu on 1/1/2016.
 */
public class SwitchAdapter extends ArrayAdapter<Switch> {

    private static final String TAG = ArrayAdapter.class.getName();

    Context context;
    int resource;
    ArrayList<Switch> switches;
    DBSource db;
   private SenzCountDownTimer senzCountDownTimer;
    Typeface typeface;

    public SwitchAdapter(Context context, int resource, ArrayList<Switch> switches1,SenzCountDownTimer senzCountDownTimer) {

        super(context, resource);
        this.context = context;
        this.resource = resource;
        db = new DBSource(context);
        this.switches = switches1;
        this.senzCountDownTimer=senzCountDownTimer;
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/vegur_2.otf");

    }

    @Override
    public int getCount() {
        return switches.size();
    }


    @Override

    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }
    static class ViewHolder{
        ToggleButton toggleOk;
        TextView title;
    }

    public void setToggleList( ArrayList<Switch> list ){
        this.switches = list;
        notifyDataSetChanged();
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder = null;

        if ( convertView == null )
        {
        /* There is no view at this position, we create a new one.
           In this case by inflating an xml layout */
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
            holder = new ViewHolder();
            final String name = switches.get(position).getSwitchName();
            final int status=switches.get(position).getStatus();
            holder.toggleOk = (ToggleButton) convertView.findViewById( R.id.toggle );
            holder.title = (TextView) convertView.findViewById(R.id.name);
            holder.title.setTypeface(typeface, Typeface.BOLD);
            setTitle(status == 1, holder.title, name);
            convertView.setTag (holder);
        }
        else
        {
        /* We recycle a View that already exists */
            final String name = switches.get(position).getSwitchName();
            final int status=switches.get(position).getStatus();

            holder = (ViewHolder) convertView.getTag ();
            setTitle(status == 1, holder.title, name);
        }

        // Once we have a reference to the View we are returning, we set its values.

        holder.toggleOk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (db.getAllSwitches().size() > 0) {
                    final String name = switches.get(position).getSwitchName();
                    db.toggleSwitch(name, isChecked == true ? 5 : 3);   ///3 :-temporary off , 5 :-teporary on
                    //switches.get(position).setStatus(isChecked == true ? 1 : 0);
                    //setToggleList(switches);

                    Log.e("state_changed ", position + " : " + isChecked + " and waiting for responce");
                    senzCountDownTimer.start();

            } else {
                    String message = "<font color=#000000>Switches are NOT SHARED from </font> <font color=#eada00>" + "<b>" + "SmartHome" + "</b>" + "</font> <font color=#000000> <br> Please SHARE Them</font>";
                    displayInformationMessageDialog("#SHARE NOT RECIEVED", message);
                }
            }
        });

        // Here is where you should set the ToggleButton value for this item!!!

        holder.toggleOk.setChecked( switches.get( position ).getStatus()==1 );

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

    public void displayInformationMessageDialog(String title, String message) {
        final Dialog dialog = new Dialog(context);

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
