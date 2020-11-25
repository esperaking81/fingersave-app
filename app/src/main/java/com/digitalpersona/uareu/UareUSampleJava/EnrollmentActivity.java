/*
 * File: 		EnrollmentActivity.java
 * Created:		2013/05/03
 *
 * copyright (c) 2013 DigitalPersona Inc.
 */

package com.digitalpersona.uareu.UareUSampleJava;

import com.digitalpersona.uareu.Engine;
import com.digitalpersona.uareu.Engine.PreEnrollmentFmd;
import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.Fmd.Format;
import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.Reader.Priority;
import com.digitalpersona.uareu.UareUGlobal;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
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
import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;

public class EnrollmentActivity extends Activity {
    private Button m_back;
    private String m_deviceName = "";

    private String m_enginError;

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
    private String imageBase64;
    private int cbe;
    private int resolution;
    private Engine m_engine = null;
    private int m_current_fmds_count = 0;
    private boolean m_first = true;
    private boolean m_success = false;
    private Fmd m_enrollment_fmd = null;
    private int m_templateSize = 0;
    EnrollmentCallback enrollThread = null;
    private Reader.CaptureResult cap_result = null;

    private void initializeActivity() {
        m_enginError = "";
        m_title = findViewById(R.id.title);
        m_title.setText("Enrollment");
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engine);
        m_textString = "Place any finger on the reader";
        initializeActivity();

