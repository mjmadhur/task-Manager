package com.example.trelloc.models

import android.os.Parcel
import android.os.Parcelable

data class Board (
    val name:String="",
    val image:String="",
val createdBy:String="",
val assignedTo:ArrayList<String> = ArrayList(),
var documentId:String="",
var tasklist:ArrayList<Task> = ArrayList()
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Task.CREATOR)!!
    ) {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(parcel: Parcel, p1: Int)= with(parcel) {
       parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeString(createdBy)
parcel.writeStringList(assignedTo)
        parcel.writeString(documentId)
        parcel.writeTypedList(tasklist)
    }

    companion object CREATOR : Parcelable.Creator<Board> {
        override fun createFromParcel(parcel: Parcel): Board {
            return Board(parcel)
        }

        override fun newArray(size: Int): Array<Board?> {
            return arrayOfNulls(size)
        }
    }
}