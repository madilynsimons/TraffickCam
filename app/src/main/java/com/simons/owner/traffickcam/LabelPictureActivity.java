package com.simons.owner.traffickcam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.simons.owner.traffickcam.R;

import java.util.ArrayList;

/**
 * See tutorial:
 * https://www.mkyong.com/android/android-spinner-drop-down-list-example/
 */

public class LabelPictureActivity extends AppCompatActivity {

    private Spinner spinner;
    private ArrayList<String> hotelObjects;

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_picture);
        spinner = (Spinner) findViewById(R.id.spinner);
        fillSpinner();
        imageView = (ImageView) findViewById(R.id.imageView);
        Bitmap hotelPicture = BitmapFactory.decodeResource(getResources(), R.drawable.genhotel);
        imageView.setImageBitmap(hotelPicture);
    }

    private void fillSpinner()
    {
        hotelObjects = new ArrayList<String>();
        hotelObjects.add("Bed");
        hotelObjects.add("Lamp");
        hotelObjects.add("Wall Art");
        hotelObjects.add("Chair");
        hotelObjects.add("TV");
        hotelObjects.add("Window");
        hotelObjects.add("Door");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, hotelObjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
