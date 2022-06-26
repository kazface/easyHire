package com.example.fasthire

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

data class Job(
    val type: String? = "", val title: String? ="", val companyName: String?="", val location: String?="", val period: String?="", val salary: Long=0, val userId: String?="", val createdDateUnix: Long? = 0, val
    jobDescription: String? ="", val vaccination: Int? = 0, val skills: ArrayList<Any>? = arrayListOf(), var id: String? = "", var saved: Int? = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readArrayList(null),
        parcel.readString(),
        parcel.readInt()
    ) {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<Job> {
        override fun createFromParcel(parcel: Parcel): Job {
            return Job(parcel)
        }

        override fun newArray(size: Int): Array<Job?> {
            return arrayOfNulls(size)
        }
    }


}



