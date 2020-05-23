package com.example.chitchatkt.apputils

import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference



class AppController : MultiDexApplication() {



    companion object {
        var mFirebaseDatabase: DatabaseReference? = null
        var mFirebaseDatabaseagain: DatabaseReference? = null
    lateinit var mFirebaseInstance: FirebaseDatabase
    }
    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        mFirebaseInstance = FirebaseDatabase.getInstance()
        mFirebaseInstance.setPersistenceEnabled(true)

    }
}