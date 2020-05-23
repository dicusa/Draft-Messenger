package com.example.chitchatkt.modelclass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class MessagesSent(
    val id: String,
    val message: String,
    val fromid: String,
    val toid: String,
    val timestamp: MutableMap<String,String>,
    val type: String):

    Parcelable {

    constructor() : this(id="",message = "",fromid = "",toid = "",timestamp = mutableMapOf(),type = "")
}