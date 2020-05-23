package com.example.chitchatkt.adapter;

import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chitchatkt.ImagesFullscreenActivity
import com.example.chitchatkt.R
import com.example.chitchatkt.modelclass.MessageList
import com.example.chitchatkt.modelclass.Messages
import kotlinx.android.synthetic.main.incoming_message_inflate.view.*
import kotlinx.android.synthetic.main.incoming_message_inflate.view.cl2
import kotlinx.android.synthetic.main.outgoing_message_inflate.view.*
import java.util.*
import kotlin.collections.ArrayList


class InOutMessageAdapter(val context: Context,val message:ArrayList<MessageList>):
    RecyclerView.Adapter<InOutMessageAdapter.Holder>() {



        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return if (message[viewType].income)
            Holder(LayoutInflater.from(context).inflate(R.layout.incoming_message_inflate,parent,false))
        else
            Holder(LayoutInflater.from(context).inflate(R.layout.outgoing_message_inflate,parent,false))
    }

    override fun getItemCount(): Int {
        return message.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        var timestamp = message[position].messages.timestamp
        var income = message[position].income
        var type = message[position].messages.type
        var text = message[position].messages.message
        val cal: Calendar = Calendar.getInstance(Locale.getDefault())
        cal.timeInMillis= (timestamp*1000)
        val dateFormat: String = DateFormat.format("yyyy-MM-dd HH:mm",cal).toString()
        if(income){
            if ((type == "originalimage") or (type == "text")) {
                holder.itemView.cl2.incomingmessage.text = text
            }
            else if (type == "compressedimage") {
                holder.itemView.cl2.chatimagein.visibility = View.VISIBLE
                holder.itemView.cl2.incomingmessage.visibility = View.GONE
                holder.itemView.cl2.messageimagetime.visibility = View.VISIBLE
                holder.itemView.cl2.messageintime.visibility = View.GONE
                Glide.with(holder.itemView.context).asBitmap().load(text)
                    .apply(RequestOptions().override(600, 600))
                    .into(holder.itemView.cl2.chatimagein)
            }
            holder.itemView.cl2.messageintime.text =dateFormat.subSequence(10,dateFormat.length)
            holder.itemView.cl2.messageimagetime.text =dateFormat.subSequence(10,dateFormat.length)

            holder.itemView.cl2.chatimagein.setOnClickListener {
                val intent = Intent(holder.itemView.context, ImagesFullscreenActivity::class.java)
                intent.putExtra("imagebit", text)
                holder.itemView.context.startActivity(intent)

            }

        }
        else{
            if ((type == "originalimage") or (type == "text" )) {
                holder.itemView.cl2.outgoingmessage.text = text
            }
            else if (type == "compressedimage"){
//            viewHolder.itemView.chatimage.setImageBitmap(null)
                Glide.with(holder.itemView.context).clear(holder.itemView.chatimage)
                holder.itemView.chatimage.setImageDrawable(null)
                holder.itemView.cl2.chatimage.visibility = View.VISIBLE
                holder.itemView.cl2.outgoingmessage.visibility = View.GONE
                holder.itemView.cl2.messageimageouttime.visibility = View.VISIBLE
                holder.itemView.cl2.messagetime.visibility = View.GONE
                Glide.with(holder.itemView.context).asBitmap().load(text).apply(RequestOptions().override(600,600)).into( holder.itemView.cl2.chatimage)
            }
            holder.itemView.cl2.messagetime.text =dateFormat.subSequence(10,dateFormat.length)
            holder.itemView.cl2.messageimageouttime.text=dateFormat.subSequence(10,dateFormat.length)
            holder.itemView.cl2.chatimage.setOnClickListener{
                val intent =Intent(holder.itemView.context,ImagesFullscreenActivity::class.java)
                intent.putExtra("imagebit",text)
                holder.itemView.context.startActivity(intent)
            }
        }

    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {


    }
}

