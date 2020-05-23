package com.example.chitchatkt

import android.Manifest
import android.Manifest.permission.*
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.custom_permission_dialog.*

class SplashScreenActivity : AppCompatActivity() {




    lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        setUpDialog()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          checkPermissionAtRunTime()
        } else {
            intialize()
        }

//        intialize()
    }

    private fun setUpDialog() {
        dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_permission_dialog)
        val text = dialog.findViewById<TextView>(R.id.progresstext)
        text.text = "You need to allow Camera permissions to use the Draft App"
        val window = dialog.window
        window!!.setGravity(Gravity.CENTER)
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }


    override fun onResume() {
        super.onResume()

        checkPermissionAtRunTime()
//        intialize()
    }




    private fun intialize() {

        Handler().postDelayed({
            startActivity(Intent(this, MessagingActivity::class.java))
            finish()
        }, 3000)

//        startActivity(Intent(this, MessagingActivity::class.java))
//        finish()

    }

    private fun checkPermissionAtRunTime() {
        if (ContextCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, SEND_SMS) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, CAMERA )
                || ActivityCompat.shouldShowRequestPermissionRationale(this, SEND_SMS )
                ||ActivityCompat.shouldShowRequestPermissionRationale(this, READ_CONTACTS )
                ||ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_CONTACTS ))
            {
                goToSettingPage()
//                intialize()
            }
            else
            {
                ActivityCompat.requestPermissions(this, arrayOf(CAMERA,SEND_SMS,READ_CONTACTS,WRITE_CONTACTS), 123)
            }
        }
        else { intialize() }
    }


    private fun goToSettingPage() {

        dialog.show()
        dialog.grantButton.setOnClickListener {

            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            dialog.dismiss()
            startActivityForResult(intent, 123)
            Toast.makeText(this,"Go To Application Permission To Allow Access", Toast.LENGTH_SHORT).show()
        }
        dialog.cancelButton.setOnClickListener { dialog.cancel() }
    }


    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults.isNotEmpty()) {

                val camera = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val sendSms = grantResults[1] == PackageManager.PERMISSION_GRANTED
                val readContact = grantResults[2] == PackageManager.PERMISSION_GRANTED
                val writeContact = grantResults[3] == PackageManager.PERMISSION_GRANTED

                if (camera) {
                    intialize()
                } else {
                    checkPermissionAtRunTime()
                }
            }

        }

    }


}


