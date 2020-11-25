package com.digitalpersona.uareu.UareUSampleJava;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public class ShowActivity extends AppCompatActivity {
    TextView cbe, res, base;
    ImageView imageView;
    String imageInBase64;
    Utils utils = new Utils();

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

        File mFile = new File(dir, "finger" + ".txt");
        byte[] mBytes = new byte[(int) mFile.length()];
        try (FileInputStream fis = new FileInputStream(mFile)) {
            fis.read(mBytes);
            String base64 = new String(mBytes);
            imageInBase64 = base64;
            base.setText(base64);

        } catch (IOException e) {
            e.printStackTrace();
        }

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
        // runOnUiThread(() -> imageView.setImageDrawable(Drawable.createFromPath(imageFile.toString())));
        imageView.setImageDrawable(Drawable.createFromPath(imageFile.toString()));

        try {
            HashMap<String, Object> finger = new ObjectMapper().readValue(jsonInputString, HashMap.class);
            // runOnUiThread(() -> cbe.setText(finger.get("cbe").toString()));
            cbe.setText(finger.get("cbe").toString());
            // runOnUiThread(() -> res.setText(finger.get("res").toString()));
            res.setText(finger.get("res").toString());

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String extDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            String dir = String.format("%s/UareU/", extDir);

            File mFile = new File(dir, "finger" + ".txt");
            byte[] mBytes = new byte[(int) mFile.length()];
            try (FileInputStream fis = new FileInputStream(mFile)) {
                fis.read(mBytes);
                String base64 = new String(mBytes);
                imageInBase64 = base64;
                base.setText(base64);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return imageInBase64;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            utils.log("BASE64: ", result);
        }
    }
}








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