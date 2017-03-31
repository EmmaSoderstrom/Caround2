package com.emmasoderstrom.caround2;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by User on 2017-03-30.
 */

public class PhoneBookContacts extends ArrayAdapter<String> {

    private final Context context;
    private ArrayList<String> name;


    public PhoneBookContacts(Context context, ArrayList<String> sName) {
        super(context, 0, sName);

        this.context = context;
        name = sName;
    }

    @Override
    public int getCount() {
        return name.size();
    }

    @Override
    public String getItem(int position) {
        return name.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = (View) inflater.inflate(R.layout.add_friend_list_item, null,true);
        } else {
            rowView = (View) convertView;
        }

        TextView textNameView = (TextView) rowView.findViewById(R.id.tv_name);
        textNameView.setText(name.get(position));

        return rowView;

    }
}