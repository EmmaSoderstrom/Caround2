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

        //super(context, 0, users);
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
        //TextView textDistansView = (TextView) rowView.findViewById(R.id.list_distans);

        textNameView.setText(name.get(position));

        //String thisUserDistans = name.get(position);

        //textDistansView.setText("hej");

        return rowView;






















        /*View rowView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = (View) inflater.inflate(R.layout.contacts_list_item, null,true);
        } else {
            rowView = (View) convertView;
        }*/

        // Get the data item for this position

       /* String name = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.add_friend_list_item, parent, false);
        }

        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.name);
        //TextView tvHome = (TextView) convertView.findViewById(R.id.tvHome);

        // Populate the data into the template view using the data object
        tvName.setText(name);
        //tvName.setText(name.get(position).getFullName());
        //tvHome.setText(user.hometown);

        // Return the completed view to render on screen
        return convertView;*/
    }
}