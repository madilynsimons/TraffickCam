package com.simons.owner.traffickcam;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.simons.owner.traffickcam.R;

public class LabelPicture3Acitivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_picture3_acitivity);
/*
        imageView = (ImageView) findViewById(R.id.result);
        //Bitmap hotelPicture = BitmapFactory.decodeResource(getResources(), R.drawable.genhotel);
        Bitmap hotelPicture = BitmapFactory.decodeByteArray(
                getIntent().getByteArrayExtra("PHOTOBYTES"),0,getIntent().getByteArrayExtra("PHOTOBYTES").length
        );
        imageView.setImageBitmap(hotelPicture);
        */

    }

    public void continueOnClick(View view)
    {
        finish();
    }

    public void finishOnClick(View view)
    {
        Intent exit = new Intent(this, HotelListActivity.class);
        startActivity(exit);
        finish();
    }

}
