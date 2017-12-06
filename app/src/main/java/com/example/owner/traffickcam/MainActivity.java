package com.example.owner.traffickcam;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       //Intent intent = new Intent(this, TraffickCamFotoActivity.class);
       Intent intent = new Intent(this, HotelListActivity.class);
        startActivity(intent);
    }

}
