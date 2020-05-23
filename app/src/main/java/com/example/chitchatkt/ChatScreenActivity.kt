package com.example.chitchatkt

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.chitchatkt.NewMessagesActivity.Companion.USER_DATA
import com.example.chitchatkt.adapter.InOutMessageAdapter
import com.example.chitchatkt.adapter.IncomingMessageAdapter
import com.example.chitchatkt.adapter.OutgoingMessageAdapter
import com.example.chitchatkt.apputils.AppController
import com.example.chitchatkt.apputils.AppController.Companion.mFirebaseDatabase
import com.example.chitchatkt.apputils.AppController.Companion.mFirebaseDatabaseagain
import com.example.chitchatkt.databinding.ActivityChatScreenBinding
import com.example.chitchatkt.modelclass.MessageList
import com.example.chitchatkt.modelclass.Messages
import com.example.chitchatkt.modelclass.MessagesSent
import com.example.chitchatkt.modelclass.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_chat_screen.view.*
import kotlinx.android.synthetic.main.activity_messaging.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList


class ChatScreenActivity : AppCompatActivity() {
    val context: Context = this
    lateinit var view: ActivityChatScreenBinding

    val adapter: GroupAdapter<ViewHolder> = GroupAdapter<ViewHolder>()
//lateinit var adapter: InOutMessageAdapter
   var messageList: ArrayList<MessageList> = ArrayList()
    lateinit var titleUsername: Users
    private val buttonClick = AlphaAnimation(1f, 0.8f)
    lateinit var dialog: Dialog
    lateinit var imagedialog: Dialog
    lateinit var images: Uri
    lateinit var sound: MediaPlayer
    lateinit var bitmap: Bitmap
    lateinit var convertedBytes: ByteArray
    lateinit var toid: String
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = DataBindingUtil.setContentView(this,R.layout.activity_chat_screen)
        titleUsername = intent.getParcelableExtra<Users>(USER_DATA)
//        setSupportActionBar(view.toolbarinc.toolbar)
//        val actionbar = supportActionBar
//        actionbar!!.title = titleUsername.username
//        actionbar.setDisplayHomeAsUpEnabled(true)

        view.toolbarinc.username.text = titleUsername.username
        toid = titleUsername.uid
        Glide.with(this).asBitmap()
            .load(titleUsername.imageURl)
            .apply(RequestOptions().override(100,100))
            .into(view.toolbarinc.profilepicture)

