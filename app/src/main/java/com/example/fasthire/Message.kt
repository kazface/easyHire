package com.example.fasthire

import android.os.Parcel
import android.os.Parcelable

data class Message(val cv: String? = null, var fromId: String? = null, var fromName: String? = null, val job: String? = null, val text: String? = null, val toId: String? = null, val toName:String? = null, val message:String? = null, val unixTime: Int? = null,
                   var fromEmail: String? = null, val toEmail: String? = null) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
        ) {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<Message> {
        override fun createFromParcel(parcel: Parcel): Message {
            return Message(parcel)
        }

        override fun newArray(size: Int): Array<Message?> {
            return arrayOfNulls(size)
        }
    }


}



