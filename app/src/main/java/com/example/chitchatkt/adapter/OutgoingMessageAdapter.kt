package com.example.chitchatkt.adapter

import android.content.Intent
import android.text.format.DateFormat
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.annotation.NonNull
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chitchatkt.ImagesFullscreenActivity
import com.example.chitchatkt.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.outgoing_message_inflate.view.*
import java.util.*


class OutgoingMessageAdapter(val text: String, val type: String, val timestamp:Long): Item<ViewHolder>() {


    override fun getLayout(): Int {
    return R.layout.outgoing_message_inflate
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val cal: Calendar = Calendar.getInstance(Locale.getDefault())
        cal.timeInMillis= (timestamp*1000)
        val dateFormat: String = DateFormat.format("yyyy-MM-dd HH:mm",cal).toString()
//        val dateFormat: String = DateFormat.format("HH:mm",cal).toString()
        if ((type == "originalimage") or (type == "text" )) {
            viewHolder.itemView.cl2.outgoingmessage.text = text
        }
        if (type == "compressedimage"){
//            viewHolder.itemView.chatimage.setImageBitmap(null)
            Glide.with(viewHolder.itemView.context).clear(viewHolder.itemView.chatimage)
            viewHolder.itemView.chatimage.setImageDrawable(null)
            viewHolder.itemView.cl2.chatimage.visibility = VISIBLE
            viewHolder.itemView.cl2.outgoingmessage.visibility = GONE
            viewHolder.itemView.cl2.messageimageouttime.visibility = VISIBLE
            viewHolder.itemView.cl2.messagetime.visibility = GONE
            Glide.with(viewHolder.itemView.context).asBitmap().load(text).apply(RequestOptions().override(600,600)).into( viewHolder.itemView.cl2.chatimage)
        }
        viewHolder.itemView.cl2.messagetime.text =dateFormat.subSequence(10,dateFormat.length)
        viewHolder.itemView.cl2.messageimageouttime.text=dateFormat.subSequence(10,dateFormat.length)
        viewHolder.itemView.cl2.chatimage.setOnClickListener{
            val intent =Intent(viewHolder.itemView.context,ImagesFullscreenActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("imagebit",text)
            viewHolder.itemView.context.startActivity(intent)
        }
        //        viewHolder.itemView.cl2.outgoingmessage.movementMethod= LinkMovementMethod.getInstance()
    }
}


