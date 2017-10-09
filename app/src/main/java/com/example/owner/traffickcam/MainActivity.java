package com.example.owner.traffickcam;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    String default_hotel;
    String default_room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        default_hotel = this.getResources().getString(R.string.default_hotel);
        default_room = this.getResources().getString(R.string.default_room);
    }

    /** called when user presses button **/
    public void startCamera(View view)
    {
        EditText rn = (EditText) findViewById(R.id.room_number);
        EditText hn = (EditText) findViewById(R.id.hotel_name);
        String room_number = rn.getText().toString();
        String hotel_name = hn.getText().toString();

        if(room_number.equals(default_room) || hotel_name.equals(default_hotel))
        {
            // TODO
            //  user either failed to put in a hotel or failed to put in a room
        }
        else
        {
            Intent intent = new Intent(this, CameraActivity.class);
            startActivity(intent);
        }
    }

}
