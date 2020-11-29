/*
 * File: 		VerificationActivity.java
 * Created:		2013/05/03
 *
 * copyright (c) 2013 DigitalPersona Inc.
 */

package com.digitalpersona.uareu.UareUSampleJava;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.digitalpersona.uareu.Engine;
import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.Reader.Priority;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;

public class VerificationActivity extends Activity {

    private Button m_back;
    private String m_deviceName = "";

    private String m_enginError;
    private String m_base64;

    private Reader m_reader = null;
    private int m_DPI = 0;
    private Bitmap m_bitmap = null;
    private ImageView m_imgView;
    private TextView m_selectedDevice;
    private TextView m_title;
    private boolean m_reset = false;

    private TextView m_text;
    private TextView m_text_conclusion;
    private String m_textString;
    private String m_text_conclusionString;
    private Engine m_engine = null;
    private Fmd m_fmd = null;
    private int m_score = -1;
    private boolean m_first = false;
    private boolean m_resultAvailableToDisplay = false;
    private Reader.CaptureResult cap_result = null;

    private void initializeActivity() {
        m_title = findViewById(R.id.title);
        m_title.setText("Verification");

        m_enginError = "";

        m_selectedDevice = findViewById(R.id.selected_device);
        m_deviceName = getIntent().getExtras().getString("device_name");

        m_selectedDevice.setText("Device: " + m_deviceName);

        m_imgView = findViewById(R.id.bitmap_image);
        m_bitmap = Globals.GetLastBitmap();
        if (m_bitmap == null)
            m_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.black);
        m_imgView.setImageBitmap(m_bitmap);
        m_back = findViewById(R.id.back);

        m_back.setOnClickListener(v -> onBackPressed());

