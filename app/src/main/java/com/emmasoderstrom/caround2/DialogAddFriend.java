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
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by User on 2017-03-30.
 */

public class DialogAddFriend{


    Context context;
    View diaFriendView;
    //FriendHandler friendHandler;
    Person thisUser;

    ArrayList<String> nameFromContacts = new ArrayList<String>();
    FriendListContiner adapter;
    ListView listView = null;

    private DatabaseReference mDatabase;

    public DialogAddFriend(Context context, Person startThisUser){
        super();

        this.context = context;
        thisUser = startThisUser;

    }

    public void showDialogAddFriend(Context sContext) {

        context = sContext;


        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle(R.string.friend_dia_add_friend);
        builder1.setCancelable(true);

        View diaView = View.inflate(context, R.layout.dialog_add_friend, null);

        setFriendList(diaView);



        builder1.setView(diaView);
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


        AlertDialog alertChoseDistans = builder1.create();
        alertChoseDistans.show();
    }

    public void setFriendList(View view){

        Cursor contacts = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        String aNameFromContacts[] = new String[contacts.getCount()];
        String aNumberFromContacts[] = new String[contacts.getCount()];



        int i = 0;

        //int nameFieldColumnIndex = contacts.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
        //int numberFieldColumnIndex = contacts.getColumnIndex(ContactsContract.PhoneLookup.NUMBER);

        //String name = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
        //int number = contacts.getInt(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        int name = contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int number = contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

        while(contacts.moveToNext()) {

            String contactName = contacts.getString(name);
            //aNameFromContacts[i] =    contactName ;
            nameFromContacts.add(contactName);

            //String number = contacts.getString(numberFieldColumnIndex);
            //aNumberFromContacts[i] =    number ;
            i++;
        }

        contacts.close();


        Log.d("tag", "setFriendList: nameFromContacts " + nameFromContacts.size());

        // Construct the data source// Create the adapter to convert the array to views
        PhoneBookContacts adapter = new PhoneBookContacts(this.context, nameFromContacts);

        Log.d("tag", "setFriendList: adapter " + adapter);

        // Attach the adapter to a ListView
        ListView listView = (ListView) view.findViewById(R.id.list_add_friends);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("tag", " click på en kontakt");

            }
        });




        /*ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.add_friend_list_item, aNameFromContacts);

        ListView listView = (ListView) friendHandler.findViewById(R.id.list_add_friends);

        listView.setAdapter(itemsAdapter);*/


        //Intent[] picList;
        /*ArrayList<Integer> picList = new ArrayList<Integer>();

        if(thisUser.friendAllowed != null) {
            for (Person personB : thisUser.friendAllowed) {
                picList.add(personB.getPic());

            }
        }*/

        //List<String> names = new ArrayList<String>();
        //Integer[] picListArray = picList.toArray(new Integer[picList.size()]);



        /*if (adapter == null) {
            Log.d("tag", "createList adapter NULL");
            adapter = new FriendListContiner(friendHandler, picListArray, thisUser.friendAllowed);

            listView = (ListView) friendHandler.findViewById(R.id.list_add_friends);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("tag", " click på en vän");

                    //dialogViewListFriend.showDialogViewListFriend(getWindow().getContext());

                }
            });

        }*/
    }


}
