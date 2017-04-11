package com.emmasoderstrom.caround2;

import android.content.Intent;
import android.support.annotation.NonNull;
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

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    String userEmail;
    String userUID;
    public static final String EXTRA_MESSAGE = "com.emmasoderstrom.caround2.MESSAGE";

    String thisPersonPhoneId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar =(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setTitle("C around");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();



        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null) {
                    Log.d("AUTH", "user logged in: " + user.getEmail());
                    userEmail = user.getEmail();
                    userUID = user.getUid();

                }else {
                    Log.d("AUTH", "user logged out.");
                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        //findViewById(R.id.sign_out_button).setOnClickListener(this);










        // kolla om användaen har konto och är inloggad

/*        thisPersonPhoneId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("tag", "Login händer detta");
                for (DataSnapshot snap: dataSnapshot.child("users").getChildren()) {
                    Person user = snap.getValue(Person.class);
                    String userId = user.getPersonId();

                    if(userId.equals(thisPersonPhoneId)){

                        if(user.getSignedIn()) {
                            goToMain(getWindow().getDecorView().getRootView());
                            break;
                        }else{
                            Button loginButton = (Button)findViewById(R.id.login);
                            Button creatButton = (Button)findViewById(R.id.creat);
                            loginButton.setVisibility(View.VISIBLE);
                            creatButton.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

    }





    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

    private void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("tag", "Connection failed.");
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    public void checkIfToCreatuser(){
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("tag", "Lkollar match med databas");
                for (DataSnapshot snap: dataSnapshot.child("users").getChildren()) {
                    Person user = snap.getValue(Person.class);
                    String userId = user.getPersonId();

                    /*if(userId.equals(userUID)){
                        goToMain();
                    }else {*/
                        goToCreat();
                    /*}*/
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
        startActivity(intent);
    }

    public void goToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goToInfo(View view){
        FirebaseAuth.getInstance().signOut();
        /*if(Auth.GoogleSignInApi){

        }*/
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        //mAuth.signOut();
    }

    public String emailReplaceInvaid(String email){

        String emailValid = email.replace(".", "%1%")
                .replace("#", "%2%")
                .replace("$", "%3%")
                .replace("[", "%4%")
                .replace("]", "%5%");

        return emailValid;
    }
}
