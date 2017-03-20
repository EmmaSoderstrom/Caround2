package com.emmasoderstrom.caround2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar =(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setTitle("C around");
    }

    public void goToMain(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goToInfo(View view){

    }
}
