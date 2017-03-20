package com.emmasoderstrom.caround2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.TabHost;
import android.widget.TabWidget;


import java.util.ArrayList;

/**
 * Created by User on 2017-03-10.
 */

public class DialogChangeDistansOneWheel {

    MainActivity mainActivity;
    Person thisUser;
    //String oldChosenDistansText;

    TabHost host;
    NumberPicker wheelNumberDistansM;
    String[] valueNumbersArrayM;
    int chosenNumberM;
    NumberPicker wheelNumberDistansKm;
    String[] valueNumbersArrayKm;
    int chosenNumberKm;
    NumberPicker wheelNumberDistansMil;
    String[] valueNumbersArrayMil;
    String chosenNumberMil;

    int oldChosenDistans;
    String oldChosenDistansText;

    public DialogChangeDistansOneWheel(MainActivity startMainActivity, Person startThisUser){
        super();

        mainActivity = startMainActivity;
        thisUser = startThisUser;
        //oldChosenDistansText = startOldChosenDistansText;
    }

    public void showDialogChangeDistans(Context context) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        //builder1.setMessage(R.string.dia_change_distans_message);
        builder1.setTitle(R.string.dia_change_distans_message);
        builder1.setCancelable(true);

        View diaView = View.inflate(context, R.layout.dialog_change_distans_one_wheel, null);

        //tabs
        host = (TabHost)diaView.findViewById(R.id.tabHost);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Tab One");
        spec.setContent(R.id.tab_m);
        spec.setIndicator("M");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Tab Two");
        spec.setContent(R.id.tab_km);
        spec.setIndicator("Km");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Tab Three");
        spec.setContent(R.id.tab_mil);
        spec.setIndicator("Mil");
        host.addTab(spec);








        //wheel M
        wheelNumberDistansM = (NumberPicker) diaView.findViewById(R.id.wheel_number_distans_m);

        ArrayList<String> valueNumbersM = new ArrayList<>();
        for (int i = 0; i < 9 ; i++) {
            valueNumbersM.add(String.valueOf((i + 1) * 10) + " m");
        }
        for (int i = 0; i < 9 ; i++) {
            valueNumbersM.add(String.valueOf((i + 1) * 100) + " m");
        }
        valueNumbersArrayM = valueNumbersM.toArray(new String[valueNumbersM.size()]);

        wheelNumberDistansM.setMinValue(0);
        wheelNumberDistansM.setMaxValue(valueNumbersM.size()-1);
        //wheelNumberDistans.setWrapSelectorWheel(false);
        wheelNumberDistansM.setDisplayedValues(valueNumbersArrayM);


        //wheel km
        wheelNumberDistansKm = (NumberPicker) diaView.findViewById(R.id.wheel_number_distans_km);

        ArrayList<String> valueNumbersKm = new ArrayList<>();
        for (int i = 0; i < 9 ; i++) {
            valueNumbersKm.add(String.valueOf((i + 1) + 0.0) + " km");
            for (int j = 0; j < 1 ; j++) {
                valueNumbersKm.add(String.valueOf((i + 1) + 0.5) + " km");
            }
        }
        valueNumbersArrayKm = valueNumbersKm.toArray(new String[valueNumbersKm.size()]);

        wheelNumberDistansKm.setMinValue(0);
        wheelNumberDistansKm.setMaxValue(valueNumbersKm.size()-1);
        //wheelNumberDistans.setWrapSelectorWheel(false);
        wheelNumberDistansKm.setDisplayedValues(valueNumbersArrayKm);


        //wheel Mil
        wheelNumberDistansMil = (NumberPicker) diaView.findViewById(R.id.wheel_number_distans_mil);

        ArrayList<String> valueNumbersMil = new ArrayList<>();
        for (int i = 0; i < 9 ; i++) {
            valueNumbersMil.add(String.valueOf(i + 1) + " mil");
        }
        for (int i = 0; i < 10 ; i++) {
            valueNumbersMil.add(String.valueOf((i + 1) * 10) + " mil");
        }
        valueNumbersArrayMil = valueNumbersMil.toArray(new String[valueNumbersMil.size()]);

        wheelNumberDistansMil.setMinValue(0);
        wheelNumberDistansMil.setMaxValue(valueNumbersMil.size()-1);
        //wheelNumberDistans.setWrapSelectorWheel(false);
        wheelNumberDistansMil.setDisplayedValues(valueNumbersArrayMil);




        //sätter den rätta hjulet som vissas för användaren utifrån det nuvarande valda distansen
        oldChosenDistans = thisUser.getChosenDistansInt();
        oldChosenDistansText = (String) mainActivity.chosenDistansText.getText();

        if(oldChosenDistans < 1000){
            host.setCurrentTab(0);

            for (int i = 0; i < valueNumbersArrayM.length; i++) {
                if(valueNumbersArrayM[i].equals(oldChosenDistansText)){
                    wheelNumberDistansM.setValue(i);
                    break;
                }
            }
        }else if(oldChosenDistans < 10000){
            host.setCurrentTab(1);

            for (int i = 0; i < valueNumbersArrayKm.length; i++) {
                if(valueNumbersArrayKm[i].equals(oldChosenDistansText)){
                    wheelNumberDistansKm.setValue(i);
                    break;
                }
            }
        }else{
            host.setCurrentTab(2);

            for (int i = 0; i < valueNumbersArrayMil.length; i++) {
                if(valueNumbersArrayMil[i].equals(oldChosenDistansText)){
                    wheelNumberDistansMil.setValue(i);
                    break;
                }
            }

        }




        //knappar
        builder1.setView(diaView);
        builder1.setPositiveButton(
                "Klar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("tag"," ----------------- ");

                        int chosenTab = host.getCurrentTab();
                        NumberPicker chosenWheel[] ={wheelNumberDistansM, wheelNumberDistansKm, wheelNumberDistansMil};
                        String[] chosenWheelValue[] ={valueNumbersArrayM, valueNumbersArrayKm, valueNumbersArrayMil};

                        //tar fram den valda tagens valda värde i den aktuella hjulet
                        int chosenIndex = chosenWheel[chosenTab].getValue();
                        String chosenDistansString = chosenWheelValue[chosenTab][chosenIndex];
                        String[] splitChosenDistansString = chosenDistansString.split("\\s+");
                        Double chosenNumber = Double.valueOf(splitChosenDistansString[0]);

                        Log.d("tag"," tag chosenNumber " + chosenNumber);


                        int chosenvalueInM;
                        switch (host.getCurrentTab()){
                            case 0:
                                chosenvalueInM = chosenNumber.intValue();
                                Log.d("tag"," tag 1 +  m " + chosenvalueInM);
                                thisUser.setChosenDistans(chosenvalueInM);
                                break;
                            case 1:
                                Double chosenvalueInMTemp = chosenNumber * 1000;
                                chosenvalueInM = chosenvalueInMTemp.intValue();
                                Log.d("tag"," tag 2 + km " + chosenvalueInM);
                                thisUser.setChosenDistans(chosenvalueInM);
                                break;
                            case 2:
                                chosenvalueInM = chosenNumber.intValue() * 10000;
                                Log.d("tag"," tag 3 + mil " + chosenvalueInM);
                                thisUser.setChosenDistans(chosenvalueInM);
                                break;
                        }

                        Log.d("tag", "Dialog Klar");
                        mainActivity.updateChosenDistansText();
                        dialog.cancel();

                    }
                });
        builder1.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("tag", "Dialog cancel");
                        dialog.cancel();
                    }
                });



        AlertDialog alertChoseDistans = builder1.create();
        alertChoseDistans.show();
    }



}
