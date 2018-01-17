package com.example.owner.traffickcam;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    /**
     *  Application opens and runs this (MainActivity) first.  As of now, MainActivity just
     *  immediately called TraffickCamFotoActivity, the activity that actually uses the camera
     *  and takes photos of a hotel room.
     *  TODO -- Have the application open right to TraffickCamFotoActivity
     *  Opening MainActivity is unnecessary
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, TraffickCamFotoActivity.class);
        startActivity(intent);
    }

}
