package com.emmasoderstrom.caround2;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;

import java.util.ArrayList;

/**
 * Created by User on 2017-03-10.
 */

public class DialogChangeDistans {

    public DialogChangeDistans(){
        super();
    }

    public void showDialogChangeDistans(Context context) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        //builder1.setMessage(R.string.dia_change_distans_message);
        builder1.setTitle(R.string.dia_change_distans_message);
        builder1.setCancelable(true);

        View diaView = View.inflate(context, R.layout.dialog_change_distans, null);

        NumberPicker wheelNumberDistans = (NumberPicker) diaView.findViewById(R.id.wheel_number_distans);

        ArrayList<String> valueNumbers = new ArrayList<>();
        for (int i = 0; i < 10 ; i++) {
            valueNumbers.add(String.valueOf(i * 10));
            Log.d("tag", "firstten " + i * 10);
        }
        for (int i = 0; i < 100 ; i++) {
            valueNumbers.add(String.valueOf((i + 1) * 100));
            Log.d("tag", "firstten " + (i + 1) * 100);
        }

        String[] valueNumbersArray = valueNumbers.toArray(new String[valueNumbers.size()]);

        wheelNumberDistans.setMinValue(0);
        wheelNumberDistans.setMaxValue(valueNumbers.size()-1);
        //wheelNumberDistans.setWrapSelectorWheel(false);
        wheelNumberDistans.setDisplayedValues(valueNumbersArray);

        NumberPicker wheelValueDistans = (NumberPicker) diaView.findViewById(R.id.wheel_value_distans);
        final String[] valuesDistans= {"M","Km","Mil"};
        wheelValueDistans.setMinValue(0);
        wheelValueDistans.setMaxValue(valuesDistans.length-1);
        wheelValueDistans.setDisplayedValues(valuesDistans);

        builder1.setView(diaView);

        builder1.setPositiveButton(
                "Klar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

}
