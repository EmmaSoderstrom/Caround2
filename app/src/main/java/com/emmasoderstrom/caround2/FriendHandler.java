package com.emmasoderstrom.caround2;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.android.gms.location.LocationListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendHandler extends AppCompatActivity {


    static String thisUserID;

    Toolbar toolbar;
    TabHost host;
    DialogAddFriend dialogAddFriend;
    Context context;
    FriendHandlerList adapterReq;
    FriendHandlerList adapterAllow;
    ListView listViewReq = null;
    ListView listViewAllow = null;

    private DatabaseReference mDatabase;
    Person thisUser;

    ArrayList<Person> friendRequestsPersonList = new ArrayList<Person>();
    ArrayList<Person> friendAllowedPersonList = new ArrayList<Person>();

    final static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 11;


    public FriendHandler(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_handler);

        thisUserID = MainActivity.thisUserID;
        Log.d("tag", "onCreate: thisUserID " + thisUserID);

        setToolbar();

        dialogAddFriend = new DialogAddFriend(this, thisUser);

        host = (TabHost)findViewById(R.id.tabHost);
        host.setup();

        //TabWidget tabWidget = (TabWidget)findViewById(R.id.tabs);
//        final TextView tv = (TextView) tabWidget.getChildAt(0).findViewById(android.R.id.tabs);
//        tv.setTextColor(this.getResources().getColorStateList(R.color.text_tab_indicator));

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Tab One");
        spec.setContent(R.id.tab_friend_allowed);
        String tabFriend = getString(R.string.friend_tab_fri);
        spec.setIndicator(tabFriend);
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Tab Two");
        spec.setContent(R.id.tab_friend_request);
        spec.setIndicator(getString(R.string.friend_tab_friReq));
        host.addTab(spec);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        //endast en gång, sätter användarens starvärden.
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("tag", "<----------------spelare null Hämta spelare------------------>>>>>>>>>>");
                thisUser = dataSnapshot.child("users")
                        .child(thisUserID)
                        .getValue(Person.class);
                Log.d("tag", "This  user " + thisUser.getFriendRequestsId());


                //Skapar en list med personer utav thisUsers lista på allowed och requeset friends list, som innehåller id.
                for (String personBId : thisUser.getFriendRequestsId()) {
                    Log.d("tag", "personBId " + personBId);
                    Person personB = dataSnapshot.child("users")
                            .child(personBId)
                            .getValue(Person.class);

                    friendRequestsPersonList.add(personB);
                }

                Log.d("tag", "friendRequestsPersonList " + friendRequestsPersonList);

                for (String personBId : thisUser.getFriendAllowed()) {
                    Person personB = dataSnapshot.child("users")
                            .child(personBId)
                            .getValue(Person.class);

                    friendAllowedPersonList.add(personB);
                }

                setAllowedFriendList();
                setRequestFriendList();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }







    /**
     * Sätter toolbar
     */
    public void setToolbar(){
        toolbar =(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Vänner");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }






    /*
    *Lista med godkända vänner
     */

    public void setRequestFriendList(){
        if (adapterReq == null) {
            Log.d("tag", "setRequestFriendList adapter NULL");
            adapterReq = new FriendHandlerList(this, friendRequestsPersonList);

            listViewReq = (ListView) findViewById(R.id.list_request_friends);
            listViewReq.setAdapter(adapterReq);

            CheckBox listCheckbox = (CheckBox)listViewReq.findViewById(R.id.list_checkbox);

            /*listViewReq.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onClick(View v) {
                    if(listCheckbox.isChecked()){
                        Log.d("tag", "Checked");
                    }else{
                        Log.d("tag", "Un-Checked");
                    }
                }
            });*/


            /*listViewReq.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("tag", " click på en vän");

                }
            });*/
        }else{
            Log.d("tag", "createList notifyDataSetChanged");
            adapterReq.notifyDataSetChanged();
            //updatedData(adapter);
        }
    }

    public void listCheckbox(View view){

        //view är checkboxen
        CheckBox checkBox = (CheckBox)view;
        RelativeLayout relLayout = (RelativeLayout)view.getParent();
        //textview (personIdFromView) med id är invisible och finns bara för att lagra id så man kan nå det via view
        TextView personIdFromView = (TextView)relLayout.findViewById(R.id.list_id);
        String viewPersonId = (String)personIdFromView.getText();


        if(checkBox.isChecked()){
            Log.d("tag", "listCheckbox: i klickad " + thisUser.getFriendRequestsId());

            for (int i = 0; i < friendRequestsPersonList.size(); i++) {
                if(friendRequestsPersonList.get(i).getPersonId().equals(viewPersonId)){

                    //lägger till i allowlistan hos båda användarna och tar bort från request listan
                    addToAllowedLists(i);

                    //mDatabase.child("users").child(friendRequestsPersonList.get(i).getPersonId()).child("friendAllowed").setValue(thisUserID);

                    friendRequestsPersonList.remove(i);

                    //ny array till databasen, med de värden som är kvar
                    ArrayList<String> tempNyReqList = new ArrayList<String>();
                    for (Person personReq : friendRequestsPersonList) {
                        String personId = personReq.getPersonId();
                        tempNyReqList.add(personId);
                    }
                    mDatabase.child("users").child(thisUserID).child("friendRequestsId").setValue(tempNyReqList);

                    setRequestFriendList();

                    break;
                }
            }
        }
    }

    public void addToAllowedLists(final int i){

        thisUser.addFriendAllowed(friendRequestsPersonList.get(i).getPersonId());
        mDatabase.child("users").child(thisUserID).child("friendAllowed").setValue(thisUser.getFriendAllowed());

        friendRequestsPersonList.get(i).addFriendAllowed(thisUserID);
        mDatabase.child("users").child(friendRequestsPersonList.get(i).getPersonId()).child("friendAllowed").setValue(friendRequestsPersonList.get(i).getFriendAllowed());



        //endast en gång, sätter användarens starvärden.
        /*mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snap : dataSnapshot.child("users").getChildren()) {
                    Person personB = snap.getValue(Person.class);

                    if(friendRequestsPersonList.get(i).getPersonId().equals(personB.getPersonId())){
                        //lägger till hos andra part
                        personB.addFriendAllowed(thisUserID);
                        mDatabase.child("users").child(personB.getPersonId()).child("friendAllowed").setValue(personB.getFriendAllowed());
                        //lägger till hos denna användare
                        thisUser.addFriendAllowed(personB.getPersonId());
                        mDatabase.child("users").child(thisUserID).child("friendAllowed").setValue(thisUser.getFriendAllowed());
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }

    public void setAllowedFriendList(){


        if (adapterAllow == null) {
            Log.d("tag", "setallowedFriendList adapter NULL");
            adapterAllow = new FriendHandlerList(this, friendAllowedPersonList);

            listViewAllow = (ListView) findViewById(R.id.list_allowed_friends);
            listViewAllow.setAdapter(adapterAllow);


            listViewAllow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("tag", " click på en vän");

                }
            });
        }
    }





    /*
    *Knapp add friend
     */
    public void addFriendRequest(View view){

        //kollar permission för kontakater

        // Assume thisActivity is the current activity
        /*int permissionCheck = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_CONTACTS);*/

        //Kollar permission att använda kontakter
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_CONTACTS)) {

                Log.d("tag", "checkSelfPermission: finns inte permission för contakter");

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            } else {

                Log.d("tag", "checkSelfPermission: 1else");
                // No explanation needed, we can request the permission.

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.


            }
        }else{
            Log.d("tag", "addFriendRequest: ");
            dialogAddFriend.showDialogAddFriend(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission godkänndes visa dialog add friends.

                    Log.d("tag", "addFriendRequest: ");
                    dialogAddFriend.showDialogAddFriend(this);

                } else {
                    Log.d("tag", "onRequestPermissionsResult: Permission nekas");
                    //Permission nekades, gör inget.
                }
                return;
            }
        }
    }

}
