package com.example.owner.traffickcam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HotelInfoActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_info);
        String hotel_name = getIntent().getStringExtra("HOTEL");
        textView = (TextView) findViewById(R.id.textView2);
        textView.setText(hotel_name);
    }

    public void exitOnClick(View view)
    {
        Intent intent = new Intent(this, ExitActivity.class);
        startActivity(intent);
    }
}
