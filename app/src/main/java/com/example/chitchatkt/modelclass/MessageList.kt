package com.example.chitchatkt.modelclass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class MessageList(val messages: Messages, val income: Boolean):
    Parcelable {
    constructor() : this(Messages(),true)
}