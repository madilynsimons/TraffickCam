package com.simons.owner.traffickcam;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.parameter.LensPosition;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.photo.BitmapPhoto;
import io.fotoapparat.result.PendingResult;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.view.CameraView;

import static io.fotoapparat.parameter.selector.FlashSelectors.autoFlash;
import static io.fotoapparat.parameter.selector.FlashSelectors.autoRedEye;
import static io.fotoapparat.parameter.selector.FlashSelectors.off;
import static io.fotoapparat.parameter.selector.FlashSelectors.torch;
import static io.fotoapparat.parameter.selector.FocusModeSelectors.autoFocus;
import static io.fotoapparat.parameter.selector.FocusModeSelectors.continuousFocus;
import static io.fotoapparat.parameter.selector.FocusModeSelectors.fixed;
import static io.fotoapparat.parameter.selector.LensPositionSelectors.lensPosition;
import static io.fotoapparat.parameter.selector.Selectors.firstAvailable;
import static io.fotoapparat.parameter.selector.SizeSelectors.biggestSize;

/**
 * FotoApparat repo:
 * https://github.com/Fotoapparat/Fotoapparat
 */

public class TraffickCamFotoActivity extends AppCompatActivity {

    private final int CAMERA_PERMISSION_CODE = 10;

    private boolean hasCameraPermission; // whether or not user has granted permissions
    private CameraView cameraView; // camera view -- how the user sees what the camera sees
    private Fotoapparat fotoapparat; // open source camera

    private int photoID;

    public Object activity = this;

    public ImageView imageView;

    AlertDialog dialog;
    String[] hotelItems = {"Bed", "Lamp", "Wall Art", "Chair",
            "TV", "Window", "Door"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.simons.owner.traffickcam.R.layout.activity_traffick_cam_foto);

        photoID = 0;

        // TODO -- Camera permission is absolutely necessary to proceed
        // make sure that the app actually has permission to use the camera
        // and make granting permission more user friendly
        getCameraPermission();

        cameraView = (CameraView) findViewById(com.simons.owner.traffickcam.R.id.camera_view);
        cameraView.setVisibility(View.VISIBLE);

        fotoapparat = initFotoapparat();
        makeDialogue();
      //  imageView = (ImageView) dialog.findViewById(R.id.dialog_imageview);
        //imageView.setImageResource(R.drawable.genhotel);



    }

    private void makeDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater factory = LayoutInflater.from(TraffickCamFotoActivity.this);
        View view = factory.inflate(R.layout.alert_dialogue, null);

        imageView = (ImageView) view.findViewById(R.id.dialog_imageview);

        builder.setTitle("Select items in this photo.")
                .setView(view)
                .setPositiveButton("FINISH", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO
                    }
                })
                .setNegativeButton("CONTINUE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO
                        // clear the selected checkboxes
                    }
                })
                /*
                .setMultiChoiceItems(hotelItems, null,
                        new DialogInterface.OnMultiChoiceClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                // TODO
                            }
                        })
        */;

        dialog = builder.create();

        // TODO change color
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    public void ShowLabels()
    {
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (hasCameraPermission) {
            fotoapparat.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (hasCameraPermission) {
            fotoapparat.stop();
        }
    }

    /*
     *  Requests permission to use the phone camera from the user
     *  Whether or not the app has permission to the camera is stored in bool hasCameraPermission
     */
    private void getCameraPermission()
    {
        // request permission to use camera
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_CODE
        );

        // check to see whether or not camera permission has been granted
        int permissionCheck = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        );

        // if TraffickCam has permission to use the user's camera, hasCameraPermission is true
        hasCameraPermission = (permissionCheck == PackageManager.PERMISSION_GRANTED);
    }

    // Sets up fotoapparat
    // see FotoApparat docs to further customize
    private Fotoapparat initFotoapparat() {
        return Fotoapparat
                .with(this)
                .into(cameraView)
                .previewScaleType(ScaleType.CENTER_CROP)
                .photoSize(biggestSize())
                .lensPosition(lensPosition(LensPosition.BACK))
                .focusMode(firstAvailable(
                        continuousFocus(),
                        autoFocus(),
                        fixed()
                ))
                .flash(firstAvailable(
                        autoRedEye(),
                        autoFlash(),
                        torch(),
                        off()
                ))
                .build();
    }

    /**
     *  takePictureOnClick will take a picture whenever the user touches the screen
     *  As of right now, the photo taken saves to a generic file path
     *  TODO -- save photo to correct file path
     *  If there are more subjects left to be iterated through, the next instruction is printed
     */
    public void takePictureOnClick(View view)
    {
        PhotoResult photoResult = fotoapparat.takePicture(); // takes photo
        savePicture(photoResult); // saves photo

        // When the photo is available,
        // if there are more subjects to take pictures of, the app will tell the user to do so
        // else, the next activity is triggered
        photoResult
                .toBitmap()
                .whenAvailable(new PendingResult.Callback<BitmapPhoto>() {
                    @Override
                    public void onResult(BitmapPhoto result) {
                        //TODO
                       // sendHotelPhoto(result.bitmap);
                       // Intent labelPicture = new Intent((Context) activity, LabelPicture3Acitivity.class);
                        //labelPicture.putExtra("PHOTOBYTES", bs.toByteArray());

                        imageView.setImageBitmap(result.bitmap);
                        imageView.setRotation(-result.rotationDegrees);



                        ShowLabels();

                       // startActivity(labelPicture);
                    }
                });

    }

    private void sendHotelPhoto(Bitmap b)
    {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 50, bs);

        Intent labelPicture = new Intent((Context) activity, LabelPicture3Acitivity.class);
        labelPicture.putExtra("PHOTOBYTES", bs.toByteArray());

        startActivity(labelPicture);

    }

    // Saves PhotoResult photoResult to a generic file
    // TODO -- get proper final directory
    private void savePicture(PhotoResult photoResult)
    {
        String path = getExternalFilesDir("photos") + "/" + (photoID++) + ".jpg";
        File photoFile = new File(path);
        photoResult.saveToFile(photoFile);
    }

    // Changes to next activity
    protected void exit()
    {
        Intent exit = new Intent(this, HotelListActivity.class);
        startActivity(exit);
        finish(); // TODO: test this
    }


}
