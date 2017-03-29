package com.emmasoderstrom.caround2;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import static java.security.AccessController.getContext;

/**
 * Created by User on 2017-03-27.
 */

public class FriendList {

    public FriendList(){
        super();
    }

    public void showFriendList(View view){
        RelativeLayout relFriendListLayout = (RelativeLayout)view.findViewById(R.id.frien_list_layout);
    }
}
