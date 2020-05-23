package com.example.chitchatkt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AlphaAnimation
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chitchatkt.adapter.NewMessageAdapter
import com.example.chitchatkt.apputils.AppController.Companion.mFirebaseDatabase
import com.example.chitchatkt.apputils.AppController.Companion.mFirebaseInstance
import com.example.chitchatkt.databinding.ActivityNewMessagesBinding
import com.example.chitchatkt.modelclass.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.toolbar_layout.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class NewMessagesActivity : AppCompatActivity() {
    lateinit var view: ActivityNewMessagesBinding
    val adapter: GroupAdapter<ViewHolder> = GroupAdapter<ViewHolder>()

    private val buttonClick = AlphaAnimation(1f, 0.8f)
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = DataBindingUtil.setContentView(this,R.layout.activity_new_messages)
        setSupportActionBar(view.toolbarinc.toolbar)
        val actionbar = supportActionBar
        actionbar!!.title = "Select contact"
        actionbar.setDisplayHomeAsUpEnabled(true)

        getUserDetailFromFirebase()

    }

    override fun onBackPressed() {
        super.onBackPressed()

        val intent = Intent(this,MessagingActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

   /* private fun getUserDetailFromFirebase() {

        mFirebaseDatabase = mFirebaseInstance!!.getReference("/Users")
        mFirebaseDatabase!!.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    Log.e("GetData","the data has been fetched succesfully")
                    Log.e("GetData",""+it.toString())
                    val usersCopy = it.getValue(UsersCopy::class.java)
                    if (usersCopy != null)
                    adapter.add(NewMessageAdapter(usersCopy))
                }
                setRecyclerView()
            }
        })
    }*/


    companion object
    {
        val USER_DATA = "USER_DATA"
    }


    //Function to get user details
    private fun getUserDetailFromFirebase() {
        CoroutineScope(IO).launch {
        mFirebaseDatabase = async { mFirebaseInstance.getReference("/Users") }.await()
        val postListener = object : ValueEventListener
        {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot)
            {
                Log.e("NewMessageActivity", "datasnapshot for user = $p0")
                adapter.clear()
                p0.children.forEach {
                    Log.e("GetData","the data has been fetched succesfully")
                    Log.e("GetData",""+it.toString())
                    val usersCopy = it.getValue(Users::class.java)
                    if (usersCopy != null)
                    {
                        if (usersCopy.uid != FirebaseAuth.getInstance().uid)
                        adapter.add(NewMessageAdapter(usersCopy))
                    }
                }
                setRecyclerView()

                adapter.setOnItemClickListener { item, view ->
                    val users = item as NewMessageAdapter
                    val intent: Intent = Intent(view.context,ChatScreenActivity::class.java)
                    intent.putExtra(USER_DATA, users.user)
                    startActivity(intent)
                    finish()
                }
                //val post=p0.getValue(UsersCopy::class.java)
            }

        }
        mFirebaseDatabase!!.addValueEventListener(postListener)

    }
    }




    private fun setRecyclerView() {

//        adapter = GroupAdapter<ViewHolder>()
        view.recyclerview.layoutManager = LinearLayoutManager(this)
        view.recyclerview.setHasFixedSize(true)
        view.recyclerview.adapter = adapter
    }
}
