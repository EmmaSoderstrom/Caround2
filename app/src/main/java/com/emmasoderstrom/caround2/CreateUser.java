package com.emmasoderstrom.caround2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateUser extends AppCompatActivity {

    //public final static Person EXTRA_MESSAGE = "com.emmasoderstrom.caround2.MESSAGE";
    private DatabaseReference mDatabase;
    String personId;
    TextView firstName;
    TextView lastName;
    TextView telNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        personId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        firstName = (TextView)findViewById(R.id.creat_user_firstname);
        lastName = (TextView)findViewById(R.id.creat_user_lastname);
        telNumber = (TextView)findViewById(R.id.creat_user_telnumber);


        TelephonyManager telManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = telManager.getLine1Number();
        Log.d("tag", "telefon nummer " + mPhoneNumber);

        if(!mPhoneNumber.isEmpty() && mPhoneNumber != null){
            Log.d("tag", "telefon nummer inte empty eller null" + mPhoneNumber);
            telNumber.setText(mPhoneNumber);
        }
    }

    public void creatUserDone(View view) {
        Log.d("tag", "firstName " + firstName.getText());

        if (!firstName.getText().toString().isEmpty()
                && !lastName.getText().toString().isEmpty()
                && !telNumber.getText().toString().isEmpty()){

            //Skapa och lagra denna nya användare i databasen
            Person personA = new Person(personId, firstName.getText().toString(), lastName.getText().toString(), 5000);
            mDatabase.child("users").child(personId).setValue(personA);

            Intent intent = new Intent(this, MainActivity.class);
            //intent.putExtra(personA);
            startActivity(intent);
        }

    }
}
