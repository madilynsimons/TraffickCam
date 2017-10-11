package com.example.owner.traffickcam;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    String default_hotel;
    String default_room;
    EditText rn;
    EditText hn;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rn = (EditText) findViewById(R.id.room_number);
        hn = (EditText) findViewById(R.id.hotel_name);

        default_hotel = this.getResources().getString(R.string.default_hotel);
        default_room = this.getResources().getString(R.string.default_room);
    }

    /** called when user presses button **/
    public void startCamera(View view)
    {
        if(! isValidHotel() )
        {
            Toast.makeText(this, R.string.invalid_hotel_input, Toast.LENGTH_LONG).show();
        }
        else
        {
            Intent intent = new Intent(this, TraffickCameraActivity.class);
            startActivity(intent);
        }
    }

    private boolean isValidHotel()
    {
        String room_number = rn.getText().toString();
        String hotel_name = hn.getText().toString();

        if(room_number.equals(default_room) || hotel_name.equals(default_hotel))
        {
            return false;
        }
        return true;
    }


}
