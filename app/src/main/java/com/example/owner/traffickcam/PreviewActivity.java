package com.example.owner.traffickcam;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;

import io.fotoapparat.photo.BitmapPhoto;
import io.fotoapparat.result.PendingResult;
import io.fotoapparat.result.PhotoResult;

import static io.fotoapparat.result.transformer.SizeTransformers.scaled;

public class PreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        Toast.makeText(this, "what's up", Toast.LENGTH_LONG).show();

        Bitmap bitmap = null;
        String filePath = null;
        Bundle b = this.getIntent().getExtras();

        if(b!=null)
        {
            filePath = (String) b.getSerializable("PHOTO_FILE");
            bitmap = getBitmap(filePath);

            ImageView imageView = (ImageView) findViewById(R.id.result);
            imageView.setImageBitmap(bitmap);
            imageView.setRotation(90);
        }

    }

    public Bitmap getBitmap(String path)
    {
        try {
            Bitmap bitmap=null;
            File f= new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();

            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            return bitmap;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