        m_text = findViewById(R.id.text);
        m_text_conclusion = findViewById(R.id.text_conclusion);
        UpdateGUI();
    }

    private void newFMD() {
        String extDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        String dir = String.format("%s/db/", extDir);
        String fName = "data.txt";
        String fPath = String.format("%s%s", dir, fName);

        try (BufferedInputStream fis = new BufferedInputStream(new FileInputStream(fPath))) {

            // MyFmd myFmd = (MyFmd) ois.readObject();
            // new Utils().log("m_fmd read data length: ", myFmd.getM_data().length);
            // new Utils().log("m_fmd read width: ", myFmd.width);
            // new Utils().log("m_fmd read height: ", myFmd.height);
            // new Utils().log("m_fmd read resolution: ", myFmd.resolution);
            // new Utils().log("m_fmd read cbeffid: ", myFmd.cbeffid);
            // new Utils().log("m_fmd read cbeffid: ", myFmd.getM_format());


            // m_fmd = m_engine.CreateFmd(myFmd.getM_data(), myFmd.getWidth(), myFmd.getHeight(), myFmd.getResolution(), 0, myFmd.getCbeffid(), myFmd.getM_format());

            Boolean success = m_fmd != null;

            new Utils().log("m_fmd read: ", success);

            // ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read() {
        String extDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        String dir = String.format("%s/db/", extDir);
        String fPath = dir + "byte.txt";
        String jsonInputString = "";
        String fileName = "finger.json";
        String absolutePath = dir + File.separator + fileName;
        File file = new File(absolutePath);
        byte[] bytes = new byte[(int) file.length()];
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(bytes);
            jsonInputString = new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Utils().log("JSON: %s", jsonInputString);

        File mFile = new File(fPath);
        byte[] mBytes = new byte[(int) mFile.length()];
        try (FileInputStream fis = new FileInputStream(mFile)) {
            fis.read(mBytes);
            String base64 = new String(mBytes);
            new Utils().log(String.format("In base64 was : %s", base64));

            byte[] decodedB64 = Base64.decode(base64, 0);
            new Utils().log("Byte[] length is: %d", decodedB64.length);

            HashMap finger = new ObjectMapper().readValue(jsonInputString, HashMap.class);
            // runOnUiThread(() -> cbe.setText(finger.get("cbe").toString()));
            // cbe.setText(finger.get("cbe").toString());
            // runOnUiThread(() -> res.setText(finger.get("res").toString()));
            // res.setText(finger.get("res").toString());

            int cbe = (int) finger.get("cbe");
            int width = (int) finger.get("width");
            int height = (int) finger.get("height");
            int res = (int) finger.get("res");

            m_fmd = m_engine.CreateFmd(decodedB64, width, height, res, 0, cbe, Fmd.Format.ANSI_378_2004);

            new Utils().log("m_fmd data is: " + Arrays.toString(decodedB64));

            Arrays.toString(decodedB64);
        } catch (IOException | UareUException e) {
            e.printStackTrace();
        }
    }

    private void getFmd() {
        String extDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        String dir = String.format("%s/db/", extDir);

        File mFile = new File(dir, "finger" + ".txt");
        byte[] mBytes = new byte[(int) mFile.length()];
        try (FileInputStream fis = new FileInputStream(mFile)) {
            fis.read(mBytes);
            String base64 = new String(mBytes);
            m_base64 = base64;
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] decompressedBArray = Base64.decode(m_base64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decompressedBArray, 0, decompressedBArray.length);

        Utils utils = new Utils();
        if (bitmap != null) {
            utils.log("Bitmap != null");
        } else {
            utils.log("Bitmap == null, decoding failed");
        }

        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, ba);

        byte[] imageInBytes = ba.toByteArray();

        String fileName = "finger.json";
        String absolutePath = dir + File.separator + fileName;
        String jsonInputString = "";

        File file = new File(absolutePath);
        byte[] bytes = new byte[(int) file.length()];
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(bytes);
            jsonInputString = new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            HashMap<String, Object> finger = new ObjectMapper().readValue(jsonInputString, HashMap.class);
            int cbe = (int) finger.get("cbe");
            int width = (int) finger.get("width");
            int height = (int) finger.get("height");
            int resolution = (int) finger.get("res");
            utils.log("cbe: %d, width: %d, height: %d, res: %d", cbe, width, height, resolution);
            utils.log("BASE64: ", m_base64);

            m_fmd = m_engine.CreateFmd(imageInBytes, width, height, resolution, 0, cbe, Fmd.Format.ANSI_378_2004);

            if (m_fmd.getData() != null) {
                if (m_fmd.getData() == imageInBytes) {
                    utils.log("FMD data: %d", m_fmd.getData());
                }
            }

            utils.log("From FMD: %d", m_fmd.getCbeffId());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (UareUException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engine);
        m_textString = "Place any finger on the reader";

        initializeActivity();

        // initialise dp sdk
        try {
            Context appContext = getApplicationContext();
            m_reader = Globals.getInstance().getReader(m_deviceName, appContext);
            m_reader.Open(Priority.EXCLUSIVE);
            m_DPI = Globals.GetFirstDPI(m_reader);
            m_engine = UareUGlobal.GetEngine();
        } catch (Exception e) {
            Log.w("UareUSampleJava", "error during init of reader");
            m_deviceName = "";
            onBackPressed();
            return;
        }


        // loop capture on a separate thread to avoid freezing the UI
        new Thread(() -> {
            read();

            m_reset = false;
            while (!m_reset) {
                try {
                    cap_result = m_reader.Capture(Fid.Format.ANSI_381_2004, Globals.DefaultImageProcessing, m_DPI, -1);
                } catch (Exception e) {
                    if (!m_reset) {
                        Log.w("UareUSampleJava", "error during capture: " + e.toString());
                        m_deviceName = "";
                        onBackPressed();
                    }
                }

                m_resultAvailableToDisplay = false;

                // an error occurred
                if (cap_result == null || cap_result.image == null) continue;

                try {
                    m_enginError = "";

                    // save bitmap image locally
                    m_bitmap = Globals.GetBitmapFromRaw(cap_result.image.getViews()[0].getImageData(), cap_result.image.getViews()[0].getWidth(), cap_result.image.getViews()[0].getHeight());

                    if (m_fmd == null) {
                        m_fmd = m_engine.CreateFmd(cap_result.image, Fmd.Format.ANSI_378_2004);
                    } else {
                        m_score = m_engine.Compare(m_fmd, 0, m_engine.CreateFmd(cap_result.image, Fmd.Format.ANSI_378_2004), 0);
                        m_fmd = null;
                        m_resultAvailableToDisplay = true;
                    }
                } catch (Exception e) {
                    m_enginError = e.toString();
                    Log.w("UareUSampleJava", "Engine error: " + e.toString());
                }

                m_text_conclusionString = Globals.QualityToString(cap_result);
                if (!m_enginError.isEmpty()) {
                    m_text_conclusionString = "Engine: " + m_enginError;
                } else if (m_fmd == null) {
                    if ((!m_first) && (m_resultAvailableToDisplay)) {
                        if (m_text_conclusionString.length() == 0) {
                            DecimalFormat formatting = new DecimalFormat("##.######");
                            m_text_conclusionString = "Dissimilarity Score: " + m_score + ", False match rate: " + Double.valueOf(formatting.format((double) m_score / 0x7FFFFFFF)) + " (" + (m_score < (0x7FFFFFFF / 100000) ? "match" : "no match") + ")";
                        }
                    }

                    m_textString = "Place any finger on the reader";
                } else {
                    m_first = false;
                    m_textString = "Place the same or a different finger on the reader";
                }

                runOnUiThread(VerificationActivity.this::UpdateGUI);
            }
        }).start();
    }

    public void UpdateGUI() {
        m_imgView.setImageBitmap(m_bitmap);
        m_imgView.invalidate();
        m_text_conclusion.setText(m_text_conclusionString);
        m_text.setText(m_textString);
    }


    @Override
    public void onBackPressed() {
        try {
            m_reset = true;
            try {
                m_reader.CancelCapture();
            } catch (Exception e) {
            }
            m_reader.Close();

        } catch (Exception e) {
            Log.w("UareUSampleJava", "error during reader shutdown");
        }

        Intent i = new Intent();
        i.putExtra("device_name", m_deviceName);
        setResult(Activity.RESULT_OK, i);
        finish();
    }

    // called when orientation has changed to manually destroy and recreate activity
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_engine);
        initializeActivity();
    }
}
