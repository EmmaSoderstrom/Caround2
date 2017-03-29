package com.emmasoderstrom.caround2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

public class FriendHandler extends AppCompatActivity {

    TabHost host;
    Toolbar toolbar;

    public FriendHandler(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_handler);

        setToolbar();

        host = (TabHost)findViewById(R.id.tabHost);
        host.setup();

        //TabWidget tabWidget = (TabWidget)findViewById(R.id.tabs);
//        final TextView tv = (TextView) tabWidget.getChildAt(0).findViewById(android.R.id.tabs);
//        tv.setTextColor(this.getResources().getColorStateList(R.color.text_tab_indicator));

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Tab One");
        spec.setContent(R.id.tab_friend);
        String tabFriend = getString(R.string.friend_tab_fri);
        spec.setIndicator(tabFriend);
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Tab Two");
        spec.setContent(R.id.tab_friend_request);
        spec.setIndicator(getString(R.string.friend_tab_friReq));
        host.addTab(spec);
    }

    /**
     * Sätter toolbar
     */
    public void setToolbar(){
        toolbar =(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Vänner");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addFriendRequest(View view){
        Log.d("tag", "addFriendRequest: ");
    }

}
