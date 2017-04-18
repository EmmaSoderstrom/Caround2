package com.emmasoderstrom.caround2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class CreateUser extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    final static String EXTRA_MESSAGE = "com.emmasoderstrom.caround2.MESSAGE";

    private DatabaseReference mDatabase;

    private static int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    String thisUserID;
    Person thisUser;
    String userPicS;

    ImageView userImeageView;
    Uri userPic;
    Bitmap userBitmap;

    EditText firstName;
    EditText lastName;
    Spinner spinnerCountry;
    EditText telNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        Intent intent = getIntent();
        String message = intent.getStringExtra(Login.EXTRA_MESSAGE);
        thisUserID = message;

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();




        //hämtar google bild och sätter användar bild
        if (user != null) {
            userPic = user.getPhotoUrl();
            userPicS = userPic.toString();

            userImeageView = (ImageView)findViewById(R.id.creat_user_pic);
            new DownloadImage().execute(userPicS);
        }


        InputFilter[] textLenght= new InputFilter[1];
        textLenght[0] = new InputFilter.LengthFilter(15);
        InputFilter[] phoneNumberLenght= new InputFilter[1];
        phoneNumberLenght[0] = new InputFilter.LengthFilter(10);

        firstName = (EditText)findViewById(R.id.creat_user_firstname);
        firstName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        firstName.setFilters(textLenght);

        lastName = (EditText)findViewById(R.id.creat_user_lastname);
        lastName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        lastName.setFilters(textLenght);
        // TODO: 2017-04-11 ta bort mellanslag

        setContryChoses();

        telNumber = (EditText)findViewById(R.id.creat_user_telnumber);
        telNumber.setInputType(InputType.TYPE_CLASS_PHONE);
        telNumber.setFilters(phoneNumberLenght);


        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //kollar om det finns användare redan
                for (DataSnapshot snap: dataSnapshot.child("users").getChildren()) {
                    Person user = snap.getValue(Person.class);
                    String userId = user.getPersonId();

                    if(userId.equals(thisUserID)){
                        firstName.setText(user.getFirstName());
                        lastName.setText(user.getLastName());
                        telNumber.setText(user.getPhoneNumber());
                        thisUser = user;

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        /*TelephonyManager telManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = telManager.getLine1Number();
        Log.d("tag", "telefon nummer Null " + mPhoneNumber);

        if(!mPhoneNumber.isEmpty() && mPhoneNumber != null){
            Log.d("tag", "telefon nummer inte empty eller null" + mPhoneNumber);
            telNumber.setText(mPhoneNumber);
        }*/
    }

    public void creatUserDone(View view) {
        Log.d("tag", "firstName " + firstName.getText());

        if (!firstName.getText().toString().isEmpty()
                && !lastName.getText().toString().isEmpty()
                && !telNumber.getText().toString().isEmpty()){

            //om använder profil inte finns
            if(thisUser == null) {
                //Skapa och lagra denna nya användare i databasen

                //conventerar bil UIR till sträng så databasen kan ta imot den
                //String picString = userPic.toString();

                Person personA = new Person(userPicS, thisUserID,
                        firstName.getText().toString(), lastName.getText().toString(),
                        spinnerCountry.getSelectedItem().toString(), telNumber.getText().toString(), 6000,
                        null, null, null);

                mDatabase.child("users").child(thisUserID).setValue(personA);

            }
            //om användar profil redan finns
            else{
                mDatabase.child("users").child(thisUserID).child("picString").setValue(userPicS);
                mDatabase.child("users").child(thisUserID).child("firstName").setValue(firstName.getText().toString());
                mDatabase.child("users").child(thisUserID).child("lastName").setValue(lastName.getText().toString());
                mDatabase.child("users").child(thisUserID).child("fullName").setValue(firstName.getText().toString() + lastName.getText().toString());
                mDatabase.child("users").child(thisUserID).child("phoneNumber").setValue(telNumber.getText().toString());
                mDatabase.child("users").child(thisUserID).child("ifLoggedIn").setValue(true);

            }

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(EXTRA_MESSAGE, thisUserID);
            startActivity(intent);

        }
    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // DownloadImage AsyncTask
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        /*@Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(MainActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Download Image Tutorial");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }*/

        @Override
        protected Bitmap doInBackground(String... URL) {

            //String image = userPic.toString();
            String imageURL = URL[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            userBitmap = bitmap;
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            userImeageView.setImageBitmap(result);
            userBitmap = result;
            // Close progressdialog
            //mProgressDialog.dismiss();
        }
    }

    public void setContryChoses(){
        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        String country;
        for( Locale loc : locale ){
            country = loc.getDisplayCountry();
            if( country.length() > 0 && !countries.contains(country) ){
                countries.add( country );
            }
        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);

        spinnerCountry = (Spinner)findViewById(R.id.spinner_country);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, countries);
        spinnerCountry.setAdapter(adapter);

        String phoneCountry = Locale.getDefault().getDisplayCountry();

        for (int i = 0; i < countries.size(); i++) {
            if(countries.get(i).equalsIgnoreCase(phoneCountry)){
                spinnerCountry.setSelection(i);
                break;
            }
        }
    }
}
