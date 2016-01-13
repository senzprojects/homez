package com.score.homez.utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.score.homez.R;
import com.score.homez.ui.HomeActivity;

import java.util.ArrayList;

/**
 * Created by Anesu on 1/11/2016.
 */
public class HomesListAdapter extends ArrayAdapter {
    Context context;
    ArrayList<String> home_names;

    public HomesListAdapter(Context context, int resource, ArrayList<String> home_names) {
        super(context, resource);
        this.context = context;
        this.home_names = home_names;
    }

    @Override
    public int getCount() {
        return home_names.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.home_row, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.home_name);
        ImageButton delete = (ImageButton) convertView.findViewById(R.id.delete);

        name.setText(home_names.get(position).replace("_", " "));

        return convertView;
    }
}
