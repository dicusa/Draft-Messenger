package com.example.chitchatkt.notificationservices

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.content.Context.NOTIFICATION_SERVICE
import android.app.NotificationManager
import android.R
import android.content.Context
import androidx.core.app.NotificationCompat



class FirebaseNotificationService: FirebaseMessagingService() {


    override fun onNewToken(onNewToken: String?) {
        super.onNewToken(onNewToken)
        Log.e("onNewToken", "" + onNewToken!!)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        Log.e("remoteMessage", "" + remoteMessage!!.data["data"])
        val builder = NotificationCompat.Builder(applicationContext)
        builder.setSmallIcon(R.drawable.sym_def_app_icon)
        builder.setContentTitle("Notification bar")
        builder.setContentText("" + remoteMessage.data["data"])
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 0
        manager.notify(notificationId, builder.build())
        //server key AIzaSyCCrwuPQHuUSCqnL63rSHRkm4AJpafavcw
    }

}