package com.emmasoderstrom.caround2;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private DatabaseReference mDatabase;
    String thisPersonPhoneId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar =(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setTitle("C around");

        // kolla om användaen har konto och är inloggad
        mDatabase = FirebaseDatabase.getInstance().getReference();
        thisPersonPhoneId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
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
        });

    }

    public void goToCreat(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goToMain(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goToInfo(View view){

    }
}
