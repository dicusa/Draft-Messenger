package com.example.chitchatkt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AlphaAnimation
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chitchatkt.adapter.IncomingMessageAdapter
import com.example.chitchatkt.adapter.OutgoingMessageAdapter
import com.example.chitchatkt.apputils.AppController
import com.example.chitchatkt.databinding.ActivitySettingsBinding
import com.example.chitchatkt.modelclass.Messages
import com.example.chitchatkt.modelclass.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.android.synthetic.main.new_message_inflate.view.*
import kotlinx.android.synthetic.main.toolbar_layout.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    lateinit var view: ActivitySettingsBinding
    lateinit var user: Users
    private val buttonClick = AlphaAnimation(1f, 0.8f)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = DataBindingUtil.setContentView(this,R.layout.activity_settings)

        setSupportActionBar(view.toolbarinc.toolbar)
        val actionbar = supportActionBar
        actionbar!!.title = "Settings"
        actionbar.setDisplayHomeAsUpEnabled(true)
        setButtonPressAnimation()
        onClickListners()
        getUserName()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this,MessagingActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun onClickListners() {

        view.layoutAccounts.setOnClickListener {
            it.startAnimation(buttonClick)
        }

        view.layoutHelp.setOnClickListener {
            it.startAnimation(buttonClick)
        }

        view.layoutInvite.setOnClickListener {
            it.startAnimation(buttonClick)
        }
    }

    //Function to set button press animation to layout
    private fun setButtonPressAnimation() {

        view.layoutAccounts.animation = buttonClick
        view.layoutHelp.animation = buttonClick
        view.layoutInvite.animation = buttonClick

    }

    //Function to set profile picture and name
    private fun setImageAndName()
    {
        Glide.with(this).asBitmap().load(user.imageURl).apply(RequestOptions().override(70 ,70)).into(view.profilepicture)
        view.username.text = user.username
    }

    //Function to get user's information
    private fun getUserName() {
        CoroutineScope(IO).launch {
        val uid= FirebaseAuth.getInstance().uid
        AppController.mFirebaseDatabase = async { AppController.mFirebaseInstance.getReference("/Users") }.await()
        AppController.mFirebaseDatabase!!.orderByChild("uid").equalTo(uid).addChildEventListener(object: ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                    user = p0.getValue(Users::class.java)!!
                    setImageAndName()
                    Log.e("User",""+user.toString())
                    Log.e("Readingdatabase","reading firebase database for active user's image and name")

            }

            override fun onChildRemoved(p0: DataSnapshot) {


            }

        })

    }  }




}
