package com.emmasoderstrom.caround2;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

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

    public FriendHandler(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_handler);

        thisUserID = MainActivity.thisUserID;
        Log.d("tag", "onCreate: thisUserID " + thisUserID);



        setToolbar();

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

        dialogAddFriend = new DialogAddFriend(this, thisUser);


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

        //Intent[] picList;
        ArrayList<Integer> picList = new ArrayList<Integer>();

        if(thisUser.friendAllowed != null) {
            for (String personB : thisUser.friendAllowed) {
                //picList.add(personB.getPic());

            }
        }

        //List<String> names = new ArrayList<String>();
        Integer[] picListArray = picList.toArray(new Integer[picList.size()]);



        if (adapter == null) {
            Log.d("tag", "createList adapter NULL");
            adapter = new FriendListContiner(this, picListArray, thisUser.friendAllowed);

            listView = (ListView) findViewById(R.id.list_friends);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("tag", " click på en vän");

                    //dialogViewListFriend.showDialogViewListFriend(getWindow().getContext());

                }
            });

        }
    }

    public void addFriendRequest(View view){
        Log.d("tag", "addFriendRequest: ");
        dialogAddFriend.showDialogAddFriend(this);
    }

}