        Log.e("Toid",toid)
//      sound = MediaPlayer.create(this,R.raw.sentmessage)
        setRecyclerview(toid)
//        callAdapter(toid)
        setUpDialog()
        setupimagedialog()
//      adapter.notifyDataSetChanged()
        onclicklistners()

    }

    override fun onBackPressed() {
        val intent = Intent(this,MessagingActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun setUpDialog() {
        dialog = Dialog(this)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.attach_menu_inflate)
        val gallery = dialog.findViewById<CircleImageView>(R.id.attachimage)
        gallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            dialog.dismiss()
            startActivityForResult(intent, 123)
        }
        val window = dialog.window
        window!!.setGravity(Gravity.BOTTOM)
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    private fun onclicklistners() {

        view.send.setOnClickListener {
            it.startAnimation(buttonClick)
            val message: String = view.entermessage.text.toString().trim()
            if (message != "") {
                sendMessage(message)
            }
        }

        view.entermessage.setOnClickListener {
            it.startAnimation(buttonClick)
            view.recyclerview.scrollToPosition(adapter.itemCount - 1)
        }

        view.toolbarinc.backbuttonimage.setOnClickListener {
            it.startAnimation(buttonClick)
            startActivity(Intent(this,MessagingActivity::class.java))
            finish()
        }

        view.attach.setOnClickListener {
            dialog.show()
        }

        view.cl2.camera.setOnClickListener {
            val intent = Intent("android.media.action.IMAGE_CAPTURE")

            startActivityForResult(intent, 127)
        }
    }

    private fun sendMessage (message: String) {
        CoroutineScope(IO).launch {

        Log.e("SendingMessage","sending message format to Firebase database")
        val fromid = FirebaseAuth.getInstance().uid
//        val titleUsername = intent.getParcelableExtra<Users>(USER_DATA)
        val toid = titleUsername.uid

        val timestamp = (System.currentTimeMillis() / 1000)

        if (fromid == null) return@launch

//        mFirebaseDatabase = AppController.mFirebaseInstance.getReference("/Message").push()
        mFirebaseDatabase = withContext(Dispatchers.Default) {
            AppController.mFirebaseInstance.getReference("/Messages/$fromid/$toid").push()
        }
            mFirebaseDatabaseagain = async { AppController.mFirebaseInstance.getReference("/Messages/$toid/$fromid").push() }.await()
        val messages = Messages(mFirebaseDatabase!!.key!!, message, toid, fromid, timestamp,"text")
        mFirebaseDatabase!!.setValue(messages)
        mFirebaseDatabaseagain!!.setValue(messages)

        val latestMessage: DatabaseReference =  async { AppController.mFirebaseInstance.getReference("/LatestMessages/$fromid/$toid") }.await()
        latestMessage.setValue(messages)
        val latestMessageAgain: DatabaseReference = async { AppController.mFirebaseInstance.getReference("/LatestMessages/$toid/$fromid") }.await()
        latestMessageAgain.setValue(messages)
//        view.recyclerview.requestFocus()


    }
        view.entermessage.text.clear()
    }

    private fun callAdapter(toid: String) {
//        adapter.setHasStableIds(false)
        CoroutineScope(IO).launch{

            val fromid = withContext(Dispatchers.Default) {
                FirebaseAuth.getInstance().uid
            }
//        val toid = titleUsername.uid
            mFirebaseDatabase = async { AppController.mFirebaseInstance.getReference("/Messages/$fromid/$toid")  }.await()
            mFirebaseDatabase!!.addChildEventListener(object: ChildEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                    val text = p0.getValue(Messages::class.java)
                    Log.e("Readingdatabase","reading firebase database for messages")

                    if(text != null) {
//                    val uid = FirebaseAuth.getInstance().uid

                        if(text.fromid == fromid) {

                     adapter.add(adapter.itemCount+1,
                         IncomingMessageAdapter(text.message,text.type,text.timestamp)
                     )
//                            adapter = InOutMessageAdapter(context,text.message,text.type,text.timestamp,true)
//                            val messages: MessageList= MessageList(text,true)
//                            messageList.add(messages)

                        } else {
                    //                        if(text.toid == toid){

                                                adapter.add(adapter.itemCount,
                                                    OutgoingMessageAdapter(text.message,text.type,text.timestamp)
                                                )
//                            adapter = InOutMessageAdapter(context,text.message,text.type,text.timestamp,false)
//                            val messages: MessageList= MessageList(text,false)
//                            messageList.add(messages)
                    //                                }
                        }
                        view.recyclerview.scrollToPosition(adapter.itemCount - 1)
                        view.recyclerview.adapter = adapter

                    }


                }

                override fun onChildRemoved(p0: DataSnapshot) { }

            })


        }



    }

    private fun setRecyclerview(toid: String) {

        view.recyclerview.layoutManager = LinearLayoutManager(this)
        view.recyclerview.setHasFixedSize(true)
        callAdapter(toid)
//        adapter = InOutMessageAdapter(context,messageList)
        view.recyclerview.adapter = adapter
        view.recyclerview.scrollToPosition(adapter.itemCount - 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
                images = data.data!!
                Log.e(ContentValues.TAG, "onActivityResult: originalimages= " + images)
                imagedialog.show()
            }

            if (requestCode == 125 && resultCode == Activity.RESULT_OK) {
                images = data.data!!
                Log.e(ContentValues.TAG, "onActivityResult: compressedimages= " + images)
                comverttobitmapcompressed(images)

            }
            if (requestCode == 127 && resultCode == Activity.RESULT_OK) {
                    var imagebit:Bitmap = data.extras!!.get("data") as Bitmap
                    Log.e(ContentValues.TAG, "onActivityResult: compressedimages= " + imagebit)
                    compressbitmap(imagebit)

            }

        }
    }

    private fun compressbitmap(images: Bitmap) {

        try {

            Glide.with(this).asBitmap().load(images).into(object: CustomTarget<Bitmap>(750, 1050){
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    bitmap = resource
                    view.toolbarinc.progress.visibility = VISIBLE
                    setImagetoFirebaseChat(125)
                }
            })
            //bitmap = BitmapFactory.decodeStream(this.contentResolver.openInputStream(images))
//            return bitmap!!
        } catch (e: Exception) {

        }

    }

    private fun comverttobitmapcompressed(images: Uri) {

        try {

            Glide.with(this).asBitmap().load(images).into(object: CustomTarget<Bitmap>(750, 1050){
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    bitmap = resource
                    setImagetoFirebaseChat(125)
                }
            })
            //bitmap = BitmapFactory.decodeStream(this.contentResolver.openInputStream(images))
//            return bitmap!!
        } catch (e: Exception) {

        }

    }

    private fun setupimagedialog() {
        imagedialog = Dialog(this)
        imagedialog.setCancelable(true)
        imagedialog.setContentView(R.layout.custom_dialog_image_quality)
        val original = imagedialog.findViewById<Button>(R.id.original)
        val compressed = imagedialog.findViewById<Button>(R.id.compressed)
        original.setOnClickListener {
            view.toolbarinc.progress.visibility = VISIBLE
            converttoBitmapOriginal(images)
//            setImagetoFirebaseChat(123)
            imagedialog.dismiss()
        }
        compressed.setOnClickListener {
            view.toolbarinc.progress.visibility = VISIBLE
            comverttobitmapcompressed(images)
//            setImagetoFirebaseChat(125)
            imagedialog.dismiss()
        }
        val window = imagedialog.window
        window!!.setGravity(Gravity.CENTER)
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    private fun setImagetoFirebaseChat(token: Int) {
        converttoByteArray(bitmap)
        val imagename = UUID.randomUUID().toString()
        val mFirebaseStorageInstance = FirebaseStorage.getInstance().getReference("/chatimages/$imagename")
        //val compressedimageuri: String =
        //  Glide.with(context).load(profilepicture).apply(RequestOptions().override(50,50)).toString()
        // val compresseduri: Uri = Uri.parse(compressedimageuri)
        Log.e("mFirebaseStorage", "image compressed")
        mFirebaseStorageInstance.putBytes(convertedBytes)
            .addOnSuccessListener {
                Log.e("mFirebaseStorage", "image uploaded successfully ${it.metadata?.path}")
                mFirebaseStorageInstance.downloadUrl.addOnSuccessListener { it1 ->
                    Log.e("ImageUrl", "$it1")
                    val imageURL = it1.toString()
                    saveToChatLog(imageURL,token)
                }


            }
            .addOnFailureListener {
                Log.e("mFirebaseStorage", "image upload failed")
                Toast.makeText(this,"Can't send this file",Toast.LENGTH_SHORT).show()
            }


    }

    private fun saveToChatLog(imageURL: String, token: Int) {
        CoroutineScope(IO).launch {
            val fromid = FirebaseAuth.getInstance().uid

            val toid = titleUsername.uid

            if (fromid == null) {return@launch}


            mFirebaseDatabase = async {  AppController.mFirebaseInstance.getReference("/Messages/$fromid/$toid").push() }.await()
            mFirebaseDatabaseagain = async { AppController.mFirebaseInstance.getReference("/Messages/$toid/$fromid").push() }.await()

            if(token == 123)
            {
                Log.e("SendingMessage","sending message format to Firebase database")
                val timestamp =ServerValue.TIMESTAMP
                val messages = MessagesSent(mFirebaseDatabase!!.key!!, imageURL, toid, fromid, timestamp,"originalimage")
                mFirebaseDatabase!!.setValue(messages)
                mFirebaseDatabaseagain!!.setValue(messages)

                view.toolbarinc.progress.visibility = GONE

                val latestMessage: DatabaseReference = AppController.mFirebaseInstance.getReference("/LatestMessages/$fromid/$toid")
                latestMessage.setValue(messages)
                val latestMessageAgain: DatabaseReference = AppController.mFirebaseInstance.getReference("/LatestMessages/$toid/$fromid")
                latestMessageAgain.setValue(messages)
            //        view.recyclerview.requestFocus()
            }

        if(token == 125){
//            Log.e("SendingMessage","sending message format to Firebase database")
//            val fromid = FirebaseAuth.getInstance().uid
////        val titleUsername = intent.getParcelableExtra<Users>(USER_DATA)
//            val toid = titleUsername.uid
//
            val timestamp = (System.currentTimeMillis() / 1000)
//
//            if (fromid == null) return@launch
//
////        mFirebaseDatabase = AppController.mFirebaseInstance.getReference("/Message").push()
//            mFirebaseDatabase = async {  AppController.mFirebaseInstance.getReference("/Messages/$fromid/$toid").push() }.await()
//            mFirebaseDatabaseagain = async { AppController.mFirebaseInstance.getReference("/Messages/$toid/$fromid").push() }.await()
            val messages = Messages(mFirebaseDatabase!!.key!!, imageURL, toid, fromid, timestamp,"compressedimage")
            mFirebaseDatabase!!.setValue(messages)
            mFirebaseDatabaseagain!!.setValue(messages)



            val latestMessage: DatabaseReference = AppController.mFirebaseInstance.getReference("/LatestMessages/$fromid/$toid")
            latestMessage.setValue(messages)
            val latestMessageAgain: DatabaseReference = AppController.mFirebaseInstance.getReference("/LatestMessages/$toid/$fromid")
            latestMessageAgain.setValue(messages)
//        view.recyclerview.requestFocus()
        }
        }
        view.toolbarinc.progress.visibility = GONE
    }

    private fun converttoBitmapOriginal(images: Uri) {

        try {

            Glide.with(this).asBitmap().load(images).into(object: CustomTarget<Bitmap>(){
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    bitmap = resource
                    setImagetoFirebaseChat(123)
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
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        convertedBytes = outputStream.toByteArray()
//        val temp = Base64.encodeToString(data, Base64.DEFAULT)
        Log.e("byte", "" + convertedBytes)


    }
}
