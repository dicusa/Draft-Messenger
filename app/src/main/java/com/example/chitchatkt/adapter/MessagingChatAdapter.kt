package com.example.chitchatkt.adapter

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.ItemTouchHelper
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chitchatkt.ChatScreenActivity
import com.example.chitchatkt.MessagingActivity
import com.example.chitchatkt.NewMessagesActivity
import com.example.chitchatkt.R
import com.example.chitchatkt.apputils.AppController
import com.example.chitchatkt.apputils.AppController.Companion.mFirebaseDatabase
import com.example.chitchatkt.customtouchlistners.OnSwipeTouchListner
import com.example.chitchatkt.modelclass.Messages
import com.example.chitchatkt.modelclass.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.messging_chat_inflate.view.*
import kotlinx.android.synthetic.main.messging_chat_inflate.view.username
import kotlinx.android.synthetic.main.new_message_inflate.view.*

class MessagingChatAdapter(val messageschat: Messages): Item<ViewHolder>() {

    var ChatPartnerUser: Users? = null
    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.message.text = messageschat.message

        val ChatPartnerId: String
        if(messageschat.fromid == FirebaseAuth.getInstance().uid){
            ChatPartnerId = messageschat.toid
        }
        else{
            ChatPartnerId = messageschat.fromid
        }

        AppController.mFirebaseDatabase = AppController.mFirebaseInstance.getReference("/Users/$ChatPartnerId")
        mFirebaseDatabase!!.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                ChatPartnerUser= p0.getValue(Users::class.java)

                viewHolder.itemView.username.text = ChatPartnerUser?.username

               Glide.with(viewHolder.itemView.context).asBitmap().load(ChatPartnerUser?.imageURl)
                   .apply(RequestOptions().override(60,60)).into( viewHolder.itemView.profilepicture)

            }

        })

//        viewHolder.itemView.cl1.setOnTouchListener(object: OnSwipeTouchListner(){
//            override fun onSwipeRight() {
//                val intent: Intent = Intent(viewHolder.itemView.context, ChatScreenActivity::class.java)
//                intent.putExtra(NewMessagesActivity.USER_DATA,ChatPartnerUser)
//                viewHolder.itemView.context.startActivity(intent)
//
//            }
//
//
//        })



    }
    override fun getLayout(): Int {

        return R.layout.messging_chat_inflate
    }


}