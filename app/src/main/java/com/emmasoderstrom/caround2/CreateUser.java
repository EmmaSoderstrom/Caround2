package com.emmasoderstrom.caround2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class CreateUser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
    }

    public void creatUserDone(View view) {

        TextView firstName = (TextView)findViewById(R.id.creat_user_firstname);
        TextView lastName = (TextView)findViewById(R.id.creat_user_lastname);
        TextView telNumber = (TextView)findViewById(R.id.creat_user_telnumber);


        if (firstName != null && lastName != null && telNumber != null){

            Intent intent = new Intent(this, MainActivity.class);
            //intent.putExtra()
            startActivity(intent);
        }

    }
}
