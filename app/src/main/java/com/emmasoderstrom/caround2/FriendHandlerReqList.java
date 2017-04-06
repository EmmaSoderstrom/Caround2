package com.emmasoderstrom.caround2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

import static android.view.View.VISIBLE;


public class FriendHandlerReqList extends ArrayAdapter<Person> {

    private final Activity context;
    private ArrayList<Person> person;

    View rowView;

    ImageView imagePicView;
    Bitmap myBitmap;
    Layout itemLayout;


    public FriendHandlerReqList(Activity context, ArrayList<Person> startPerson) {
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




        TabHost host = (TabHost)context.findViewById(R.id.tabHost);
        Log.d("tag", "host: " + host.getCurrentTab());

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = (View) inflater.inflate(R.layout.friend_handler_req_list_item, null,true);
        } else {
            rowView = (View) convertView;
        }

        imagePicView = (ImageView) rowView.findViewById(R.id.list_pic);
        TextView textNameView = (TextView) rowView.findViewById(R.id.list_name);
        TextView textIdView = (TextView) rowView.findViewById(R.id.list_id);



        //bild
        String userPicS = person.get(position).getPicString();
        new DownloadImage().execute(userPicS);


        //imagePicView.setImageDrawable(placeHolderImage);
        //imagePicView.setTag(userPicS);
        //new DownloadImageTask().execute(imagePicView);

        textNameView.setText(person.get(position).getFullName());
        textIdView.setText(person.get(position).getPersonId());

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

    /*private class DownloadImageTask extends AsyncTask<ImageView,Void, Bitmap> {
        private ImageView imageView;


        @Override
        protected Bitmap doInBackground(ImageView... params) {

            URL imageURL = (URL) imv.getTag();
            imageView = imv;
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream((InputStream)(imageURL.getContent()));
            } catch (MalformedURLException e) {
                // Invalid URL
            } catch (IOException e) {
                //Problem with connecting internet, or resource is not available
            }
            return bitmap;

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap){
            imageView.setImageBitmap(bitmap);
        }

    }*/

}