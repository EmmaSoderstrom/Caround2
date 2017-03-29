package com.emmasoderstrom.caround2;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.maps.android.SphericalUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener  {

    Intent intent;
    private DatabaseReference mDatabase;
    Person thisUser;

    Toolbar toolbar;
    DialogChangeDistansOneWheel dialogChangeDistans;
    //FriendList friendList;
    //FriendHandler friendHandler;
    DialogViewListFriend dialogViewListFriend;

    final static int MY_PERMISSION_ACCESS_COURSE_LOCATION = 11;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    TextView numberOfFrieansText;
    String panelNumberOfFriends;
    int chosenDistansInt;
    TextView chosenDistansText;
    String panelDistansM;
    String panelDistansKm;
    String panelDistansMil;

    PersonList personList;
    ListContiner adapter;
    ListView listView = null;
    String thisPersonPhoneId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String message = intent.getStringExtra(Login.EXTRA_MESSAGE);
        thisPersonPhoneId = message;
        Log.d("tag", "onCreate: " + message);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mapApiCreat();

        setToolbar();
        dialogChangeDistans = new DialogChangeDistansOneWheel(this, thisUser);
        //friendList = new FriendList();
        //friendHandler = new FriendHandler();
        dialogViewListFriend = new DialogViewListFriend();

        chosenDistansText = (TextView)findViewById(R.id.text_distans);
        panelDistansM = getString(R.string.panel_distans_m);
        panelDistansKm = getString(R.string.panel_distans_km);
        panelDistansMil = getString(R.string.panel_distans_mil);
        numberOfFrieansText = (TextView)findViewById(R.id.text_number_of_friends);
        panelNumberOfFriends = getString(R.string.panel_number_of_friends);

        //thisPersonPhoneId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        setThisUser();

        personList = new PersonList(this, 0);
        creatFakeUser();

    }

    public void setThisUser(){
        Log.d("tag", "setThisUser");

        //endast en gång, sätter användarens starvärden.
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                /*if(thisUser != null) {
                    Log.d("tag", "<----------------spelare INTE null------------------>>>>>>>>>>");
                    chosenDistansInt = thisUser.getChosenDistansInt();
                    updateChosenDistansText(chosenDistansInt);
                }else{*/
                    Log.d("tag", "<----------------spelare null Hämta spelare------------------>>>>>>>>>>");

                    thisUser = dataSnapshot.child("users")
                            .child(thisPersonPhoneId)
                            .getValue(Person.class);
                    chosenDistansInt = thisUser.getChosenDistansInt();
                    updateChosenDistansText(chosenDistansInt);
                    Log.d("tag", "This  user " + thisUser.getFullName());
                //}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //sker när något ändras på denna användarens databas värden
        mDatabase.child("users").child(thisPersonPhoneId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("tag", "<----------------2222222222------------------>>>>>>>>>>");
                thisUser = dataSnapshot.child("users").child(thisPersonPhoneId).getValue(Person.class);

                updateListOfClosePerson();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Skapar personer och skapar en arraylist med alla vänner
     */
    public void creatFakeUser(){
        Log.d("tag", "creatFakeUser ");

        writeNewUser("id1", "Jonas", "Amnesten", 10000, true, 59.31803730000001, 18.38822559999994);
        writeNewUser("id2", "Mamma", "Pappa", 10500, true, 59.35460399999999, 18.282052499999963);
        writeNewUser("id3", "Slussen", "Slussen", 1000, true, 59.31951240000001, 18.07214060000001);
        writeNewUser("id4", "Karolinska", "Instutet", 2000, true, 59.34814839999999, 18.023657800000024);
        writeNewUser("id5", "EmmaNot", "Söderström", 1550, true, 59.31803730000001, 18.38822559999994);
        writeNewUser("id6", "JonasNot", "Amnesten", 10000, true, 59.31803730000001, 18.38822559999994);
        writeNewUser("id7", "MammaNot", "Pappa", 10500, true, 59.35460399999999, 18.282052499999963);
        writeNewUser("id8", "SlussenNot", "Slussen", 1000, true, 59.31951240000001, 18.07214060000001);
        writeNewUser("id9", "KarolinskaNot", "Instutet", 2000, true, 59.34814839999999, 18.023657800000024);
        writeNewUser("id10", "EmmaNot", "Söderström", 1550, true, 59.31803730000001, 18.38822559999994);

    }

    private void writeNewUser(String startPersonId, String startFirstName, String startLastName, int startChosenDistans, boolean startSignedIn,
                              double startLocationLatitude, double startLocationLongitude) {
        Person personClass = new Person(startPersonId, startFirstName, startLastName, startChosenDistans, startSignedIn,
                startLocationLatitude, startLocationLongitude);

        personList.allPersonArrayList.add(personClass);
        mDatabase.child("users").child(startFirstName).setValue(personClass);

    }

    private void writeNewUser(String startPersonId, String startFirstName, String startLastName, int startChosenDistans, boolean startSignedIn) {
        Person personClass = new Person(startPersonId, startFirstName, startLastName, startChosenDistans);
        mDatabase.child("users").child(startFirstName).setValue(personClass);
    }







    /**
     * Sätter toolbar
     */
    public void setToolbar(){
        toolbar =(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    /**
     * Sätter topmenyn
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("tag", "onCreateOptionsMenu ");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     *Onklick på meny valen
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_friends:
                Log.d("tag", "mainactivity menu_friends");
                intent = new Intent(this, FriendHandler.class);
                startActivity(intent);

                return true;
            case R.id.menu_change_distans:
                Log.d("tag", "mainactivity menu_change_distans");

                dialogChangeDistans.showDialogChangeDistans(this, chosenDistansInt);

                return true;
            case R.id.menu_notification:
                Log.d("tag", "mainactivity menu_notification");
                return true;
            case R.id.menu_busy:
                Log.d("tag", "mainactivity menu_busy");
                return true;
            case R.id.menu_sing_out:
                Log.d("tag", "mainactivity menu_sing_out");
                intent = new Intent(this, Login.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateChosenDistansText(int thisUserDistans){
        chosenDistansInt = thisUserDistans;
        if(thisUser != null) {
            Log.d("tag", "updateChosenDistansText");
            //int thisUserDistans = thisUser.getChosenDistansInt();
            int distansConverted;
            String distansValue;

            if (thisUserDistans < 1000) {
                distansConverted = thisUserDistans;
                distansValue = panelDistansM;

                chosenDistansText.setText(distansConverted + " " + distansValue);
            } else if (thisUserDistans < 10000) {
                Double distansConvertedDouble = Double.valueOf(thisUserDistans) / 1000;
                distansValue = panelDistansKm;

                chosenDistansText.setText(distansConvertedDouble + " " + distansValue);
            } else {
                distansConverted = thisUserDistans / 10000;
                distansValue = panelDistansMil;

                chosenDistansText.setText(distansConverted + " " + distansValue);
            }
        }
    }







    /**
     * Karta
     */

    public void mapApiCreat(){
        Log.d("tag", "mapApiCreat");
        //"Skapar" API google map
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    //Metoder till kartan och gps punkterna
    protected void onStart() {
        Log.d("tag", "Start");
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        Log.d("tag", "Stop");
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d("tag", "onConnected");


        //personList.closePersonArrayList.clear();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_ACCESS_COURSE_LOCATION);
        } startLocationUpdates();

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLastLocation == null){
            startLocationUpdates();
        }

        if (mLastLocation != null) {
            onLocationChanged(mLastLocation);
        }else {
            Toast.makeText(this, "Platsen hittas inte", Toast.LENGTH_SHORT).show();
        }
    }

    protected void startLocationUpdates() {
        Log.d("tag", "startLocationUpdates");
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000)
                .setFastestInterval(1000);

        // Request location updates
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_ACCESS_COURSE_LOCATION);
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    /**
     *När användarens position har ändrats
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.d("tag", "onLocationChanged");


        setThisUsersNewLocation(location);
        //updateListOfClosePerson();


    }

    public void setThisUsersNewLocation(Location location){
        Log.d("tag", "setThisUsersNewLocation");
        Log.d("tag", "thisuser" + thisUser);
        //Log.d("tag", "thisuser" + thisUser.getFullName());

        //if(thisUser != null) {
            Log.d("tag", " thisUser OK Sätter GPS");
            //thisUser.setLocationLatitude(location.getLatitude());
            //thisUser.setLocationLongitude(location.getLongitude());
            mDatabase.child("users").child(thisPersonPhoneId).child("locationLatitude").setValue(location.getLatitude());
            mDatabase.child("users").child(thisPersonPhoneId).child("locationLongitude").setValue(location.getLongitude());

            //updateListOfClosePerson();

        //}else{
           // Log.d("tag", " thisUser == null ______---------------------!!!!!! ");
        //}
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }



    /**
     * Skapar en lista med personer som är nära. Den listan skapas utifrån vänlistan.
     * De personer som är nära läggs i en arraylist personList.closePersonArrayList
     */
    public void updateListOfClosePerson() {
        Log.d("tag", "updateListClosePerson ");

        //personList.closePersonArrayList.clear();

        //uppdatera alla vänner som är lagrade på telefonen utifrån



        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                thisUser = dataSnapshot.child("users").child(thisPersonPhoneId).getValue(Person.class);
                ArrayList<String> closePersonIdList = new ArrayList<String>();

                //kollar alla användare i databasen
                for (DataSnapshot snap : dataSnapshot.child("users").getChildren()) {
                    Person personB = snap.getValue(Person.class);

                    int distanceInM;
                    //kollar avståndet mellan denna användare och personB
                    distanceInM = getDistanBetween(personB);

                    //skapar en arraylist med alla nuvarande nära vänners id
                    for(Person personBToGetId : personList.closePersonArrayList ){
                        String personBId = personBToGetId.getPersonId();
                        closePersonIdList.add(personBId);
                    }

                    //om denna användare och personB valda avstånd är mindre eller lika som avståndet imellan dem
                    //läggs personB till i personList.closePersonArrayList
                    if (distanceInM <= thisUser.getChosenDistansInt() && distanceInM <= personB.getChosenDistansInt()
                        && !thisPersonPhoneId.equals(personB.getPersonId())) {

                        if(!closePersonIdList.contains(personB.getPersonId())) {
                            personB.setDistansBetween(distanceInM);
                            personList.closePersonArrayList.add(personB);
                        }
                    }

                    //kollar hella personList.closePersonArrayList om det är någon som ska bort
                    //utifall avståndet har ändrats
                    for (Person personBToRemove : personList.closePersonArrayList ){
                        distanceInM = getDistanBetween(personBToRemove);
                        if (distanceInM > thisUser.getChosenDistansInt() && distanceInM > personB.getChosenDistansInt()) {
                            personList.closePersonArrayList.remove(personBToRemove);
                        }
                    }
                }

                personList.sortPersonArrayList();

                String numberOfFriensString = String.valueOf(personList.closePersonArrayList.size());
                numberOfFrieansText.setText(numberOfFriensString + " " + panelNumberOfFriends);
                createList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public int getDistanBetween(Person personB){
        LatLng mPositionA;
        LatLng mPositionB;
        mPositionA = new LatLng(thisUser.getLocationLatitude(), thisUser.getLocationLongitude());
        mPositionB = new LatLng(personB.getLocationLatitude(), personB.getLocationLongitude());

        double distance = SphericalUtil.computeDistanceBetween(mPositionA, mPositionB);
        int distanceInM = (int) distance;
        return distanceInM;
    }

    /**
     * Lägger till listan med nära vänner till vyn
     */
    public void createList() {
        Log.d("tag", "createList");

        Integer[] picList = personList.personPicArrayListToArray();
        //Person[] closePersonList = personList.personClosePersonArrayListToArray();

        if (adapter == null) {
            Log.d("tag", "createList adapter NULL");
            adapter = new ListContiner(this, picList, personList.closePersonArrayList);

            listView = (ListView) findViewById(R.id.list_close_friends);
            listView.setAdapter(adapter);




            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("tag", " click på en vän");

                dialogViewListFriend.showDialogViewListFriend(getWindow().getContext());

                }
            });



        }else{
            Log.d("tag", "createList notifyDataSetChanged");
            adapter.notifyDataSetChanged();
            //updatedData(adapter);
        }
    }

    int count = 0;
    public void updatedData(ListContiner adapter) {

        count++;
        if(count > 5){
            //writeNewUser("Slussen", "Slussen", 200, 59.31951240000001, 18.07214060000001);
            mDatabase.child("users").child("Slussen").child("chosenDistansInt").setValue(200);
            //fakeP3.setChosenDistans(100000);
        }
        if (count > 10){
            // writeNewUser("Slussen", "Slussen", 10000, 59.31951240000001, 18.07214060000001);
            mDatabase.child("users").child("Slussen").child("chosenDistansInt").setValue(10000);
            //fakeP3.setChosenDistans(500);

            //fakeP2.setLocationLatitude(59.314449);
            //fakeP2.setLocationLongitude(59.314449);
        }
        if (count > 15){
            //fakeP2.setLocationLatitude(59.31803730000001);
            //fakeP2.setLocationLongitude(18.38822559999994);
            count = 0;
        }
    }
}
