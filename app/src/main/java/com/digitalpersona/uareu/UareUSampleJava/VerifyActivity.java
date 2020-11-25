package com.digitalpersona.uareu.UareUSampleJava;

import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;

public class VerifyActivity extends AppCompatActivity {

    ImageView saved, newImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_verify);

        saved = findViewById(R.id.image_saved);

        newImage = findViewById(R.id.image_new);

        String directory = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "UareU";
        File imageFile = new File(directory, "finger" + ".png");
        saved.setImageDrawable(Drawable.createFromPath(imageFile.toString()));
    }
}