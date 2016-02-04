package com.score.homez.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.score.homez.R;
import com.score.homez.pojos.Switch;

import java.util.ArrayList;

/**
 * Created by eranga on 2/2/16.
 */
public class SwitchListAdapter extends BaseAdapter {

    private ArrayList<Switch> switchList;
    private SwitchListActivity activity;
    private Typeface typeface;

    public SwitchListAdapter(ArrayList<Switch> switchList, SwitchListActivity activity) {
        this.switchList = switchList;
        this.activity = activity;
        this.typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/vegur_2.otf");
    }

    @Override
    public int getCount() {
        return switchList.size();
    }

    @Override
    public Object getItem(int position) {
        return switchList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // create custom view
        // A ViewHolder keeps references to children views to avoid unnecessary calls
        // to findViewById() on each row.
        final ViewHolder holder;
        final Switch aSwitch = (Switch) getItem(position);

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.switch_list_row_layout, parent, false);
            holder = new ViewHolder();
            holder.switchName = (TextView) view.findViewById(R.id.name);
            holder.switchToggle = (ToggleButton) view.findViewById(R.id.toggle);

            view.setTag(holder);
        } else {
            // get view holder back
            holder = (ViewHolder) view.getTag();
        }

        // bind view holder content view for efficient use
        holder.switchName.setText(aSwitch.getName());
        holder.switchName.setTypeface(typeface, Typeface.BOLD);
        holder.switchToggle.setChecked(aSwitch.getStatus() == 1);

        // set toggle button click listener
        holder.switchToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.switchToggle.isChecked()) {
                    // update switch status and send PUT with switch on
                    aSwitch.setStatus(0);
                    activity.doPut(aSwitch);
                } else {
                    // update switch status and send PUT with switch off
                    aSwitch.setStatus(1);
                    activity.doPut(aSwitch);
                }
              //  displayConfirmMessageDialog("<font color=#000000>Are you sure you want to switch </font><font color=#eada00><b> [" + (holder.switchToggle.isChecked() ? "ON" : "OFF") + "] </b></font>" + "the switch", holder.switchToggle, aSwitch);
            }
        });

        return view;
    }

    /**
     * Keep reference to children view to avoid unnecessary calls
     */
    static class ViewHolder {
        TextView switchName;
        ToggleButton switchToggle;
    }

    /**
     * Display message dialog when user request(click) to delete invoice
     *
     * @param message message to be display
     */
    public void displayConfirmMessageDialog(String message, final ToggleButton toggleButton, final Switch aSwitch) {
        final Dialog dialog = new Dialog(activity);

        //set layout for dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.confirm_message_dialog_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);

        // set dialog texts
        TextView messageHeaderTextView = (TextView) dialog.findViewById(R.id.information_message_dialog_layout_message_header_text);
        TextView messageTextView = (TextView) dialog.findViewById(R.id.information_message_dialog_layout_message_text);
        messageHeaderTextView.setText("#PUT");
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
                if (toggleButton.isChecked()) {
                    // update switch status and send PUT with switch on
                    aSwitch.setStatus(1);
                    activity.doPut(aSwitch);
                } else {
                    // update switch status and send PUT with switch off
                    aSwitch.setStatus(0);
                    activity.doPut(aSwitch);
                }
                dialog.cancel();
            }
        });

        // cancel button
        Button cancelButton = (Button) dialog.findViewById(R.id.information_message_dialog_layout_cancel_button);
        cancelButton.setTypeface(typeface);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel();
                toggleButton.setChecked(!toggleButton.isChecked());
            }
        });

        dialog.show();
    }
}
