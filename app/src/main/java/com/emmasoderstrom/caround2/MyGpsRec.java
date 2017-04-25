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

    @Override
    public void onReceive(Context context, Intent intent){

        if(MainActivity.getInstace() != null){
            MainActivity.getInstace().checkGPSOn();
        }
    }
}