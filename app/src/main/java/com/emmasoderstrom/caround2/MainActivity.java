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

    Toolbar toolbar;
    //DialogChangeDistans dialogChangeDistans;
    DialogChangeDistansOneWheel dialogChangeDistans;
    Intent intent;

    final static int MY_PERMISSION_ACCESS_COURSE_LOCATION = 11;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    Location thisUserLocation;
    TextView chosenDistansText;
    String panelDistansM;
    String panelDistansKm;
    String panelDistansMil;
    TextView numberOfFrieansText;
    String panelNumberOfFriends;
    TextView mLatitudeText;
    TextView mLongitudeText;
    ListContiner adapter;
    ListView listView = null;

    ArrayList<Person> personListArray = new ArrayList<Person>();
    PersonList personList;

    long numberOfUser;


    private DatabaseReference mDatabase;
    Person thisUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setToolbar();

        chosenDistansText = (TextView)findViewById(R.id.text_distans);
        panelDistansM = getString(R.string.panel_distans_m);
        panelDistansKm = getString(R.string.panel_distans_km);
        panelDistansMil = getString(R.string.panel_distans_mil);
        numberOfFrieansText = (TextView)findViewById(R.id.text_number_of_friends);
        panelNumberOfFriends = getString(R.string.panel_number_of_friends);
        //mLatitudeText = (TextView)findViewById(R.id.text1);
        //mLongitudeText = (TextView)findViewById(R.id.text2);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        Log.d("tag", "databas");




        //Log.d("tag", "this user" + thisUser.getChosenDistansInt());




        creatFakeUser();
        //FirebaseStorage storage = FirebaseStorage.getInstance();
        //dialogChangeDistans = new DialogChangeDistans();
        dialogChangeDistans = new DialogChangeDistansOneWheel(this, thisUser);

        //MapAPI mapAPI = new MapAPI(this);
        mapApiCreat();

        createList();

    }

    public void setThisUser(){
        Log.d("tag", "databas 2 ");

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("tag", "databas" + dataSnapshot.child("users")
                        .child("Emma"));
                /*thisUser = dataSnapshot.child("users")
                        .child(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID))
                        .getValue(Person.class);*/
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
        //getSupportActionBar().setTitle("C around");
    }

    /**
     * Sätter topmenyn
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.top_menu, menu);
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
                //intent = new Intent(this, MapsActivity.class);
                //startActivity(intent);
                return true;
            case R.id.menu_change_distans:
                Log.d("tag", "mainactivity menu_change_distans");

                dialogChangeDistans.showDialogChangeDistans(this);

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

    /**
     * Skapar personer och skapar en arraylist med alla vänner
     */
    public void creatFakeUser(){

        Log.d("tag", "creatFakeUser ");
        personList = new PersonList(this, 0);

        //setThisUser();
        thisUser = new Person ("Denna", "Denna", "Användare", 100000);
        //writeNewUser("testtest", "Denna", "Användare", 10000);

        writeNewUser("Jonas", "Amnesten", 10000, 59.31803730000001, 18.38822559999994);
        writeNewUser("Mamma", "Pappa", 10500, 59.35460399999999, 18.282052499999963);
        writeNewUser("Slussen", "Slussen", 10000, 59.31951240000001, 18.07214060000001);
        writeNewUser("Karolinska", "Instutet", 2000, 59.34814839999999, 18.023657800000024);
        writeNewUser("Emma", "Söderström", 1550, 59.31803730000001, 18.38822559999994);


        Log.d("tag", "user längd " + mDatabase.child("users"));


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("tag", " ändrat 1 ------------>>>>>>" + dataSnapshot.child("users").getChildrenCount());
                numberOfUser = dataSnapshot.child("users").getChildrenCount();

                //for (DataSnapshot snap: dataSnapshot.child("users").getChildren()) {
                    updateListOfClosePerson();
                //}

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("tag", " Error 2 ------------>>>>>>");
            }
        });

        updateChosenDistansText();

    }

    private void writeNewUser(String startFirstName, String startLastName, int startChosenDistans,
                              double startLocationLatitude, double startLocationLongitude) {
        Person personClass = new Person(startFirstName, startLastName, startChosenDistans,
                                    startLocationLatitude, startLocationLongitude);

        personList.allPersonArrayList.add(personClass);
        mDatabase.child("users").child(startFirstName).setValue(personClass);

    }

    private void writeNewUser(String startPersonId, String startFirstName, String startLastName, int startChosenDistans) {
        Person personClass = new Person(startPersonId, startFirstName, startLastName, startChosenDistans);
        mDatabase.child("users").child(startFirstName).setValue(personClass);
    }


    public void updateChosenDistansText(){
        Log.d("tag", "updateChosenDistansText");
        int thisUserDistans = thisUser.getChosenDistansInt();
        int distansConverted;
        String distansValue;

        if(thisUserDistans < 1000){
            distansConverted = thisUserDistans;
            distansValue = panelDistansM;

            chosenDistansText.setText(distansConverted + " " + distansValue);
        }else if(thisUserDistans < 10000){
            Double distansConvertedDouble = Double.valueOf(thisUserDistans) / 1000;
            distansValue = panelDistansKm;

            chosenDistansText.setText(distansConvertedDouble + " " + distansValue);
        }else{
            distansConverted = thisUserDistans / 10000;
            distansValue = panelDistansMil;

            chosenDistansText.setText(distansConverted + " " + distansValue);
        }
    }







    /**
     * Karta
     */

    public void mapApiCreat(){
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
        Log.d("tag", " onLocationChanged");

        setThisUsersNewLocation(location);
        updateListOfClosePerson();

        //skas ta bort senare, finns nu för att se position

    }

    public void setThisUsersNewLocation(Location location){
        Log.d("tag", " setThisUsersNewLocation ______---------------------!!!!!! " + thisUser.getLocationLatitude());
        if(thisUser != null) {
            thisUser.setLocationLatitude(location.getLatitude());
            thisUser.setLocationLongitude(location.getLongitude());
        }else{
            Log.d("tag", " thisUser == null ______---------------------!!!!!! ");
        }
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
    public void updateListOfClosePerson(){
        Log.d("tag", "updateListClosePerson ");
        personList.closePersonArrayList.clear();

        //for (int i = 0; i < personList.allPersonArrayList.size(); i++) {
            //Person personB = personList.allPersonArrayList.get(i);

            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot snap: dataSnapshot.child("users").getChildren()) {
                        //Log.d("tag", " lista databas " + snap.child("locationLatitude").getValue());
                        Person personB = snap.getValue(Person.class);

                        LatLng mPositionA;
                        LatLng mPositionB;
                        mPositionA = new LatLng(thisUser.getLocationLatitude(), thisUser.getLocationLongitude());
                        mPositionB = new LatLng(personB.getLocationLatitude(), personB.getLocationLongitude());

                        double distance = SphericalUtil.computeDistanceBetween(mPositionA, mPositionB);
                        int distanceInM = (int)distance;


                        if(distanceInM <= thisUser.getChosenDistansInt() && distanceInM <= personB.getChosenDistansInt()
                                && personB.getPersonId() != "Denna" && personB.getPersonId() != Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)){
                            //Log.d("tag", "closePersonArrayList " + personB.firstName + " " + distanceInM);
                            personB.setDistansBetween(distanceInM);
                            personList.closePersonArrayList.add(personB);
                        }
                    }

                    personList.sortPersonArrayList();
                    Log.d("tag", "onLocationChanged closePersonArrayList size " + personList.closePersonArrayList.size());

                    String numberOfFriensString = String.valueOf(personList.closePersonArrayList.size());
                    numberOfFrieansText.setText(numberOfFriensString + " " + panelNumberOfFriends);
                    createList();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        //}

        /*personList.sortPersonArrayList();
        Log.d("tag", "onLocationChanged closePersonArrayList size " + personList.closePersonArrayList.size());

        String numberOfFriensString = String.valueOf(personList.closePersonArrayList.size());
        //String panelNumberOfFriends = getString(R.string.panel_number_of_friends);
        numberOfFrieansText.setText(numberOfFriensString + " " + panelNumberOfFriends);
        createList();*/
    }

    /**
     * Lägger till listan med nära vänner till vyn
     */
    public void createList() {
        Log.d("tag", "createList 1");

        if(adapter != null) {
            updatedData(adapter);
        }

        Integer[] picList = personList.personPicArrayListToArray();
        Person[] closePersonList = personList.personClosePersonArrayListToArray();

        if (adapter == null && personList.closePersonArrayList.size() > 0) {
            Log.d("tag", "createList 2 " + adapter);

            adapter = new ListContiner(this, picList, personList.closePersonArrayList);

            listView = (ListView) findViewById(R.id.list_close_friends);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("tag", adapter.getItem(position).getFullName() + " has been selected!");
                    //Toast.makeText(getApplicationContext(), Slecteditem, Toast.LENGTH_SHORT).show();
                    Log.d("tag", " click på en vän");

                }
            });
        }

        //personList.personChosenDistansArrayList.add(String.valueOf(2000));
        //adapter.notifyDataSetChanged();

    }

    int count = 0;
    public void updatedData(ListContiner adapter) {
        //Log.d("tag", " updatedData " + count + " " + fakeP1.getFullName());

        // för att testa förändring på vänner postition
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

        //updateListOfClosePerson();
        //adapter.clear();
        //adapter.addAll(personList.closePersonArrayList);
    }
}
