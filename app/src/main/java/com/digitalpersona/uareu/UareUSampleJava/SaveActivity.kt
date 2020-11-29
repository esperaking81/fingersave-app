package com.digitalpersona.uareu.UareUSampleJava

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.digitalpersona.uareu.*
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import kotlin.concurrent.thread

class SaveActivity : AppCompatActivity() {
    private lateinit var mReader: Reader
    private lateinit var mEngineError: String
    private var mDPI = 0
    private var mBitmap: Bitmap? = null
    private var mReset: Boolean = false

    private var tip: String = "Place any finger on reader"

    private var imageBase64: String = ""
    private var cbe = 0
    private var resolution = 0

    private var conclusion: String = ""
    private var mDeviceName: String = ""
    private var mEngine: Engine? = null
    private var mFmd: Fmd? = null
    private var capResult: Reader.CaptureResult? = null

    private lateinit var mImageView: ImageView
    private lateinit var mTextConclusion: TextView
    private lateinit var mTipText: TextView

    private fun initializeActivity() {
        mEngineError = ""

        mDeviceName = intent.extras?.getString("device_name", "").toString()

        mImageView = findViewById(R.id.bitmap_img)
        mTextConclusion = findViewById(R.id.conclusion)
        mTipText = findViewById(R.id.tip_save)

        mBitmap = Globals.GetLastBitmap()
        if (mBitmap == null) {
            mBitmap = BitmapFactory.decodeResource(resources, R.drawable.black)
        }

        findViewById<Button>(R.id.backBtn).setOnClickListener {
            onBackPressed()
        }

        updateGUI()
    }

    private fun fileWriting(fPath: String) {
        try {
            FileOutputStream(fPath).use { writer ->
                val textBase64 = Base64.encodeToString(mFmd!!.views[0].data, Base64.DEFAULT)
                writer.write(textBase64.toByteArray())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun updateGUI() {
        mImageView.apply {
            setImageBitmap(mBitmap)
            invalidate()
        }
        mTextConclusion.text = conclusion
        mTipText.text = tip
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_save)
        tip = "Place any finger on reader"
        initializeActivity()

        try {
            mReader = Globals.getInstance().getReader(mDeviceName, applicationContext)
            mReader.Open(Reader.Priority.EXCLUSIVE)
            mDPI = Globals.GetFirstDPI(mReader)
            mEngine = UareUGlobal.GetEngine()
        } catch (_: Exception) {
            Log.w("UareUSampleJava", "error during init of reader")
            mDeviceName = ""
            onBackPressed()
            return
        }

        // loop capture on a separate thread to avoid freezing the UI
        thread {
            mReset = false
            while (mReset.not()) {
                try {
                    capResult = mReader.Capture(Fid.Format.ANSI_381_2004, Globals.DefaultImageProcessing, mDPI, -1)
                } catch (e: java.lang.Exception) {
                    if (mReset.not()) {
                        Log.w("UareUSampleJava", "error during capture: $e")
                        mDeviceName = ""
                        onBackPressed()
                    }
                }

                // an error occured
                if (capResult == null || capResult!!.image == null) continue

                try {
                    mEngineError = ""

                    // save bitmap image locally
                    mBitmap = Globals.GetBitmapFromRaw(capResult!!.image.views[0].imageData, capResult!!.image.views[0].width, capResult!!.image.views[0].height)
                    if (mFmd == null) {
                        mFmd = mEngine?.CreateFmd(capResult!!.image, Fmd.Format.ANSI_378_2004)

                        // save to storage
                        // added ------------------
                        val extDir = Environment.getExternalStorageDirectory().absolutePath
                        val dir = String.format("%s/db/", extDir)
                        Globals.setBase(imageBase64)

                        // save data.txt
                        val base = "finger.json"
                        try {
                            val myFolder = File(dir)
                            if (!myFolder.exists()) {
                                myFolder.mkdirs()
                            }
                            val finger = HashMap<String, Any>()
                            finger["cbe"] = cbe
                            finger["res"] = resolution
                            finger["height"] = mBitmap!!.height
                            finger["width"] = mBitmap!!.width

                            //  finger.put("image", imageBase64);
                            val objectMapper = ObjectMapper()
                            val absolutePath = String.format("%s%s", dir, base)
                            var jsonOutputString: String
                            try {
                                FileOutputStream(absolutePath).use { writer ->
                                    jsonOutputString = objectMapper.writeValueAsString(finger)
                                    writer.write(jsonOutputString.toByteArray())
                                }
                            } catch (e: IOException) {
                                Utils().log("Image save failed: ", e.message)
                            }
                        } catch (e: java.lang.Exception) {
                            Utils().log("Image save failed: ", e.message)
                        }

                        // save finger.png
                        val image = "finger.png"
                        val absPath = String.format("%s%s", dir, image)

                        try {
                            val myFolder = File(dir)
                            if (!myFolder.exists()) {
                                myFolder.mkdirs()
                            }
                            val fos = FileOutputStream(absPath)
                            mBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, fos)
                            fos.flush()
                            fos.close()
                        } catch (e: IOException) {
                            Utils().log("finger.png save error: ", e.message)
                        }

                        // save finger.txt
                        val txt = "finger.txt"
                        val path = String.format("%s%s", dir, txt)

                        try {
                            FileOutputStream(path).use { fos ->
                                val myFolder = File(dir)
                                if (!myFolder.exists()) {
                                    myFolder.mkdirs()
                                }
                                fos.write(imageBase64.toByteArray(Charset.defaultCharset()))
                                fos.flush()
                            }
                        } catch (e: java.lang.Exception) {
                            Utils().log("finger.txt save error: ", e.message)
                        }

                        // save data.txt
                        val fName = "byte.txt"
                        val fPath = String.format("%s%s", dir, fName)
                        fileWriting(fPath)

                        conclusion = "Fingerprint saved !"

                        // end added -------------------
                    }

                } catch (e: Exception) {
                    mEngineError = e.toString()
                    Log.w("UareUSampleJava", "Engine error: $e")
                }

                conclusion = Globals.QualityToString(capResult)

                if (mEngineError.isNotEmpty()) {
                    conclusion = "Engine: $mEngineError"
                }

                runOnUiThread(this@SaveActivity::updateGUI)
            }
        }
    }

    override fun onBackPressed() {
        try {
            mReset = true
            mReader.CancelCapture()
            mReader.Close()
        } catch (_: Exception) {

        }

        val i = Intent()
        i.putExtra("device_name", mDeviceName)
        setResult(Activity.RESULT_OK, i)
        finish()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setContentView(R.layout.activity_save)
        initializeActivity()
    }
}