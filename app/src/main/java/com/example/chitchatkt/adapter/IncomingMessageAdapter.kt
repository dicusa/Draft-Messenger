package com.example.chitchatkt.adapter

import android.content.Intent
import android.text.format.DateFormat
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chitchatkt.ImagesFullscreenActivity
import com.example.chitchatkt.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.incoming_message_inflate.view.*
import kotlinx.android.synthetic.main.incoming_message_inflate.view.cl2
import kotlinx.android.synthetic.main.messging_chat_inflate.view.*
import java.security.Timestamp
import java.util.*

class IncomingMessageAdapter(val text: String, val type: String, val timestamp:Long): Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.incoming_message_inflate
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val cal: Calendar = Calendar.getInstance(Locale.getDefault())
        cal.timeInMillis= (timestamp*1000)
        val dateFormat: String = DateFormat.format("yyyy-MM-dd HH:mm",cal).toString()
//        val dateFormat: String = DateFormat.format("HH:mm",cal).toString()

        if ((type == "originalimage") or (type == "text")) {
            viewHolder.itemView.cl2.incomingmessage.text = text
        }
        if (type == "compressedimage") {
            viewHolder.itemView.cl2.chatimagein.visibility = View.VISIBLE
            viewHolder.itemView.cl2.incomingmessage.visibility = View.GONE
            viewHolder.itemView.cl2.messageimagetime.visibility = View.VISIBLE
            viewHolder.itemView.cl2.messageintime.visibility = View.GONE
            Glide.with(viewHolder.itemView.context).asBitmap().load(text)
                .apply(RequestOptions().override(600, 600))
                .into(viewHolder.itemView.cl2.chatimagein)
        }
        viewHolder.itemView.cl2.messageintime.text =dateFormat.subSequence(10,dateFormat.length)
        viewHolder.itemView.cl2.messageimagetime.text =dateFormat.subSequence(10,dateFormat.length)

        viewHolder.itemView.cl2.chatimagein.setOnClickListener {
            var intent = Intent(viewHolder.itemView.context, ImagesFullscreenActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("imagebit", text)
            viewHolder.itemView.context.startActivity(intent)
//        viewHolder.itemView.incomingmessage.autoLinkMask= Int.MAX_VALUE
//        viewHolder.itemView.incomingmessage.linksClickable = true
        }


    }
}