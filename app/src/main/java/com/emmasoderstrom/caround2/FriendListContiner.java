package com.emmasoderstrom.caround2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class FriendListContiner extends ArrayAdapter<String> {

    private final Activity context;
    private ArrayList<String> person;


    public FriendListContiner(Activity context, ArrayList<String> startPerson) {
        super(context, 0, startPerson);

        this.context = context;
        person = startPerson;
    }

    @Override
    public int getCount() {
        return person.size();
    }

    @Override
    public String getItem(int position) {
        return person.get(position);
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
            rowView = (View) inflater.inflate(R.layout.main_list_item, null,true);
        } else {
            rowView = (View) convertView;
        }

        TextView textNameView = (TextView) rowView.findViewById(R.id.list_name);

        return rowView;
    }
}