package com.emmasoderstrom.caround2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by User on 2017-03-30.
 */

public class DialogAddFriend{


    static String thisUserID;


    Context context;
    private DatabaseReference mDatabase;
    Person thisUser;

    AlertDialog.Builder builder1;

    ArrayList<String> tempArrayNotDoubleNumber = new ArrayList<String>();
    ArrayList<String> nameFromContacts = new ArrayList<String>();
    ArrayList<String> phoneNumberFromContacts = new ArrayList<String>();
    ArrayList<String> checktContacktsPhoneNumber = new ArrayList<String>();

    String[] listItems;
    boolean[] checkedItems;


    public DialogAddFriend(Context context, Person startThisUser){
        super();

        this.context = context;
        thisUser = startThisUser;

        thisUserID = MainActivity.thisUserID;


    }

    public void showDialogAddFriend(Context context) {


        this.context = context;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        View diaView = View.inflate(context, R.layout.dialog_add_friend, null);
        getAllDatabasNumber();

    }


    public void getAllDatabasNumber(){

        final ArrayList<String> allDataBasPhoneNum = new ArrayList<String>();

        //endast en gång, sätter användarens starvärden.
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("tag", "_____-----------MMMMMMMMMMMM----------->>>>>>>>>>>>>>>>>>");

                for (DataSnapshot snap : dataSnapshot.child("users").getChildren()) {
                    Person personB = snap.getValue(Person.class);
                    allDataBasPhoneNum.add(personB.getPhoneNumber());

                    if(personB.getPersonId().equals(thisUserID)){
                        thisUser = personB;
                    }
                }

                setUpArray(allDataBasPhoneNum);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void setUpArray(ArrayList<String> allDataBasPhoneNum){

        Cursor contacts = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        String aNameFromContacts[] = new String[contacts.getCount()];
        String aNumberFromContacts[] = new String[contacts.getCount()];

        //int nameFieldColumnIndex = contacts.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
        //int numberFieldColumnIndex = contacts.getColumnIndex(ContactsContract.PhoneLookup.NUMBER);

        int name = contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int number = contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

        Log.d("tag", "setFriendList: allDataBasPhoneNum " + allDataBasPhoneNum);

        int i = 0;
        while(contacts.moveToNext()) {

            String contactName = contacts.getString(name);
            String contactNumber = contacts.getString(number);

            if(contactNumber.length() >= 10){

                if(contactNumber.length() == 10){
                    addContactToList(allDataBasPhoneNum, contactNumber, contactName);
                }else{
                    String contactNumberTakeThree = contactNumber.substring(0,3);
                    String contactNumberTakeLast9 = contactNumber.substring(contactNumber.length() - 9, contactNumber.length());

                    if(contactNumberTakeThree.equals("+46")){
                        addContactToList(allDataBasPhoneNum, "0" + contactNumberTakeLast9, contactName);
                    }
                }



            }


            i++;
        }

        contacts.close();
        setUpList();
    }

    public void addContactToList(ArrayList<String> allDataBasPhoneNum, String contactNumber, String contactName){

        if((allDataBasPhoneNum.contains(contactNumber))
                && !tempArrayNotDoubleNumber.contains(contactNumber)
                && !thisUser.friendSendedRequests.contains(contactNumber)
                && !thisUser.getPhoneNumber().equals(contactNumber)) {
            Log.d("tag", "Tjuttt tjuuuuuuuttttttttttt--------->>>>>" + tempArrayNotDoubleNumber);
            tempArrayNotDoubleNumber.add(contactNumber);
            nameFromContacts.add(contactName);
            phoneNumberFromContacts.add(contactNumber);
        }
    }


    public void setUpList(){

        builder1 = new AlertDialog.Builder(context);
        builder1.setTitle(R.string.friend_dia_add_friend);
        builder1.setCancelable(false);

        //Arraylist till array
        String nameFromContactsArray2[] = new String[nameFromContacts.size()];
        for (int i = 0; i < nameFromContacts.size() ; i++) {
            nameFromContactsArray2[i] = nameFromContacts.get(i);
        }

        listItems = nameFromContactsArray2;
        checkedItems = new boolean[listItems.length];

        builder1.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position, boolean isChecked) {

                Log.d("tag", "Dialog klick på vänner");

                if(isChecked){
                    Log.d("tag", "Dialog klick på vänner checkt" + position);
                    checktContacktsPhoneNumber.add(phoneNumberFromContacts.get(position));
                }else{
                    Log.d("tag", "Dialog klick på vänner Not checkt" + position);
                    if(checktContacktsPhoneNumber.contains(phoneNumberFromContacts.get(position))){
                        checktContacktsPhoneNumber.remove(checktContacktsPhoneNumber.indexOf(phoneNumberFromContacts.get(position)));
                    }
                }

                Log.d("tag", "Dialog klick på vänner färdig checka" + checktContacktsPhoneNumber);
            }
        });

        builder1.setPositiveButton(
                "Klar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("tag", "Dialog Klar");

                        sendFriendRequest();
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
            public void onClick(DialogInterface dialog, int which) {
                Log.d("tag", "Rensa val");
                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = false;
                    //mUserItems.clear();
                    //mItemSelected.setText("");
                }

            }
        });


        AlertDialog alertAddFriends = builder1.create();
        alertAddFriends.show();
    }

    /*
    *sätter mitt namn, tel och id hos dem jag försrågars, deras requeat arraylist
     */
    public void sendFriendRequest(){


        //endast en gång, sätter användarens starvärden.
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("tag", "_____-----------PPPPPPPPPPPP----------->>>>>>>>>>>>>>>>>>");

                for (DataSnapshot snap : dataSnapshot.child("users").getChildren()) {
                    Person personB = snap.getValue(Person.class);

                    if(checktContacktsPhoneNumber.contains(personB.getPhoneNumber())){
                        //lägger till request hos andra part
                        personB.addFriendRequests(thisUserID);
                        mDatabase.child("users").child(personB.getPersonId()).child("friendRequestsId").setValue(personB.getFriendRequestsId());
                        //lägger till notering att det är sent request så det inte kan göras igen, hos båda parter
                        thisUser.addFriendSendedRequests(personB.getPhoneNumber());
                        mDatabase.child("users").child(thisUserID).child("friendSendedRequests").setValue(thisUser.getFriendSendedRequests());

                        personB.addFriendSendedRequests(thisUser.getPhoneNumber());
                        mDatabase.child("users").child(personB.getPersonId()).child("friendSendedRequests").setValue(personB.getFriendSendedRequests());
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}
