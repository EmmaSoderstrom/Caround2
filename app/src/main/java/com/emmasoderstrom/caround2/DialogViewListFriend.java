package com.emmasoderstrom.caround2;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by User on 2017-03-27.
 */

public class DialogViewListFriend {

    final static int MY_PERMISSIONS_REQUEST_CALL_PHONE = 11;

    Context context;
    MainActivity mainActivity;


    public DialogViewListFriend() {
        super();
    }

    public void showDialogViewListFriend(final Context context, final Person person) {

        this.context = context;

        final AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setCancelable(true);
        //builder1.setTitle(R.string.dilog_header);
        View diaView = View.inflate(context, R.layout.dialog_view_list_friend, null);

        ImageButton closeCross = (ImageButton) diaView.findViewById(R.id.dialog_close_cross);
        ImageButton sendMessage = (ImageButton) diaView.findViewById(R.id.dialog_send_Massage);
        ImageButton makeCall = (ImageButton) diaView.findViewById(R.id.dialog_make_call);

        TextView dialogFirstName = (TextView) diaView.findViewById(R.id.dialog_friend_first_name);
        TextView dialogLastName = (TextView) diaView.findViewById(R.id.dialog_friend_last_name);
        TextView dialogdistans = (TextView) diaView.findViewById(R.id.dialog_friend_distans);
        TextView dialogdistansSubtext = (TextView) diaView.findViewById(R.id.dialog_friend_distans_subtext);


        String firstName = person.getFirstName();
        String lastName = person.getLastName();
        int distans = person.getDistansBetween();
        String distansString = ((MainActivity) context).updateChosenDistansText(distans);


        dialogFirstName.setText(firstName);
        dialogLastName.setText(lastName);
        dialogdistans.setText(distansString);
        dialogdistansSubtext.setText(firstName + " " + context.getString(R.string.dilog_distans_subtext_part1)
                + " " + distansString + " " + context.getString(R.string.dilog_distans_subtext_part2));


        builder1.setView(diaView);
        final AlertDialog alertChoseDistans = builder1.create();


        closeCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertChoseDistans.cancel();
            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                // TODO: 2017-04-11 om tid finns lägg till meddelande i sms
                //smsIntent.putExtra("sms_body","Body of Message");
                smsIntent.setData(Uri.parse("sms:" + person.getPhoneNumber()));

                context.startActivity(smsIntent);
                alertChoseDistans.cancel();
            }
        });

        makeCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean permissionCall = checkPermissonCall();

                if (permissionCall) {

                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + person.getPhoneNumber()));

                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(((MainActivity) context),
                                new String[]{Manifest.permission.CALL_PHONE},
                                MY_PERMISSIONS_REQUEST_CALL_PHONE);

                        return;
                    }else{
                        context.startActivity(callIntent);
                        alertChoseDistans.cancel();
                    }

                }
            }
        });

        alertChoseDistans.show();



    }

    public boolean checkPermissonCall() {
        //Kollar permission att gör ett telefonsamtal
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED
                /*&& ActivityCompat.checkSelfPermission(context, android.Manifest.permission.C)
                != PackageManager.PERMISSION_GRANTED*/) {

            Log.d("tag", "onConnected permission int ok");

            return false;
        } else {
            Log.d("tag", "onConnected permission ok");
            return true;
        }

    }
}
