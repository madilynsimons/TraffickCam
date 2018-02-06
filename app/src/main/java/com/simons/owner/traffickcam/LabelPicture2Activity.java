package com.simons.owner.traffickcam;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.simons.owner.traffickcam.R;

public class LabelPicture2Activity extends AppCompatActivity {

    private ImageView imageView;
    AlertDialog dialog;
    String[] hotelItems = {"Bed", "Lamp", "Wall Art", "Chair",
    "TV", "Window", "Door"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_picture2);

        imageView = (ImageView) findViewById(R.id.imageView);
        Bitmap hotelPicture = BitmapFactory.decodeResource(getResources(), R.drawable.genhotel);
        imageView.setImageBitmap(hotelPicture);

        makeDialogue();
    }

    private void makeDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select items in this photo.")
            .setPositiveButton("FINISH", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO
            }
        })
            .setNegativeButton("CONTINUE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // TODO
            }
        })
         .setMultiChoiceItems(hotelItems, null,
                 new DialogInterface.OnMultiChoiceClickListener()
                 {
                     @Override
                     public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                // TODO
                     }
                 });


        dialog = builder.create();
        // TODO change color
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    public void ShowLabels(View view)
    {
        dialog.show();
    }



}
