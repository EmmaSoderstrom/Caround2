package com.emmasoderstrom.caround2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by User on 2017-04-25.
 */

public class MyInternetRec extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){

        if(MainActivity.getInstace() != null){
            Log.d("tag", "onReceive: MyInternetRec");
            //MainActivity.getInstace().noInternetRec();
        }
    }
}