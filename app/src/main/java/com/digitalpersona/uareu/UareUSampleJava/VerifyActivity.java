package com.digitalpersona.uareu.UareUSampleJava;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

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

        String extDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        String dir = String.format("%s/db/", extDir);
        String fName = "byte.txt";
        String fPath = String.format("%s%s", dir, fName);
        File fFile = new File(fPath);

        byte[] data = new byte[(int) fFile.length()];
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fPath))) {
            bis.read(data, 0, (int) fFile.length());
            new Utils().log("Byte length:", data.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        read(fPath);
    }

    private static void read(String fPath) {
        File mFile = new File(fPath);
        byte[] mBytes = new byte[(int) mFile.length()];
        try (FileInputStream fis = new FileInputStream(mFile)) {
            fis.read(mBytes);
            String base64 = new String(mBytes);
            new Utils().log(String.format("In base64 was : %s", base64));

            byte[] decodedB64 = Base64.decode(base64, 0);
            new Utils().log("Byte[] length is: %d", decodedB64.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}