package com.emmasoderstrom.caround2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static android.R.attr.data;

public class CreateUser extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    final static String EXTRA_MESSAGE = "com.emmasoderstrom.caround2.MESSAGE";

    private DatabaseReference mDatabase;

    private static int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    String thisUserID;

    ImageView userImeageView;
    Uri userPic;
    Bitmap userBitmap;

    EditText firstName;
    EditText lastName;
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

        Log.d("tag", "onCreate: getProviderId " + user.getProviderId());
        Log.d("tag", "onCreate: getProviderId " + user.getUid());
        Log.d("tag", "onCreate: getProviderId " + user.getDisplayName());
        Log.d("tag", "onCreate: getProviderId " + user.getProviderId());

        if (user != null) {
            userPic = user.getPhotoUrl();
            String userPicS = userPic.toString();

            userImeageView = (ImageView)findViewById(R.id.creat_user_pic);
            new DownloadImage().execute(userPicS);
        }


        InputFilter[] textLenght= new InputFilter[1];
        textLenght[0] = new InputFilter.LengthFilter(20);
        InputFilter[] phoneNumberLenght= new InputFilter[1];
        phoneNumberLenght[0] = new InputFilter.LengthFilter(10);

        firstName = (EditText)findViewById(R.id.creat_user_firstname);
        firstName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        firstName.setFilters(textLenght);

        lastName = (EditText)findViewById(R.id.creat_user_lastname);
        lastName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        lastName.setFilters(textLenght);

        telNumber = (EditText)findViewById(R.id.creat_user_telnumber);
        telNumber.setInputType(InputType.TYPE_CLASS_PHONE);
        telNumber.setFilters(phoneNumberLenght);


        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snap: dataSnapshot.child("users").getChildren()) {
                    Person user = snap.getValue(Person.class);
                    String userId = user.getPersonId();

                    if(userId.equals(thisUserID)){
                        firstName.setText(user.getFirstName());
                        lastName.setText(user.getLastName());
                        telNumber.setText(user.getPhoneNumber());

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

            //Skapa och lagra denna nya användare i databasen

            //conventerar bil UIR till sträng så databasen kan ta imot den
            String picString = userPic.toString();

            Person personA = new Person(thisUserID, picString, firstName.getText().toString(), lastName.getText().toString(),telNumber.getText().toString(), 6000);
            mDatabase.child("users").child(thisUserID).setValue(personA);

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
}
