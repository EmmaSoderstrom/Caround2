package com.emmasoderstrom.caround2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.support.v7.widget.RecyclerView.*;

/**
 * Created by User on 2017-03-02.
 */

public class MainListContiner extends ArrayAdapter<Person> {

    private final Context context;
    private ArrayList<Person> person;

    //ViewHolder holder;
    ImageView imagePicView;


    public MainListContiner(Context context, ArrayList<Person> startPerson) {
        super(context, 0, startPerson);

        this.context = context;
        person = startPerson;

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
            rowView = inflater.inflate(R.layout.main_list_item, null,true);
        } else {
            rowView = convertView;
        }

        imagePicView = (ImageView) rowView.findViewById(R.id.list_pic);
        TextView textNameView = (TextView) rowView.findViewById(R.id.list_name);
        TextView textTimwView = (TextView) rowView.findViewById(R.id.list_time);
        TextView textDistansView = (TextView) rowView.findViewById(R.id.list_distans);

        //bild
        String userPicS = person.get(position).getPicString();
        new DownloadImage().execute(userPicS);

        //Håller bilden på plats till den rätta raden i listan
        Picasso.with(context)
                .load(userPicS)
                .resize(50, 50)
                .centerCrop()
                .into(imagePicView);


        //text för och efternamn
        textNameView.setText(person.get(position).getFullName());
        //text tid
        String panelTime = context.getString(R.string.panel_time);
        textTimwView.setText(checkTimeDistance(person.get(position)) + " " + panelTime);

        //text distans
        int thisUserDistans = person.get(position).getDistansBetween();
        int distansConverted;
        String distansValue;

        String panelDistansM = context.getString(R.string.panel_distans_m);
        String panelDistansKm = context.getString(R.string.panel_distans_km);
        String panelDistansMil = context.getString(R.string.panel_distans_mil);

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

        return rowView;
    }


    // DownloadImage AsyncTask
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imagePicView.setImageBitmap(result);
        }
    }

    public String checkTimeDistance(Person personB){
        double timeDistanceSek = (System.currentTimeMillis() / 1000) - (personB.getLastUpdateLocation() / 1000) ;
        int timeDistanceMin = (int) (timeDistanceSek / 60);
        String timeDistanceString = String.valueOf(timeDistanceMin);
        return timeDistanceString;
    }
}