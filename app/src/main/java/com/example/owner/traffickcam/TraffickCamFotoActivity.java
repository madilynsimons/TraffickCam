package com.example.owner.traffickcam;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.FotoapparatSwitcher;
import io.fotoapparat.error.CameraErrorCallback;
import io.fotoapparat.hardware.CameraException;
import io.fotoapparat.hardware.provider.CameraProviders;
import io.fotoapparat.parameter.LensPosition;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.parameter.update.UpdateRequest;
import io.fotoapparat.photo.BitmapPhoto;
import io.fotoapparat.preview.Frame;
import io.fotoapparat.preview.FrameProcessor;
import io.fotoapparat.result.PendingResult;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.view.CameraView;

import static io.fotoapparat.log.Loggers.fileLogger;
import static io.fotoapparat.log.Loggers.logcat;
import static io.fotoapparat.log.Loggers.loggers;
import static io.fotoapparat.parameter.selector.AspectRatioSelectors.standardRatio;
import static io.fotoapparat.parameter.selector.FlashSelectors.autoFlash;
import static io.fotoapparat.parameter.selector.FlashSelectors.autoRedEye;
import static io.fotoapparat.parameter.selector.FlashSelectors.off;
import static io.fotoapparat.parameter.selector.FlashSelectors.torch;
import static io.fotoapparat.parameter.selector.FocusModeSelectors.autoFocus;
import static io.fotoapparat.parameter.selector.FocusModeSelectors.continuousFocus;
import static io.fotoapparat.parameter.selector.FocusModeSelectors.fixed;
import static io.fotoapparat.parameter.selector.LensPositionSelectors.back;
import static io.fotoapparat.parameter.selector.LensPositionSelectors.lensPosition;
import static io.fotoapparat.parameter.selector.PreviewFpsRangeSelectors.rangeWithHighestFps;
import static io.fotoapparat.parameter.selector.Selectors.firstAvailable;
import static io.fotoapparat.parameter.selector.SensorSensitivitySelectors.highestSensorSensitivity;
import static io.fotoapparat.parameter.selector.SizeSelectors.biggestSize;
import static io.fotoapparat.result.transformer.SizeTransformers.scaled;

public class TraffickCamFotoActivity extends AppCompatActivity {

    private final PermissionsDelegate permissionsDelegate = new PermissionsDelegate(this);
    private boolean hasCameraPermission;
    private CameraView cameraView;

    private FotoapparatSwitcher fotoapparatSwitcher;
    private Fotoapparat fotoapparat;

    protected List<String> s = Arrays.asList(
            "window", "bed", "TV", "bathroom"
    );
    protected ListIterator<String> subjects;
    protected String currentSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffick_cam_foto);

        subjects = s.listIterator();

        cameraView = (CameraView) findViewById(R.id.camera_view);
        hasCameraPermission = permissionsDelegate.hasCameraPermission();

        if (hasCameraPermission) {
            cameraView.setVisibility(View.VISIBLE);
        } else {
            permissionsDelegate.requestCameraPermission();
        }

        setupFotoapparat();

        focusOnLongClick();
        toggleTorchOnSwitch();

        printText();
    }

    protected void printText()
    {
        currentSubject = subjects.next();
        Toast.makeText(this, getString(R.string.take_pic_message) + " " + currentSubject + ".", Toast.LENGTH_LONG).show();
    }

    protected void printText(String text)
    {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private void setupFotoapparat() {
        fotoapparat = createFotoapparat(LensPosition.BACK);
        fotoapparatSwitcher = FotoapparatSwitcher.withDefault(fotoapparat);
    }

    private void toggleTorchOnSwitch() {
        SwitchCompat torchSwitch = (SwitchCompat) findViewById(R.id.torchSwitch);

        torchSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fotoapparatSwitcher
                        .getCurrentFotoapparat()
                        .updateParameters(
                                UpdateRequest.builder()
                                        .flash(
                                                isChecked
                                                        ? torch()
                                                        : off()
                                        )
                                        .build()
                        );
            }
        });
    }

    private void focusOnLongClick() {
        cameraView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                fotoapparatSwitcher.getCurrentFotoapparat().autoFocus();

                return true;
            }
        });
    }

    public void takePictureOnClick(View view)
    {
        takePicture();

        if(subjects.hasNext() == false) exit();
        else printText();

    }

    private Fotoapparat createFotoapparat(LensPosition position) {
        return Fotoapparat
                .with(this)
                .cameraProvider(CameraProviders.v1()) // change this to v2 to test Camera2 API
                .into(cameraView)
                .previewScaleType(ScaleType.CENTER_CROP)
                .photoSize(standardRatio(biggestSize()))
                .lensPosition(lensPosition(position))
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
                .previewFpsRange(rangeWithHighestFps())
                .sensorSensitivity(highestSensorSensitivity())
                .frameProcessor(new TraffickCamFotoActivity.SampleFrameProcessor())
                .logger(loggers(
                        logcat(),
                        fileLogger(this)
                ))
                .cameraErrorCallback(new CameraErrorCallback() {
                    @Override
                    public void onError(CameraException e) {
                        Toast.makeText(TraffickCamFotoActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    }
                })
                .build();
    }

    private void takePicture() {

        PhotoResult photoResult = fotoapparat.takePicture();

        String path = getExternalFilesDir("photos") + "/" + currentSubject + ".jpg";

        File photoFile = new File(path);

        photoResult.saveToFile(photoFile);

        printText("Photo saved to file " + path);

        /**

        photoResult
                .toBitmap()
                .whenAvailable(new PendingResult.Callback<BitmapPhoto>() {
                    @Override
                    public void onResult(BitmapPhoto result) {
                        ImageView imageView = (ImageView) findViewById(R.id.result);

                        imageView.setImageBitmap(result.bitmap);
                        imageView.setRotation(-result.rotationDegrees);
                    }
                });
         */

    }

    protected void exit()
    {
        Intent exit = new Intent(this, ExitActivity.class);
        startActivity(exit);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (hasCameraPermission) {
            fotoapparatSwitcher.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (hasCameraPermission) {
            fotoapparatSwitcher.stop();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionsDelegate.resultGranted(requestCode, permissions, grantResults)) {
            fotoapparatSwitcher.start();
            cameraView.setVisibility(View.VISIBLE);
        }
    }

    private class SampleFrameProcessor implements FrameProcessor {

        @Override
        public void processFrame(Frame frame) {
            // Perform frame processing, if needed
        }

    }
}
