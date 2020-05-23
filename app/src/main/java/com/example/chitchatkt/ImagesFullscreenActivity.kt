package com.example.chitchatkt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chitchatkt.databinding.ActivityChatScreenBinding
import com.example.chitchatkt.databinding.ActivityImagesFullscreenBinding
import kotlinx.android.synthetic.main.outgoing_message_inflate.view.*

class ImagesFullscreenActivity : AppCompatActivity() {
    lateinit var view: ActivityImagesFullscreenBinding
    lateinit var imagebit: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = DataBindingUtil.setContentView(this,R.layout.activity_images_fullscreen)
        imagebit = intent.getStringExtra("imagebit")
        Glide.with(this).asBitmap().load(imagebit).apply(RequestOptions().override(1024,1024)).into( view.imageView)
        onClick()
    }

    private fun onClick() {
        view.backbuttonimage.setOnClickListener{
            finish()
        }

//        view.sharebuttonimage.setOnClickListener {
//            var intent = Intent(this,NewMessagesActivity::class.java)
//            intent.putExtra("ImageForward",imagebit)
//            startActivity(intent)
//            finish()
//        }
    }
}
