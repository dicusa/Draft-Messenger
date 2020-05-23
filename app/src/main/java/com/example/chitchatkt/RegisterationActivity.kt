package com.example.chitchatkt

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.chitchatkt.apputils.AppController.Companion.mFirebaseDatabase
import com.example.chitchatkt.apputils.AppController.Companion.mFirebaseInstance
import com.example.chitchatkt.databinding.ActivityRegisterationBinding
import com.example.chitchatkt.modelclass.Users
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_registeration.view.*
import kotlinx.android.synthetic.main.activity_registeration.view.ll2
import kotlinx.android.synthetic.main.activity_registeration.view.ll_1
import kotlinx.android.synthetic.main.activity_registeration.view.passwordtoggle
import java.io.ByteArrayOutputStream
import java.util.*


class RegisterationActivity : AppCompatActivity() {
    var context: Context = this@RegisterationActivity
    lateinit var view: ActivityRegisterationBinding
    private lateinit var auth: FirebaseAuth
    lateinit var username: EditText
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var mobile: EditText
    lateinit var register: Button
    lateinit var alreadyregistered: TextView
    internal var bitmap: Bitmap? = null
    var check: Boolean = false
    var bool: Boolean = false
    var profilepicture: Uri? = null
    lateinit var data: ByteArray
    lateinit var imageURL: String
    var toggle: Boolean = false
    lateinit var dialog: Dialog
    private val buttonClick = AlphaAnimation(1f, 0.8f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = DataBindingUtil.setContentView(this, R.layout.activity_registeration)
        auth = FirebaseAuth.getInstance()

        findbyid()
        setupdialog()
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.profileimage96)
        setonclicklistners()


    }

    private fun setupdialog() {
    dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_progress_dialog)
        val text = dialog.findViewById<TextView>(R.id.progresstext)
        text.text = getString(R.string.registering)
        val window = dialog.window
        window!!.setGravity(Gravity.CENTER)
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    private fun findbyid() {
        alreadyregistered = findViewById(R.id.alreadyregistered)
        username = findViewById(R.id.username)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        mobile = findViewById(R.id.mobile)
        register = findViewById(R.id.register)
    }

    private fun setonclicklistners() {

        register.setOnClickListener {
            val textUsername: String = username.text.toString().trim()
            val textEmail: String = email.text.toString().trim()
            val textPassword: String = password.text.toString().trim()
            val textMobile: String = mobile.text.toString().trim()

            when {
                TextUtils.isEmpty(textUsername) or TextUtils.isEmpty(textEmail) or TextUtils.isEmpty(textPassword) or TextUtils.isEmpty(
                    textMobile
                ) -> {

                    Snackbar.make(it, "Fill All Credentials", Snackbar.LENGTH_LONG).show()

                }
                textPassword.length < 6 -> {

                    Toast.makeText(this, "Password should have atleast 6 characters", Toast.LENGTH_LONG).show()
                    password.requestFocus()
                }
                (textMobile.length < 10) or (textMobile.length > 10) -> {

                    Toast.makeText(this, "Invalid Mobile No.", Toast.LENGTH_LONG).show()
                    mobile.requestFocus()
                }
                else -> {

                    dialog.show()
                    getUserDetailFromFirebase(textMobile)
                    registration(textUsername, textEmail, textPassword, textMobile)
                }
            }
        }


        alreadyregistered.setOnClickListener {
            it.startAnimation(buttonClick)
            intents(Intent(context, LoginActivity::class.java))
//            var intent: Intent = Intent(context, LoginActivity::class.java)
////            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
////            startActivity(intent)
            finish()

        }


        view.scroll.ll_1.addprofileimage.setOnClickListener {
            it.startAnimation(buttonClick)
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 123)
        }

        view.ll1.ll2.passwordtoggle.setOnClickListener {
            it.startAnimation(buttonClick)
            if(toggle == false)
            {
                password.transformationMethod = HideReturnsTransformationMethod.getInstance()
                toggle = true
            }

            else
            {
                password.transformationMethod = PasswordTransformationMethod.getInstance()
                toggle = false
            }
        }
    }


    private fun intents(intent: Intent){

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

    }

    fun registration(usernameText: String, emailText: String, passwordText: String, mobileText: String) {
        auth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener {


            Log.e("mFirebaseDatabase", "" + bool)
            if (!bool) {
                if (!it.isSuccessful) return@addOnCompleteListener

                val userID: String = auth.uid.toString()
                Log.e("mFirebaseDatabase", "" + userID)

                converttoByteArray(bitmap!!)

                imageURL = "hello"

                saveImageToFirebaseStorage( usernameText, userID, mobileText)

            }

        }
            .addOnFailureListener {
                Log.e("mFirebaseDatabase", "failed to register")
                Toast.makeText(this, "You can't register with this Email ", Toast.LENGTH_SHORT).show()
//                view.progressdialog.visibility = GONE
                    dialog.dismiss()
            }

    }

    private fun savetodatabase(
        imageURL: String,
        usernameText: String,
        userId: String,
        mobileText: String



    ) {
        if (!bool) {
            mFirebaseDatabase = mFirebaseInstance.getReference("/Users/$userId")


            val user = Users(imageURL, usernameText, userId, mobileText)

            mFirebaseDatabase!!.setValue(user)

            Log.e("mFirebaseDatabase", "the value has been written on database")

            dialog.dismiss()
            Toast.makeText(this,"Successfully Registered ",Toast.LENGTH_SHORT).show()
            gotoLoginavtivity()
        }
        else{
        Log.e("mFirebaseDatabase", "mobile already registered")
        Toast.makeText(this,"Mobile no already registered ",Toast.LENGTH_SHORT).show()

        dialog.dismiss()
        mobile.requestFocus()
        }



    }

    private fun saveImageToFirebaseStorage(
        usernameText: String,
        userID: String,
        mobileText: String) {

         if (!bool) {
            val imageName = UUID.randomUUID().toString()
            val mFirebaseStorageInstance = FirebaseStorage.getInstance().getReference("/profilepicture/$imageName")
            Log.e("mFirebaseStorage", "image compressed")
            mFirebaseStorageInstance.putBytes(data)
                .addOnSuccessListener {
                    Log.e("mFirebaseStorage", "image uploaded successfully ${it.metadata?.path}")
                    mFirebaseStorageInstance.downloadUrl.addOnSuccessListener { it1 ->
                        Log.e("ImageUrl", "$it1")
                        imageURL = it1.toString()
                        savetodatabase(imageURL, usernameText, userID, mobileText)
                    }


                }
                .addOnFailureListener {
                    Log.e("mFirebaseStorage", "image upload failed")
                }
                .addOnCompleteListener {


                }
        }
        else{
                Log.e("mFirebaseDatabase", "mobile already registered")
                Toast.makeText(this,"Mobile no already registered ",Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                mobile.requestFocus()

            }
    }

    private fun getUserDetailFromFirebase(mobiletext: String) {
        Log.e("checkingmatch","getUserDetailsFromFirebase")

        mFirebaseDatabase = mFirebaseInstance.getReference("/Users")
        mFirebaseDatabase!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {

                p0.children.forEach {
                    Log.e("chechingmatch", "the data has been fetched succesfully")
                    Log.e("chechingmatch", "" + it.toString())
                    val user = it.getValue(Users::class.java)

                    if (user!!.mobile == mobiletext) {
                        bool = true
                    }


                }



            }


        })


    }

    private fun gotoLoginavtivity() {
        intents(Intent(context, LoginActivity::class.java))
//        val intent: Intent = Intent(context, LoginActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
                profilepicture = data.data
                Log.e(TAG, "onActivityResult: images= " + profilepicture!!)
                view.scroll.ll_1.cl_2.addprofileimagetext.visibility = View.GONE
                Glide.with(this).asBitmap().load(profilepicture).apply(RequestOptions().override(120,120)).into(view.scroll.ll_1.cl_2.addprofileimage)
                comverttoBitmap(profilepicture!!)
                //image.setImageBitmap(bitmap);



            } else
                Log.e(TAG, "onActivityResult: error")

        }
    }

    private fun comverttoBitmap(images: Uri) {

        try {

            Glide.with(this).asBitmap().load(images).into(object: CustomTarget<Bitmap>(120, 120){
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    bitmap = resource
                }
            })
            //bitmap = BitmapFactory.decodeStream(this.contentResolver.openInputStream(images))
//            return bitmap!!
        } catch (e: Exception) {

        }
//        return bitmap!!
    }

    private fun converttoByteArray(bitmap: Bitmap) {

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        data = outputStream.toByteArray()
//        val temp = Base64.encodeToString(data, Base64.DEFAULT)
        Log.e("byte", "" + data)


    }

}



