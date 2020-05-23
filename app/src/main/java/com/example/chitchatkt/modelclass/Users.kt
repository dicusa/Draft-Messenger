package com.example.chitchatkt.modelclass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Users(val imageURl: String, val username: String, val uid: String, val mobile: String): Parcelable{
    constructor() : this("","","","")
}






