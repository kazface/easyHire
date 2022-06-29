package com.example.fasthire

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.util.ArrayList

data class Cv( val email: String? = null, val description: String? = null,
    val fullName: String? = null, val location: String? = null, val phone: String? = null, val skills: ArrayList<Any>? = arrayListOf(), val title: String? = null, var id: String? = null, val vaccination: Int? = null, var saved: Int? = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readArrayList(null),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<Cv> {
        override fun createFromParcel(parcel: Parcel): Cv {
            return Cv(parcel)
        }

        override fun newArray(size: Int): Array<Cv?> {
            return arrayOfNulls(size)
        }
    }


}



