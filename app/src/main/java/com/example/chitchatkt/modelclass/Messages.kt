package com.example.chitchatkt.modelclass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Messages(val id: String, val message: String, val fromid: String, val toid: String, val timestamp: Long, val type: String):
    Parcelable {
    constructor() : this("","","","",0L,"")
}