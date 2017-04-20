package com.emmasoderstrom.caround2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by User on 2017-04-20.
 */

public class MyGpsRec extends BroadcastReceiver {

    private static boolean firstConnect = true;

    @Override
    public void onReceive(Context context, Intent intent){

        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null) {
            if(firstConnect) {
                // do subroutines here
                Log.d("tag", "onReceive: OOOO222222OOOOOO>");
                //Toast.makeText(context, "First: gps ändras " + intent.getAction(), Toast.LENGTH_SHORT).show();
                MainActivity.getInstace().checkGPSOn();
                firstConnect = false;
            }
        }
        else {
            firstConnect= true;
        }


    }
}