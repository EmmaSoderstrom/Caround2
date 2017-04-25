package com.emmasoderstrom.caround2;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
import com.google.android.gms.plus.Plus;
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
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static MainActivity insMain;

    public static final String EXTRA_MESSAGE = "com.emmasoderstrom.caround2.MESSAGE";

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

    boolean appIsInForegroundMode;

    int REQUEST_CHECK_SETTINGS = 100;
    final static int MY_PERMISSION_ACCESS_COURSE_LOCATION = 11;
    final static int REQUEST_LOCATION = 199;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;


    TextView numberOfFriendsText;
    String panelNumberOfFriends;
    String numberOfFriensString;
    int chosenDistansInt;
    TextView chosenDistansText;
    String panelDistansM;
    String panelDistansKm;
    String panelDistansMil;

    PersonList personList;
    ArrayList<Person> closePersonList = new ArrayList<Person>();
    MainListContiner adapter;
    ListView listView = null;

    ListView listCloseFriends;
    RelativeLayout gpsPermissionRView;
    RelativeLayout gpsNotOnView;
    RelativeLayout noFriendCloseView;
    RelativeLayout internetNotOnView;


    ArrayList<Person> lastNotiArray = new ArrayList<>();
    ArrayList<Person> tempLastNotiArray = new ArrayList<>();
    ArrayList<Person> newFriendNotiArray = new ArrayList<>();
    ArrayList<Person> toRmoveFriendNotiArray = new ArrayList<>();
    ArrayList<Person> oldFriendNotiArray = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        insMain = this;

        thisUserID = Login.thisUserID;
        Log.d("tag", "onCreate: detta är det jag ska tittta på" + thisUserID);


        mDatabase = FirebaseDatabase.getInstance().getReference();

        mapApiCreat();
        //checkPermissionGPS();

        setToolbar();
        dialogChangeDistans = new DialogChangeDistansOneWheel(this, thisUser);
        dialogViewListFriend = new DialogViewListFriend();

        chosenDistansText = (TextView) findViewById(R.id.text_distans);
        panelDistansM = getString(R.string.panel_distans_m);
        panelDistansKm = getString(R.string.panel_distans_km);
        panelDistansMil = getString(R.string.panel_distans_mil);
        numberOfFriendsText = (TextView) findViewById(R.id.text_number_of_friends);
        panelNumberOfFriends = getString(R.string.panel_number_of_friends);

        setThisUser();

        personList = new PersonList(this, 0);

        noFriendCloseView = (RelativeLayout) findViewById(R.id.no_friend_close);
        listCloseFriends = (ListView) findViewById(R.id.list_close_friends);
        gpsPermissionRView = (RelativeLayout) findViewById(R.id.permission_not_granted);
        gpsNotOnView = (RelativeLayout) findViewById(R.id.gps_not_on);
        internetNotOnView = (RelativeLayout) findViewById(R.id.internet_not_on);

    }

    public static MainActivity getInstace() {
        return insMain;
    }

    protected void onStart() {
        Log.d("tag", "Start");
        mGoogleApiClient.connect();
        checkGPSOn();

        if (checkInternetOn(this)) {
            Log.d("tag", "signIn: inget internett      true-------zzzzzzz--------->");

        } else {
            Log.d("tag", "signIn: inget internett      false-------zzzzzzz--------->");
            noInternet(this);

        }
        super.onStart();
    }

    protected void onStop() {
        Log.d("tag", "Stop");
        //mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.d("tag", "onPause");
        super.onPause();
        appIsInForegroundMode = false;
    }

    @Override
    protected void onResume() {
        Log.d("tag", "onResume");
        super.onResume();
        appIsInForegroundMode = true;
        oldFriendNotiArray.clear();
    }


    public void setThisUser() {
        Log.d("tag", "setThisUser");

        //endast en gång, sätter användarens starvärden.
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("tag", "<----------------spelare null Hämta spelare------------------>>>>>>>>>>");

                thisUser = dataSnapshot.child("users")
                        .child(thisUserID)
                        .getValue(Person.class);

                chosenDistansInt = thisUser.getChosenDistansInt();
                chosenDistansText.setText(updateChosenDistansText(chosenDistansInt));

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

                if (thisUser != null) {
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


    /**
     * Sätter toolbar
     */
    public void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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
     * Onklick på meny valen
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
                //MenuItem notiItemMenu = (MenuItem)findViewById(R.id.menu_notification);
                if (item.isChecked()) {
                    item.setChecked(false);
                    //notiIsCheckt = false;
                    mDatabase.child("users").child(thisUserID).child("notiIsCheckt").setValue(false);
                } else {
                    item.setChecked(true);
                    //notiIsCheckt = true;
                    mDatabase.child("users").child(thisUserID).child("notiIsCheckt").setValue(true);
                }
                return true;
//            case R.id.menu_busy:
//                Log.d("tag", "mainactivity menu_busy");
//                return true;
            case R.id.menu_sing_out:
                Log.d("tag", "mainactivity menu_sing_out");

                if (mGoogleApiClient != null) {
                    mGoogleApiClient.disconnect();
                }
                thisUser = null;

                intent = new Intent(this, Login.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public String updateChosenDistansText(int thisUserDistans) {
        Log.d("tag", "updateChosenDistansText ");

        chosenDistansInt = thisUserDistans;
        String chosenDistensString = null;

        if (thisUser != null) {
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

    public void mapApiCreat() {
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

        } else {
            Log.d("tag", "onConnected permission ok");

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            //kolla om gps är på
            if (mLastLocation != null) {
                onLocationChanged(mLastLocation);
                startLocationUpdates();
            } else {
                startLocationUpdates();

            }
        }
    }

    /**
     * Startar dialogruta om att få använda gps
     */
    public void checkPermissionGPS() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //om inte ok
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_ACCESS_COURSE_LOCATION);


        } else {
            //om ok
        }
    }

    /**
     * Dilaoge ruta som frågar om att få använda gps
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        RelativeLayout gpsPermissionRView = (RelativeLayout) findViewById(R.id.permission_not_granted);

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
    boolean firstConnect = true;

    public void checkGPSOn() {
        Log.d("tag", "checkGPSOn: ");

        if (firstConnect) {

            PendingResult<LocationSettingsResult> result;

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
                            Log.d("tag", "onResult: SUCCESS");
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are NOT satisfied. But could be fixed by showing the user a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                Log.d("tag", "onResult: RESOLUTION_REQUIRED");
                                if (appIsInForegroundMode) {
                                    status.startResolutionForResult(MainActivity.this, REQUEST_LOCATION);
                                }
                                firstConnect = false;

                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            Log.d("tag", "onResult: SETTINGS_CHANGE_UNAVAILABLE");
                            break;
                    }
                }
            });
        }
    }

    /**
     * Dilaoge ruta som frågar om att sätta på gps
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("tag", "onActivityResult()");
        firstConnect = true;

        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        // All required changes were successfully made
                        Log.d("tag", "Location enabled by user!");

                        listCloseFriends.setVisibility(VISIBLE);
                        gpsNotOnView.setVisibility(INVISIBLE);

                        numberOfFriendsText.setText(numberOfFriensString + " " + panelNumberOfFriends);
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        Log.d("tag", "Location not enabled, user cancelled.");

                        listCloseFriends.setVisibility(INVISIBLE);
                        internetNotOnView.setVisibility(INVISIBLE);
                        gpsNotOnView.setVisibility(VISIBLE);
                        numberOfFriendsText.setText(0 + " " + panelNumberOfFriends);
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }

    public static boolean checkInternetOn(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    public static void noInternet(Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.dialog_no_internet)
                //.setTitle("Ingeintenet hittat!")
                .setCancelable(false)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }
                );
        AlertDialog alert = builder.create();
        alert.show();
    }
    



    /**
     * Knappar för att starta dialogruror om gps
     * @param view
     */
    public void onClickGpsPermission(View view){
        checkPermissionGPS();
    }
    public void onClickGpsTurnOn(View view){
        checkGPSOn();
    }
    public void onClickChangeDistans(View view){
        dialogChangeDistans.showDialogChangeDistans(this, chosenDistansInt);
    }




    protected void startLocationUpdates() {
        Log.d("tag", "startLocationUpdates");

        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(20 * 1000)
                .setFastestInterval(10 * 1000);

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
        gpsNotOnView.setVisibility(INVISIBLE);

        setThisUsersNewLocation(location);
        //updateListOfClosePerson();
    }

    public void setThisUsersNewLocation(Location location){
        Log.d("tag", "setThisUsersNewLocation");

        //if(thisUser != null) {
            Log.d("tag", " thisUser OK Sätter GPS");
            mDatabase.child("users").child(thisUserID).child("locationLatitude").setValue(location.getLatitude());
            mDatabase.child("users").child(thisUserID).child("locationLongitude").setValue(location.getLongitude());

            Log.d("tag", "setThisUsersNewLocation:System.currentTimeMillis() " + System.currentTimeMillis()/1000);
            mDatabase.child("users").child(thisUserID).child("lastUpdateLocation").setValue(System.currentTimeMillis()/1000);



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
                                //samt att personB senaste position ska ha uppdateras senast
                                //och om personB är inloggad
                                //läggs personB till i personList.closePersonArrayList
                                if (distanceInM <= thisUser.getChosenDistansInt()
                                        && checkTimeDistance(personB) <= 60 * 10
                                        && distanceInM <= personB.getChosenDistansInt()
                                        && personB.getIfLoggedIn()) {
                                    personB.setDistansBetween(distanceInM);
                                    closePersonList.add(personB);
                                }

                            }else{

                                //kollar i closePersonList om det är någon som ska bort
                                int distanceInM = getDistanBetween(personB);

                                if (distanceInM > thisUser.getChosenDistansInt()
                                        || distanceInM > personB.getChosenDistansInt()
                                        || personB.getIfLoggedIn() == false) {

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



                    numberOfFriensString = String.valueOf(closePersonList.size());
                    numberOfFriendsText.setText(numberOfFriensString + " " + panelNumberOfFriends);

                    if(checkInternetOn(insMain)) {
                        Log.d("tag", "signIn: internett      true-------zzzzzzz--------->");
                        listCloseFriends.setVisibility(VISIBLE);
                        internetNotOnView.setVisibility(INVISIBLE);
                        createList();

                    }else{
                        Log.d("tag", "signIn: internett      false-------zzzzzzz--------->");
                        listCloseFriends.setVisibility(INVISIBLE);
                        internetNotOnView.setVisibility(VISIBLE);
                        numberOfFriendsText.setText(0 + " " + panelNumberOfFriends);

                    }

                    //createList();

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

    public double checkTimeDistance(Person personB){
        double timeDistance = personB.getLastUpdateLocation() - System.currentTimeMillis()/1000;
        return timeDistance;
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

            viewNoFriendClose();

            for (Person personB : closePersonList) {
                lastNotiArray.add(personB);
            }

        }else {
            Log.d("tag", "createList notifyDataSetChanged");
            adapter.notifyDataSetChanged();

            viewNoFriendClose();



            //Notifikation om appen inte är aktiv
            if (appIsInForegroundMode == false) {

                //kollar om lastNotiArray innhåller en person som inte finns i closePersonList
                // för att då tas bort från notifikationen
                for (Person personB : lastNotiArray) {
                    if (closePersonList.contains(personB)) {
                    } else {
                        //om lastNotiArray innehåller en perosn som inte finns i closePersonList
                        //då ska den personen tas bort      ??från notifikationen och lastNotiArray och oldNotiArray??
                        toRmoveFriendNotiArray.add(personB);
                        Log.d("tag", "createList: toRmoveFriendNotiArray " + toRmoveFriendNotiArray);
                    }

                    //ta bort från oldNotiArray
                    for (Person personBRemove : toRmoveFriendNotiArray) {
                        oldFriendNotiArray.remove(personBRemove);
                        Log.d("tag", "createList: toRmoveFriendNotiArray " + toRmoveFriendNotiArray);
                    }
                }

                //kollar om closePersonList innhåller en person som inte finns i lastNotiArray
                for (Person personB : closePersonList) {
                    if (lastNotiArray.contains(personB)) {
                    } else {
                        //om den inte innehåller då är det en ny nära vän
                        newFriendNotiArray.add(personB);
                    }

                    // temp finns för att göra en ny lastNotiArray utav closePersonList
                    tempLastNotiArray.add(personB);
                }





                //om det finns en ny person så ska en notifikation göras
                if (newFriendNotiArray.size() > 0 || toRmoveFriendNotiArray.size() > 0 ) {

                    sendNotification(newFriendNotiArray);

                    //ränsar ut arrayer
                    newFriendNotiArray.clear();
                    lastNotiArray.clear();
                    toRmoveFriendNotiArray.clear();


                    //skapar en ny lastNotiArray
                    for (Person personB : tempLastNotiArray) {
                        lastNotiArray.add(personB);
                    }

                    tempLastNotiArray.clear();
                }
            }
        }

    }

    public void viewNoFriendClose(){
        if(closePersonList.size() < 1){
            noFriendCloseView.setVisibility(VISIBLE);
        }else{
            noFriendCloseView.setVisibility(INVISIBLE);
        }
    }



    public void sendNotification(ArrayList<Person> newFriendNotiArray){
        Log.d("tag", "sendNotification: ????????" + appIsInForegroundMode);

        if(appIsInForegroundMode == false) {

            Log.d("tag", "sendNotification: ????????" + appIsInForegroundMode);

            String notiTitle = null;
            String notiText = null;

            //if(newFriendNotiArray.size() > 0){
            //Title för notifikation
            String oneFriendClose = getString(R.string.notification_one_friend_close);
            String multiFriendClose = getString(R.string.notification_multi_friend_close);

            int numberFriends = newFriendNotiArray.size() + oldFriendNotiArray.size();

            if (numberFriends == 0) {

                NotificationManager notifManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                notifManager.cancelAll();

            }
            if (numberFriends == 1) {
                notiTitle = numberFriends + " " + oneFriendClose;
            }else if (numberFriends > 1) {
                notiTitle = numberFriends + " " + multiFriendClose;
            }


            //Andra text för notifikation
            String allOldName = "";
            for (int i = 0; i < oldFriendNotiArray.size(); i++) {
                Person personB = oldFriendNotiArray.get(i);

                if(newFriendNotiArray.size() == 0){
                    if(oldFriendNotiArray.size() - 1 == i){
                        allOldName = personB.getFullName() + allOldName;
                    }else{
                        allOldName = ", " + personB.getFullName() + allOldName;
                    }

                }else{
                    allOldName = ", " + personB.getFullName() + allOldName;
                }

            }

            if (newFriendNotiArray.size() == 1) {
                notiText = newFriendNotiArray.get(0).getFullName() + allOldName;
            }else if (newFriendNotiArray.size() > 1) {
                String allNewName = "";
                for (int i = 0; i < newFriendNotiArray.size(); i++) {
                    if (newFriendNotiArray.size() - 1 == i) {
                        allNewName = newFriendNotiArray.get(i).getFullName() + allNewName;
                    } else {
                        allNewName = ", " + newFriendNotiArray.get(i).getFullName() + allNewName;
                    }
                }
            }else {
                notiText = allOldName;
            }
            //}

            if (numberFriends > 0 && thisUser.notiIsCheckt) {
                Log.d("tag", "sendNotification: om ändring _____--------->");
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.mipmap.icon2)
                                .setContentTitle(notiTitle)
                                .setContentText(notiText)
                                .setAutoCancel(true);
                // Creates an explicit intent for an Activity in your app
                Intent resultIntent = new Intent(this, MainActivity.class);

                // The stack builder object will contain an artificial back stack for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(MainActivity.class);
                // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                // mId allows you to update the notification later on.
                int myId = 0;
                mNotificationManager.notify(myId, mBuilder.build());
            }
        }


        for (Person personB : newFriendNotiArray) {
            oldFriendNotiArray.add(personB);
        }

    }

}
