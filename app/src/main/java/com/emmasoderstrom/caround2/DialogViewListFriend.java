package com.emmasoderstrom.caround2;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;

/**
 * Created by User on 2017-03-27.
 */

public class DialogViewListFriend {


    public DialogViewListFriend(){
        super();
    }

    public void showDialogViewListFriend(Context context){


        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle(R.string.dia_friend_list_message);
        builder1.setCancelable(true);
        View diaView = View.inflate(context, R.layout.dialog_view_list_friend, null);

        builder1.setView(diaView);
        AlertDialog alertChoseDistans = builder1.create();
        alertChoseDistans.show();
    }
}
