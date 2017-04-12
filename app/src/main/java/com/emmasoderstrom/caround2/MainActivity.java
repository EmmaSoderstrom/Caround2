package com.emmasoderstrom.caround2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.emmasoderstrom.caround2.FriendHandler.MY_PERMISSIONS_REQUEST_READ_CONTACTS;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String MyPREFERENCES = "com.emmasoderstrom.caround2.saveid.MyPREFERENCES";
    public static final String USER_ID_KEY = "userIdKey";
    public static String thisUserID;
    public static SharedPreferences sharedPreferences;

    Intent intent;
    private DatabaseReference mDatabase;
    Person thisUser;

    Toolbar toolbar;
    DialogChangeDistansOneWheel dialogChangeDistans;
    DialogViewListFriend dialogViewListFriend;

    int REQUEST_CHECK_SETTINGS = 100;
    final static int MY_PERMISSION_ACCESS_COURSE_LOCATION = 11;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    TextView numberOfFriendsText;
    String panelNumberOfFriends;
    int chosenDistansInt;
    TextView chosenDistansText;
    String panelDistansM;
    String panelDistansKm;
    String panelDistansMil;

    PersonList personList;
    ArrayList<Person> closePersonList = new ArrayList<Person>();
    MainListContiner adapter;
    ListView listView = null;
    //String thisPersonPhoneId;

    ListView listCloseFriends;
    RelativeLayout gpsNotOn;
    final static int REQUEST_LOCATION = 199;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        thisUserID = sharedPreferences.getString(USER_ID_KEY, null);

        Intent intent = getIntent();
        String message = intent.getStringExtra(Login.EXTRA_MESSAGE);
        thisUserID = message;

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mapApiCreat();
        //checkPermissionGPS();

        setToolbar();
        dialogChangeDistans = new DialogChangeDistansOneWheel(this, thisUser);
        dialogViewListFriend = new DialogViewListFriend();

        chosenDistansText = (TextView)findViewById(R.id.text_distans);
        panelDistansM = getString(R.string.panel_distans_m);
        panelDistansKm = getString(R.string.panel_distans_km);
        panelDistansMil = getString(R.string.panel_distans_mil);
        numberOfFriendsText = (TextView)findViewById(R.id.text_number_of_friends);
        panelNumberOfFriends = getString(R.string.panel_number_of_friends);

        setThisUser();

        personList = new PersonList(this, 0);
        //creatFakeUser();

        listCloseFriends = (ListView)findViewById(R.id.list_close_friends);
        gpsNotOn = (RelativeLayout)findViewById(R.id.gps_not_on);

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
                            .child(thisUserID)
                            .getValue(Person.class);

                    chosenDistansInt = thisUser.getChosenDistansInt();
                    chosenDistansText.setText(updateChosenDistansText(chosenDistansInt));
                //}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //sker när något ändras på denna användarens databas värden
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("tag", "<----------------2222222222------------------>>>>>>>>>>");
                thisUser = dataSnapshot.child("users").child(thisUserID).getValue(Person.class);

                if(thisUser != null) {
                    Log.d("tag", "onDataChange: mDatabase -------------->>>>><" + thisUser);
                    updateListOfClosePerson();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Skapar personer och skapar en arraylist med alla vänner
     */
//    public void creatFakeUser(){
//        Log.d("tag", "creatFakeUser ");
//
//        writeNewUser("id1", "Jonas2", "Amnesten", "12345", 10000, true, 59.31803730000001, 18.38822559999994);
//        writeNewUser("id2", "Ulla", "Söderström", "0707324181", 10500, true, 59.35460399999999, 18.282052499999963);
//        writeNewUser("id3", "Patrik", "Slussen", "0739168234", 1000, true, 59.31951240000001, 18.07214060000001);
//        writeNewUser("id4", "Karolinska", "Instutet", "12345", 2000, true, 59.34814839999999, 18.023657800000024);
//        writeNewUser("id5", "Emma", "Söderström", "12345", 1550, true, 59.31803730000001, 18.38822559999994);
//        writeNewUser("id6", "JonasNot", "Amnesten", "12345", 10000, true, 59.31803730000001, 18.38822559999994);
//        writeNewUser("id7", "UllaNot", "Södersröm", "12345", 10500, true, 59.35460399999999, 18.282052499999963);
//        writeNewUser("id8", "SlussenNot", "Slussen", "12345", 1000, true, 59.31951240000001, 18.07214060000001);
//        writeNewUser("id9", "KarolinskaNot", "Instutet", "12345", 2000, true, 59.34814839999999, 18.023657800000024);
//        writeNewUser("id10", "EmmaNot", "Söderström", "12345", 1550, true, 59.31803730000001, 18.38822559999994);
//
//    }
//
//    private void writeNewUser(String startPersonId, String startFirstName, String startLastName, String startPhoneNumber, int startChosenDistans, boolean startSignedIn,
//                              double startLocationLatitude, double startLocationLongitude) {
//        Person personClass = new Person(startPersonId, startFirstName, startLastName, startPhoneNumber, startChosenDistans,
//                startLocationLatitude, startLocationLongitude);
//
//        personList.allPersonArrayList.add(personClass);
//        mDatabase.child("users").child(startPersonId).setValue(personClass);
//
//    }






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
                //FirebaseAuth.getInstance().signOut();
                //Auth.GoogleSignInApi.signOut();

                /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .enableAutoManage(this, this)
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build();

                mGoogleApiClient.connect();

                if(mGoogleApiClient.isConnected()){
                    Log.d("tag", "mGoogleApiClient conecktad");
                    FirebaseAuth.getInstance().signOut();
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                }else{
                    Log.d("tag", "mGoogleApiClient inte conecktad");
                }*/

                //if (view == buttonLogout) {
                    //firebaseAuth.signOut();
                    FirebaseAuth.getInstance().signOut();
                    finish();
                    //startActivity(new Intent(this, SignActivity.class));
                //}

                thisUser = null;
                intent = new Intent(this, Login.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String updateChosenDistansText(int thisUserDistans){
        Log.d("tag", "updateChosenDistansText ");

        chosenDistansInt = thisUserDistans;
        String chosenDistensString = null;

        if(thisUser != null) {
            int distansConverted;
            String distansValue;

            if (thisUserDistans < 1000) {
                distansConverted = thisUserDistans;
                distansValue = panelDistansM;

                chosenDistensString = distansConverted + " " + distansValue;
            } else if (thisUserDistans < 10000) {
                Double distansConvertedDouble = Double.valueOf(thisUserDistans) / 1000;
                distansValue = panelDistansKm;

                chosenDistensString = distansConvertedDouble + " " + distansValue;
            } else {
                distansConverted = thisUserDistans / 10000;
                distansValue = panelDistansMil;

                chosenDistensString = distansConverted + " " + distansValue;
            }
        }
        return chosenDistensString;
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

        //Kollar permission att använda GPS
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_ACCESS_COURSE_LOCATION);

        }else{
            Log.d("tag", "onConnected permission ok");

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            //kolla om gps är på
            if (mLastLocation != null) {
                onLocationChanged(mLastLocation);
                startLocationUpdates();
            }else {
                startLocationUpdates();
                checkGPSOn();
            }
        }
    }

    /**
     * Startar dialogruta om att få använda gps
     */
    public void checkPermissionGPS(){

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //om inte ok
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_ACCESS_COURSE_LOCATION);


        }else{
            //om ok
        }
    }

    /**
     * Dilaoge ruta som frågar om att få använda gps
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        RelativeLayout gpsPermissionRView = (RelativeLayout)findViewById(R.id.permission_not_granted);

        switch (requestCode) {
            case MY_PERMISSION_ACCESS_COURSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission godkänndes sätt vi med förfrågan om permisson inviseble
                    Log.d("tag", "MY_PERMISSION_ACCESS_COURSE_LOCATION: Permission ");
                    gpsPermissionRView.setVisibility(INVISIBLE);
                    startLocationUpdates();
                } else {
                    Log.d("tag", "MY_PERMISSION_ACCESS_COURSE_LOCATION: Permission nekas");
                    //Permission nekades, gör inget.
                    gpsPermissionRView.setVisibility(VISIBLE);
                }
                return;
            }
        }
    }


    /**
     * Startar dialogruta om att sätta på gps
     */
    public void checkGPSOn(){

        PendingResult<LocationSettingsResult> result;

        //Toast.makeText(this, "Platsen hittas inte", Toast.LENGTH_SHORT).show();

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        //kollar om gps är på
        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        //...
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().

                            listCloseFriends.setVisibility(INVISIBLE);
                            gpsNotOn.setVisibility(VISIBLE);

                            status.startResolutionForResult(MainActivity.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //...
                    break;
                }
            }
        });
    }

    /**
     * Dilaoge ruta som frågar om att sätta på gps
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("tag", "onActivityResult()");

        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        // All required changes were successfully made
                        Log.d("tag", "Location enabled by user!");

                        listCloseFriends.setVisibility(VISIBLE);
                        gpsNotOn.setVisibility(INVISIBLE);

                        //onLocationChanged(mLastLocation);

                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        Log.d("tag", "Location not enabled, user cancelled.");
                        break;
                    }
                    default: {
                        break;
                    }
                }
            break;
        }
    }


    /**
     * Knappar för att starta dialogruror om gps
     * @param view
     */
    public void gpsPermission(View view){
        checkPermissionGPS();
    }
    public void gpsTurnOn(View view){
        checkGPSOn();
    }




    protected void startLocationUpdates() {
        Log.d("tag", "startLocationUpdates");

        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(3000)
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

        listCloseFriends.setVisibility(VISIBLE);
        gpsNotOn.setVisibility(INVISIBLE);

        setThisUsersNewLocation(location);
        //updateListOfClosePerson();
    }

    public void setThisUsersNewLocation(Location location){
        Log.d("tag", "setThisUsersNewLocation");

        //if(thisUser != null) {
            Log.d("tag", " thisUser OK Sätter GPS");
            mDatabase.child("users").child(thisUserID).child("locationLatitude").setValue(location.getLatitude());
            mDatabase.child("users").child(thisUserID).child("locationLongitude").setValue(location.getLongitude());

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

        //uppdatera alla vänner som är lagrade på telefonen utifrån
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                thisUser = dataSnapshot.child("users").child(thisUserID).getValue(Person.class);

                //kollar alla användare i databasen
                for (DataSnapshot snap : dataSnapshot.child("users").getChildren()) {
                    Person personB = snap.getValue(Person.class);
                    String personBId = personB.getPersonId();

                    if (thisUser.getFriendAllowed().contains(personB.getPersonId())) {

                        //Kolla om den redan innhåller person med id
                        Boolean ifclosePersonListContainsId = checkIfArryListContainsId(closePersonList, personBId);

                        if(!ifclosePersonListContainsId) {
                            int distanceInM;

                            //kollar avståndet mellan denna användare och personB
                            distanceInM = getDistanBetween(personB);

                            //om denna användare och personB valda avstånd är mindre eller lika som avståndet imellan dem
                            //läggs personB till i personList.closePersonArrayList
                            if (distanceInM <= thisUser.getChosenDistansInt() && distanceInM <= personB.getChosenDistansInt()) {
                                personB.setDistansBetween(distanceInM);
                                closePersonList.add(personB);
                            }

                        }else{

                            //kollar i closePersonList om det är någon som ska bort
                            int distanceInM = getDistanBetween(personB);

                            if (distanceInM > thisUser.getChosenDistansInt() || distanceInM > personB.getChosenDistansInt()) {

                                for (Person personBToRemove:closePersonList) {
                                    if(personBToRemove.getPersonId().equals(personB.getPersonId())){
                                        closePersonList.remove(personBToRemove);
                                        break;
                                    }
                                }
                            }else{
                                //updatera avstånds text
                                for (Person personBToUpdate:closePersonList) {
                                    if(personBToUpdate.getPersonId().equals(personB.getPersonId())){
                                        personBToUpdate.setDistansBetween(distanceInM);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                

                String numberOfFriensString = String.valueOf(closePersonList.size());
                numberOfFriendsText.setText(numberOfFriensString + " " + panelNumberOfFriends);
                createList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public boolean checkIfArryListContainsId(ArrayList<Person> closePersonList, String presonBId){

        boolean ifContines = false;

        for (int i = 0; i <closePersonList.size() ; i++) {
            if(closePersonList.get(i).getPersonId().equals(presonBId)){
                ifContines = true;
                break;
            }
        }

        return ifContines;
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

        if (adapter == null) {
            Log.d("tag", "createList adapter NULL");
            //adapter = new MainListContiner(this, personList.closePersonArrayList);
            adapter = new MainListContiner(this, closePersonList);

            listView = (ListView) findViewById(R.id.list_close_friends);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("tag", " click på en vän");

                dialogViewListFriend.showDialogViewListFriend(getWindow().getContext(), closePersonList.get(position));

                }
            });

        }else{
            Log.d("tag", "createList notifyDataSetChanged");
            adapter.notifyDataSetChanged();
            //updatedData(adapter);
        }
    }

   /* @Override
    public void onResult(@NonNull Result result) {

    }*/
}
