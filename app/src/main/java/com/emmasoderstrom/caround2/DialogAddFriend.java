package com.emmasoderstrom.caround2;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by User on 2017-03-30.
 */

public class DialogAddFriend{


    Context context;
    private DatabaseReference mDatabase;
    View diaFriendView;
    //FriendHandler friendHandler;
    Person thisUser;

    //ArrayList<String> allDataBasPhoneNum = new ArrayList<String>();

    AlertDialog.Builder builder1;

    ArrayList<String> tempArrayNotDoubleNumber = new ArrayList<String>();
    ArrayList<String> nameFromContacts = new ArrayList<String>();
    PhoneBookContacts adapter;
    ListView diaListView;
    String[] listItems;
    boolean[] checkedItems;
    CheckBox addCheckbox;

    /*String listItems[] = {
            "Jonas"+" "+"Amnesten",
            "Emma"+" "+"Södetersöm",
            "Lisa"+" "+"Svensson",
            "Sara"+" "+"Johansson",

    };*/



    ArrayList<String> testName = new ArrayList<String>();


    public DialogAddFriend(Context context, Person startThisUser){
        super();

        this.context = context;
        thisUser = startThisUser;

    }

    public void showDialogAddFriend(Context sContext) {

        context = sContext;

        mDatabase = FirebaseDatabase.getInstance().getReference();



        builder1 = new AlertDialog.Builder(context);
        builder1.setTitle(R.string.friend_dia_add_friend);
        builder1.setCancelable(true);

        View diaView = View.inflate(context, R.layout.dialog_add_friend, null);

        //getAllDatabasNumber(diaView);

        //rrayList<String>  mStringList= new ArrayList<String>();;
        //listItems = new String[nameFromContacts.size()];




        testName.add("Emma");
        testName.add("Joans");
        testName.add("Ulla");
        testName.add("Åke");

        //casta om araylist till array
        //String[] testNameArray = new String[testName.size()];
        //listItems = testNameArray;

        //checkedItems = new boolean[listItems.length];

        /*adapter = new PhoneBookContacts(this.context, testName);

        // Attach the adapter to a diaListView
        diaListView = (ListView) diaView.findViewById(R.id.list_add_friends);
        diaListView.setAdapter(adapter);*/

        //builder1.setView(diaView);

        listItems = context.getResources().getStringArray(R.array.shopping_item);
        checkedItems = new boolean[listItems.length];

        builder1.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {

                Log.d("tag", "Dialog klick på vänner");
//                        if (isChecked) {
//                            if (!mUserItems.contains(position)) {
//                                mUserItems.add(position);
//                            }
//                        } else if (mUserItems.contains(position)) {
//                            mUserItems.remove(position);
//                        }
                /*if(isChecked){
                    mUserItems.add(position);
                }else{
                    mUserItems.remove((Integer.valueOf(position)));
                }*/
            }
        });




        builder1.setPositiveButton(
                "Klar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("tag", "Dialog Klar");
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


        builder1.setNeutralButton("Rensa val", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                Log.d("tag", "Rensa val");
                /*for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = false;
                    mUserItems.clear();
                    mItemSelected.setText("");
                }*/
            }
        });


        AlertDialog alertAddFriends = builder1.create();
        alertAddFriends.show();
    }






//
//
//
//
//    public void getAllDatabasNumber(final View diaView){
//
//        final ArrayList<String> allDataBasPhoneNum = new ArrayList<String>();
//
//        //endast en gång, sätter användarens starvärden.
//        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                Log.d("tag", "_____-----------MMMMMMMMMMMM----------->>>>>>>>>>>>>>>>>>");
//
//                for (DataSnapshot snap : dataSnapshot.child("users").getChildren()) {
//                    Person personB = snap.getValue(Person.class);
//                    allDataBasPhoneNum.add(personB.getPhoneNumber());
//                }
//
//                setUpList(diaView, allDataBasPhoneNum);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//    }
//
//    public void setUpList(View diaView, ArrayList<String> allDataBasPhoneNum){
//
//        Cursor contacts = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
//        String aNameFromContacts[] = new String[contacts.getCount()];
//        String aNumberFromContacts[] = new String[contacts.getCount()];
//
//
//        int i = 0;
//
//        //int nameFieldColumnIndex = contacts.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
//        //int numberFieldColumnIndex = contacts.getColumnIndex(ContactsContract.PhoneLookup.NUMBER);
//
//        int name = contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
//        int number = contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//
//        Log.d("tag", "setFriendList: allDataBasPhoneNum " + allDataBasPhoneNum);
//
//        while(contacts.moveToNext()) {
//
//            String contactName = contacts.getString(name);
//            String contactNumber = contacts.getString(number);
//            Log.d("tag", "setFriendList: allDataBasPhoneNum " + allDataBasPhoneNum);
//            Log.d("tag", "setFriendList: contactNumber " + contactName + "   " + contactNumber);
//
//            if(allDataBasPhoneNum.contains(contactNumber) && !tempArrayNotDoubleNumber.contains(contactNumber)) {
//                Log.d("tag", "Tjuttt tjuuuuuuuttttttttttt--------->>>>>" + tempArrayNotDoubleNumber);
//                tempArrayNotDoubleNumber.add(contactNumber);
//                nameFromContacts.add(contactName);
//            }
//
//            i++;
//        }
//
//        contacts.close();
//
//
//        // Construct the data source// Create the adapter to convert the array to views
//        adapter = new PhoneBookContacts(this.context, nameFromContacts);
//
//        // Attach the adapter to a diaListView
//        diaListView = (ListView) diaView.findViewById(R.id.list_add_friends);
//        diaListView.setAdapter(adapter);
//
//
//
//        /*diaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.d("tag", " click på en vän");
//
//            }
//
//
//        });*/
//
//
//        /*builder1.setAdapter(adapter, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Log.d("tag", " click på en vän");
//                /*String strName = adapter.getItem(which);
//                AlertDialog.Builder builderInner = new AlertDialog.Builder(DialogActivity.this);
//                builderInner.setMessage(strName);
//                builderInner.setTitle("Your Selected Item is");
//                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog,int which) {
//                        dialog.dismiss();
//                    }
//                });
//                builderInner.show();*/
//           /* }
//        });*/
//
//
//
//
//
//    }
//
//    public void test(View view){
//        Log.d("tag", "test: cliclcilckcilciclcilciclc");
//    }




}
