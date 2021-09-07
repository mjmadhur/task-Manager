package com.example.trelloc.models

import android.os.Parcel
import android.os.Parcelable

data class User (
    val id:String="",
    val email:String="",
    val name:String="",
    val image:String="",
    val mobile:Long=0,
    val fcmToken:String="",
var selected:Boolean=false

        ):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!

    ) {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel, p1: Int): Unit = with(p0) {
        writeString(id)
        writeString(email)
        writeString(name)
        writeString(image)
        writeLong(mobile)
        writeString(fcmToken)
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}