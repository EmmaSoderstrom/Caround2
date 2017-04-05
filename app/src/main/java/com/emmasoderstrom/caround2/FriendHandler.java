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
import android.widget.ListView;
import android.widget.TabHost;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendHandler extends AppCompatActivity {


    static String thisUserID;

    Toolbar toolbar;
    TabHost host;
    DialogAddFriend dialogAddFriend;
    Context context;
    FriendListContiner adapter;
    ListView listView = null;

    private DatabaseReference mDatabase;
    Person thisUser;

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
        spec.setContent(R.id.tab_friend);
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
                Log.d("tag", "This  user " + thisUser.getFullName());
                setFriendList();

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

    public void setFriendList(){

        ArrayList<Integer> picList = new ArrayList<Integer>();

        if(thisUser.friendAllowed != null) {
            for (String personB : thisUser.friendAllowed) {
                //picList.add(personB.getPic());

            }
        }

        if (adapter == null) {
            Log.d("tag", "createList adapter NULL");
            adapter = new FriendListContiner(this, thisUser.friendAllowed);

            listView = (ListView) findViewById(R.id.list_friends);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("tag", " click på en vän");

                }
            });
        }
    }

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
