package com.example.chitchatkt

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chitchatkt.adapter.MessagingChatAdapter
import com.example.chitchatkt.apputils.AppController
import com.example.chitchatkt.databinding.ActivityMessagingBinding
import com.example.chitchatkt.modelclass.Messages
import com.example.chitchatkt.reciever.NetworkChangeReciever
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.toolbar_layout.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MessagingActivity : AppCompatActivity(),NetworkChangeReciever.ConnectivityRecieverListner {


    lateinit var view: ActivityMessagingBinding
    val context: Context = this
    private lateinit var auth: FirebaseAuth

    lateinit var builder: AlertDialog.Builder
    private val adapter: GroupAdapter<ViewHolder> = GroupAdapter<ViewHolder>()
    val messagingMap = HashMap<String, Messages>()
    private lateinit var mNetworkReceiver: BroadcastReceiver
    private val buttonClick = AlphaAnimation(1f, 0.8f)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = DataBindingUtil.setContentView(this,R.layout.activity_messaging)
        auth = FirebaseAuth.getInstance()
        setSupportActionBar(view.toolbarinc.toolbar)
        val actionbar = supportActionBar
        actionbar!!.title = "Draft"
//        actionbar.setDisplayHomeAsUpEnabled(true)
//        checkforinternet()
        mNetworkReceiver = NetworkChangeReciever()
        registerNetworkBroadcast()
        checkUserStatus()
        setRecyclerview()
//        setRecyclertouchevent()
        callAdapter()
        setonclicklistners()
    }

//    private fun setRecyclertouchevent() {
//        val itemtouchhelper: ItemTouchHelper.SimpleCallback = ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT){
//
//        })
//    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {

showDialog(isConnected)
    }

    private fun showDialog(isConnected: Boolean) {
        var alert: Any

        builder = AlertDialog.Builder(this)
        alert = builder.create()
        if(!isConnected){

           builder.setMessage("Please check your network connectivity and try again.")
            builder.setCancelable(true)

            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
//                finish()
            }
            alert = builder.create()
            alert.setTitle("Oopss...")
            alert.show()

        }
        else{

           if(alert != null && alert.isShowing){
               alert.dismiss()
           }
        }
    }

    override fun onPause() {
        unregisterNetworkBroadcast()
        super.onPause()
    }
    override fun onDestroy() {
        unregisterNetworkBroadcast()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()

        NetworkChangeReciever.connectivityReciverListner = this
    }


    private fun registerNetworkBroadcast() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            this.registerReceiver(NetworkChangeReciever(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        }
        else{

            this.registerReceiver(NetworkChangeReciever(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        }
    }

    private fun unregisterNetworkBroadcast(){


        try {
            unregisterReceiver(mNetworkReceiver)
        }catch (e: IllegalArgumentException){
            e.printStackTrace()
        }
    }
    private fun callAdapter() {

        CoroutineScope(IO).launch {
        val fromID = FirebaseAuth.getInstance().uid
        val userDatabase: DatabaseReference =  async { AppController
            .mFirebaseInstance.getReference("/LatestMessages/$fromID") }.await()
        userDatabase.addChildEventListener(object: ChildEventListener{

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                val messages = p0.getValue(Messages::class.java) ?: return
                messagingMap[p0.key!!] = messages
                refreshRecyclerView()

            }


            override fun onChildChanged(p0: DataSnapshot, p1: String?){

                val messages = p0.getValue(Messages::class.java) ?: return
                messagingMap[p0.key!!] = messages
                refreshRecyclerView()

            }

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }


            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })
        }
    }

    private fun refreshRecyclerView() {
        adapter.clear()
        messagingMap.values.forEach {

            adapter.add(MessagingChatAdapter(it))

        }



    }



    private fun setonclicklistners() {
        view.newMessage.setOnClickListener {
            it.startAnimation(buttonClick)
            intents(Intent(this,NewMessagesActivity::class.java ),false)
//            val intent: Intent = Intent(this,NewMessagesActivity::class.java )
//            startActivity(intent)
            Log.e("new message","moved to new message activity")
        }

        adapter.setOnItemClickListener { item, view ->
            view.startAnimation(buttonClick)
            val users = item as MessagingChatAdapter
            val intent: Intent = Intent(view.context,ChatScreenActivity::class.java)
            intent.putExtra(NewMessagesActivity.USER_DATA, users.ChatPartnerUser)
            startActivity(intent)
            finish()

        }


    }

    //Function to check active user
    private fun checkUserStatus()
    {
        val uid = auth.uid

        if(uid == null)
        {
            intents(Intent(this,LoginActivity::class.java ),true)
//            val intent: Intent = Intent(this,LoginActivity::class.java )
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
            Log.e("checkuser","no Active User on device")
        }

    }

    //Function to check menu option selected
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_signout -> {
                auth.signOut()
                intents(Intent(this,LoginActivity::class.java ),true)
//                val intent: Intent = Intent(this,LoginActivity::class.java )
////                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
////                startActivity(intent)
                Log.e("SignOut","User has been sign out")

            }

            R.id.menu_settings -> {
                intents(Intent(this,SettingsActivity::class.java),false)
//                val intent = Intent(this,SettingsActivity::class.java)
//                startActivity(intent)
                Log.e("Settings","User has been directed to setting activity")

            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.message_window_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun intents(intent: Intent, flag: Boolean) {
        if(flag) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        else startActivity(intent)
    }
    private fun setRecyclerview() {

        view.recyclerView.layoutManager = LinearLayoutManager(this)
        view.recyclerView.setHasFixedSize(true)
        view.recyclerView.adapter = adapter


    }


}