        // initiliaze dp sdk
        try {
            Context applContext = getApplicationContext();
            m_reader = Globals.getInstance().getReader(m_deviceName, applContext);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    m_current_fmds_count = 0;
                    m_reset = false;
                    enrollThread = new EnrollmentCallback(m_reader, m_engine);
                    while (!m_reset) {
                        try {
                            m_enrollment_fmd = m_engine.CreateEnrollmentFmd(Format.ISO_19794_2_2005, enrollThread);
                            if (m_success = (m_enrollment_fmd != null)) {
                                m_templateSize = m_enrollment_fmd.getData().length;
                                m_current_fmds_count = 0;    // reset count on success


                                // added ------------------
                                String extDir = Environment.getExternalStorageDirectory().getAbsolutePath();
                                String dir = String.format("%s/UareU/", extDir);
                                Globals.setBase(imageBase64);

                                // save data.txt
                                String base = "finger.json";
                                try {
                                    File myFolder = new File(dir);
                                    if (!myFolder.exists()) {
                                        myFolder.mkdirs();
                                    }

                                    HashMap<String, Object> finger = new HashMap<>();
                                    finger.put("cbe", cbe);
                                    finger.put("res", resolution);
                                    finger.put("height", m_bitmap.getHeight());
                                    finger.put("width", m_bitmap.getWidth());

                                    //  finger.put("image", imageBase64);
                                    ObjectMapper objectMapper = new ObjectMapper();

                                    String absolutePath = String.format("%s%s", dir, base);

                                    String jsonOutputString;

                                    try (FileOutputStream writer = new FileOutputStream(absolutePath)) {
                                        jsonOutputString = objectMapper.writeValueAsString(finger);
                                        writer.write(jsonOutputString.getBytes());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        new Utils().log("Image save failed");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    new Utils().log("Image save failed");
                                }

                                // save finger.png
                                String image = "finger.png";
                                String absPath = String.format("%s%s", dir, image);

                                try {
                                    File myFolder = new File(dir);
                                    if (!myFolder.exists()) {
                                        myFolder.mkdirs();
                                    }

                                    FileOutputStream fos = new FileOutputStream(absPath);
                                    m_bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                                    fos.flush();
                                    fos.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                // save finger.txt
                                String txt = "finger.txt";
                                String path = String.format("%s%s", dir, txt);

                                try (FileOutputStream fos = new FileOutputStream(path)) {
                                    File myFolder = new File(dir);
                                    if (!myFolder.exists()) {
                                        myFolder.mkdirs();
                                    }

                                    fos.write(imageBase64.getBytes(Charset.defaultCharset()));
                                    fos.flush();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                // save finger.
                                String fName = "data.txt";
                                String fPath = String.format("%s%s", dir, fName);
                                MyFMD myFmd = (MyFMD) m_enrollment_fmd;

                                new Utils().log("myFMD check: cbe -> %d, res -> %d", myFmd.getCbeffId(), myFmd.getResolution());

                                try (FileOutputStream fileOut = new FileOutputStream(fPath)) {
                                    // Creates an ObjectOutputStream
                                    ObjectOutputStream objOut = new ObjectOutputStream(fileOut);

                                    // Writes objects to the output stream
                                    objOut.writeObject(myFmd);

                                    new Utils().log("myFMD write: ", true);

                                    objOut.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                // end added -------------------
                            }
                        } catch (Exception e) {
                            // template creation failed, reset count
                            m_current_fmds_count = 0;
                        }
                    }
                } catch (Exception e) {
                    if (!m_reset) {
                        Log.w("UareUSampleJava", "error during capture");
                        m_deviceName = "";
                        onBackPressed();
                    }
                }
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

    public class EnrollmentCallback
            extends Thread
            implements Engine.EnrollmentCallback {
        public int m_current_index = 0;

        private Reader m_reader = null;
        private Engine m_engine = null;

        public EnrollmentCallback(Reader reader, Engine engine) {
            m_reader = reader;
            m_engine = engine;
        }

        // callback function is called by dp sdk to retrieve fmds until a null is returned
        @Override
        public PreEnrollmentFmd GetFmd(Format format) {
            PreEnrollmentFmd result = null;
            while (!m_reset) {
                try {
                    cap_result = m_reader.Capture(Fid.Format.ISO_19794_4_2005, Globals.DefaultImageProcessing, m_DPI, -1);
                } catch (Exception e) {
                    Log.w("UareUSampleJava", "error during capture: " + e.toString());
                    m_deviceName = "";
                    onBackPressed();
                }

                // an error occurred
                if (cap_result == null || cap_result.image == null) continue;

                try {
                    m_enginError = "";
                    // save bitmap image locally
                    m_bitmap = Globals.GetBitmapFromRaw(cap_result.image.getViews()[0].getImageData(), cap_result.image.getViews()[0].getWidth(), cap_result.image.getViews()[0].getHeight());
                    PreEnrollmentFmd prefmd = new Engine.PreEnrollmentFmd();
                    prefmd.fmd = m_engine.CreateFmd(cap_result.image, Format.ISO_19794_2_2005);
                    prefmd.view_index = 0;
                    m_current_fmds_count++;

                    imageBase64 = getStringImage(m_bitmap); // added
                    cbe = cap_result.image.getCbeffId();
                    resolution = cap_result.image.getImageResolution();

                    result = prefmd;
                    break;
                } catch (Exception e) {
                    m_enginError = e.toString();
                    Log.w("UareUSampleJava", "Engine error: " + e.toString());
                }
            }

            m_text_conclusionString = Globals.QualityToString(cap_result);

            if (!m_enginError.isEmpty()) {
                m_text_conclusionString = "Engine: " + m_enginError;
            }

            if (m_enrollment_fmd != null || m_current_fmds_count == 0) {
                if (!m_first) {
                    if (m_text_conclusionString.length() == 0) {
                        m_text_conclusionString = m_success ? "Enrollment template created, size: " + m_templateSize : "Enrollment template failed. Please try again";
                    }
                }
                m_textString = "Place any finger on the reader";
                m_enrollment_fmd = null;
            } else {
                m_first = false;
                m_success = false;
                m_textString = "Continue to place the same finger on the reader";
            }


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    UpdateGUI();
                }
            });

            return result;
        }
    }

    public String getStringImage(Bitmap bm) {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, ba);
        byte[] image = ba.toByteArray();
        return Base64.encodeToString(image, Base64.DEFAULT);
    }
}
