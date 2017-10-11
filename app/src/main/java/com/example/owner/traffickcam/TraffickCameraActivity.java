package com.example.owner.traffickcam;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

@SuppressWarnings("deprecation")
public class TraffickCameraActivity extends CameraActivity {

    private static final String TAG = "TraffickCameraActivity";

    protected List<String> s = Arrays.asList(
            " window", " bed", " TV", " bathroom"
    );
    protected ListIterator<String> subjects = s.listIterator();

    @Override
    protected void printText()
    {
        Toast.makeText(ctx, getString(R.string.take_pic_message) + subjects.next() + ".", Toast.LENGTH_LONG).show();
    }

    protected void printText(String text)
    {
        Toast.makeText(ctx, text, Toast.LENGTH_LONG).show();
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            camera.takePicture(shutterCallback, rawCallback, jpegCallback);
            if(subjects.hasNext()) printText();
            else exit();
        }
    };

    protected void exit()
    {
        Intent exit = new Intent(this, ExitActivity.class);
        startActivity(exit);
    }



}
