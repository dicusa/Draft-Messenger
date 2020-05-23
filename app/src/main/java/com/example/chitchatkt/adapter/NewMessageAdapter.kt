package com.example.chitchatkt.adapter

import com.example.chitchatkt.modelclass.Users
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.new_message_inflate.view.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chitchatkt.R


class NewMessageAdapter(val user: Users) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username.text = user.username
        viewHolder.itemView.mobile.text = user.mobile
        var imagestring: String = user.imageURl
//        var bitmap: Bitmap = convertStringtoBitmap(imagestring)
        Glide.with(viewHolder.itemView.context).asBitmap().load(imagestring).apply(RequestOptions().override(50,50)).into(viewHolder.itemView.profileimage)
    }

//    private fun convertStringtoBitmap(imagestring: String): Bitmap {
//
//        try {
//            var encodeByte = Base64.decode(imagestring, Base64.DEFAULT)
//
//            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
//        } catch (e: Exception) {
//            e.message
//            Log.e("error","the string was not converted to bitmap")
//            return null
//        }
//
//    }

    override fun getLayout(): Int {
        return R.layout.new_message_inflate
    }
}
