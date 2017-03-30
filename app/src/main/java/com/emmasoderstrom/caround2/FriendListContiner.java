package com.emmasoderstrom.caround2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by User on 2017-03-02.
 */

public class FriendListContiner extends ArrayAdapter<Person> {

    private final Activity context;

    private final Integer[] pic;
    private final String name;
    private final String distance;
    //private final Person[] person;
    private ArrayList<Person> person;



    public FriendListContiner(Activity context, Integer[] startPic, ArrayList<Person> startPerson) {
        super(context, R.layout.list_item, startPerson);

        this.context = context;
        pic = startPic;
        person = startPerson;
        name = "Hej";
        distance = "200";

    }

    @Override
    public int getCount() {
        return person.size();
    }

    @Override
    public Person getItem(int position) {
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
            rowView = (View) inflater.inflate(R.layout.list_item, null,true);
        } else {
            rowView = (View) convertView;
        }

        /*TextView text1 = rowView.getText1();
        TextView text2 = rowView.getText2();

        text1.setText(persons.get(position).getName());
        text2.setText("" + persons.get(position).getAge());*/

        //ImageView imagePicView = (ImageView) rowView.findViewById(R.id.icon);
        TextView textNameView = (TextView) rowView.findViewById(R.id.list_name);
        TextView textDistansView = (TextView) rowView.findViewById(R.id.list_distans);

        //imagePicView.setImageResource(thisPic[position]);
        //textNameView.setText(name[position]);
        //textDistansView.setText(distance[position]);

        textNameView.setText(person.get(position).getFullName());

        /*panelDistansM = getString(R.string.panel_distans_m);
        panelDistansKm = getString(R.string.panel_distans_km);
        panelDistansMil = getString(R.string.panel_distans_mil);*/
        int thisUserDistans = person.get(position).getDistansBetween();
        int distansConverted;
        String distansValue;

        String panelDistansM = context.getString(R.string.panel_distans_m);
        String panelDistansKm = context.getString(R.string.panel_distans_km);
        String panelDistansMil = context.getString(R.string.panel_distans_mil);

        /*if(thisUserDistans < 1000){
            distansConverted = thisUserDistans;
            distansValue = "m";
        }else if(thisUserDistans < 10000){
            distansConverted = thisUserDistans / 1000;
            distansValue = "km";
        }else{
            distansConverted = thisUserDistans / 10000;
            distansValue = "mil";
        }*/

        if (thisUserDistans < 1000) {
            distansConverted = thisUserDistans;
            distansValue = panelDistansM;

            textDistansView.setText(distansConverted + " " + distansValue);
        } else if (thisUserDistans < 10000) {
            Double distansConvertedDouble = Double.valueOf(thisUserDistans) / 1000;
            double distansConvertedDouble1decimals = (int)Math.round(distansConvertedDouble * 10)/(double)10;
            distansValue = panelDistansKm;

            textDistansView.setText(distansConvertedDouble1decimals + " " + distansValue);
        } else {
            distansConverted = thisUserDistans / 10000;
            distansValue = panelDistansMil;

            textDistansView.setText(distansConverted + " " + distansValue);
        }

        //textDistansView.setText(distansConverted + " " + distansValue);


        //textDistansView.setText(String.valueOf(person.get(position).getDistansBetween()));

        return rowView;
    }
}