package com.example.chitchatkt.reciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.core.content.ContextCompat.getSystemService
import java.lang.NullPointerException

class NetworkChangeReciever : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            if(connectivityReciverListner != null){
               connectivityReciverListner!!.onNetworkConnectionChanged(isOnline(context))
            }

        }catch (e: NullPointerException)
        {
            e.printStackTrace()
        }


    }

    private fun isOnline(context: Context?): Boolean {

    try{

    val cm: ConnectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo: NetworkInfo? = cm.activeNetworkInfo
    return (networkInfo != null && networkInfo.isConnected)

    }
    catch (e: NullPointerException){
    e.printStackTrace()
    return false
}
    }

    interface ConnectivityRecieverListner{

        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

    companion object{

        var connectivityReciverListner: ConnectivityRecieverListner? = null
    }
}