package com.emmasoderstrom.caround2;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.emmasoderstrom.caround2.MainActivity.sharedPreferences;

public class Login extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    public static final String MyPREFERENCES = "com.emmasoderstrom.caround2.saveid.MyPREFERENCES";
    public static final String EXTRA_MESSAGE = "com.emmasoderstrom.caround2.MESSAGE";
    public static final String USER_ID_KEY = "userIdKey";
    public static SharedPreferences sharedPreferences;
    public static String thisUserID;

    private boolean ifFirstLogout;
    private static int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    String userEmail;
    String userUID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar =(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setTitle("C around");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        findViewById(R.id.sign_in_button).setOnClickListener(this);


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null) {
                    Log.d("tag", "user logged in: " + user.getEmail());
                    userEmail = user.getEmail();
                    userUID = user.getUid();


                }else {
                    Log.d("tag", "user logged out.");
                    ifFirstLogout = false;
                }

                if(user != null && ifFirstLogout){
                    Log.d("tag", "if first logga ut. user logged in: " + user.getEmail());
                    ifFirstLogout = false;
                    logOut();
                }
            }
        };
        //bolian för att logga ut användaren om de är inloggade när de kommer till sidan.
        ifFirstLogout = true;

    }

    @Override
    public void onClick(View v) {
        Log.d("tag", "onClick");
        signIn();
    }

    private void signIn(){
        Log.d("tag", "signIn");

        if(checkInternetOn()) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }else{
            noInternet();
        }
    }

    public void logOut(){
        Log.d("tag", "logOut");
        if(mGoogleApiClient.isConnected()) {
            Log.d("tag", "logOut mGoogleApiClient isConnected");
            mDatabase.child("users").child(emailReplaceInvaid(userEmail)).child("ifLoggedIn").setValue(false);

            FirebaseAuth.getInstance().signOut();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("tag", "onActivityResult" );
        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if(result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
            else
                Log.d("tag", "Google Login Failed");

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        Log.d("tag", "firebaseAuthWithGoogle" );
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("tag", "signInWithCredential:oncomplete: " + task.isSuccessful());

                        checkIfToCreatuser();

                        }
                    });
    }

    public void checkIfToCreatuser(){
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("tag", "Lkollar match med databas");
                for (DataSnapshot snap: dataSnapshot.child("users").getChildren()) {
                    Person user = snap.getValue(Person.class);
                    String userId = user.getPersonId();

                    if(userId.equals(userUID)){
                        //goToMain();
                        Log.d("tag", "onDataChange: händer dettta ------------>>>>>");
                        //mDatabase.child("users").child(emailReplaceInvaid(userEmail)).child("ifLoggedIn").setValue(true);
                        goToCreat();
                        break;
                    }else {
                        goToCreat();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void goToCreat(){
        Intent intent = new Intent(this, CreateUser.class);
        String emailValid = emailReplaceInvaid(userEmail);
        Log.d("tag", "goToCreat: emailValid " + emailValid);
        intent.putExtra(EXTRA_MESSAGE, emailValid);

        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        thisUserID = sharedPreferences.getString(USER_ID_KEY, null);
        thisUserID = emailValid;


        startActivity(intent);
    }

    public String emailReplaceInvaid(String email){

        String emailValid = email.replace(".", "%1%")
                .replace("#", "%2%")
                .replace("$", "%3%")
                .replace("[", "%4%")
                .replace("]", "%5%");

        return emailValid;
    }

    public boolean checkInternetOn(){

        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    protected void noInternet() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_no_internet)
                //.setTitle("Ingeintenet hittat!")
                .setCancelable(false)
                /*.setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                                startActivity(i);
                            }
                        }
                )*/
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("tag", "onConnected" );
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("tag", "onConnectionFailed" );
    }
}
