package com.digitalpersona.uareu.UareUSampleJava;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class ShowActivity extends AppCompatActivity {
    TextView cbe, res, base;
    ImageView imageView;
    String imageInBase64 = Globals.base64;
    Utils utils = new Utils();

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_show);

        cbe = findViewById(R.id.cbe);
        res = findViewById(R.id.resolution);
        base = findViewById(R.id.base);
        imageView = findViewById(R.id.image_view);

        String extDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        String dir = String.format("%s/UareU/", extDir);
        String fileName = "finger.json";
        String absolutePath = dir + File.separator + fileName;
        String jsonInputString = "";

/*        try (FileInputStream is = new FileInputStream(new File(absolutePath))) {
            BufferedReader r = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = r.readLine()) != null) {
                if (line.length() > 0) jsonInputString.append(line);
            }
            r.close();
        } catch (IOException e) {
            e.printStackTrace();
            utils.log("File reading failed !");
        }*/

        File file = new File(absolutePath);
        byte[] bytes = new byte[(int) file.length()];
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(bytes);
            jsonInputString = new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        utils.log("JSON: %s", jsonInputString);

        String directory = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "UareU";
        File imageFile = new File(directory, "finger" + ".png");
        imageView.setImageDrawable(Drawable.createFromPath(imageFile.toString()));

        try {
            HashMap<String, Object> finger = new ObjectMapper().readValue(jsonInputString, HashMap.class);
            cbe.setText((String) finger.get("cbe"));
            res.setText((String) finger.get("res"));

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String base64 = "";
        File mFile = new File(dir, "finger" + ".txt");
        byte[] mBytes = new byte[(int) mFile.length()];
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(mBytes);
            base64 = new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        base.setText(base64);

//        try {
//            byte[] decompressedBArray = Base64.decode(imageInBase64, Base64.DEFAULT);
//
//            Bitmap bitmap = BitmapFactory.decodeByteArray(decompressedBArray, 0, decompressedBArray.length);
//
//            if (bitmap != null) {
//                utils.log("Image height: %d", bitmap.getHeight());
//                utils.log("Image width: %d", bitmap.getWidth());
//
//                imageView.setImageBitmap(bitmap);
//            } else {
//                utils.log("Bitmap could not be decoded !");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}