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
import com.score.homez.pojos.Switchz;
import com.score.senzc.pojos.User;

import java.util.ArrayList;

/**
 * Created by eranga on 2/2/16.
 */
public class SwitchListAdapter extends BaseAdapter {

    private ArrayList<Switchz> switchzList;
    private Context context;
    private Typeface typeface;

    public SwitchListAdapter(ArrayList<Switchz> switchzList, Context context) {
        this.switchzList = switchzList;
        this.context = context;
        this.typeface = Typeface.createFromAsset(context.getAssets(), "fonts/vegur_2.otf");
    }

    @Override
    public int getCount() {
        return switchzList.size();
    }

    @Override
    public Object getItem(int position) {
        return switchzList.get(position);
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
        final Switchz switchz = (Switchz) getItem(position);

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        holder.switchName.setText(switchz.getName());
        holder.switchName.setTypeface(typeface, Typeface.BOLD);
        holder.switchToggle.setChecked(switchz.getStatus() == 1);

        // set toggle button click listener
        holder.switchToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                displayDeleteMessageDialog("Sure to switch on ", new User("1", "sdf"));
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
    public void displayDeleteMessageDialog(String message, final User user) {
        final Dialog dialog = new Dialog(context);

        //set layout for dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.confirm_message_dialog_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
                dialog.cancel();
            }
        });

        // cancel button
        Button cancelButton = (Button) dialog.findViewById(R.id.information_message_dialog_layout_cancel_button);
        cancelButton.setTypeface(typeface);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }
}
