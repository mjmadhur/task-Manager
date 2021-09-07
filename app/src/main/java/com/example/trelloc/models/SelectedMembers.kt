package com.projemanag.model

import android.os.Parcel
import android.os.Parcelable

// TODO (Step 1: Add a data model class for the Selected Members class.)
// START
data class SelectedMembers(
    val id: String = "",
    val image: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun describeContents() :Int{
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(image)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SelectedMembers> =
            object : Parcelable.Creator<SelectedMembers> {
                override fun createFromParcel(source: Parcel): SelectedMembers =
                    SelectedMembers(source)

                override fun newArray(size: Int): Array<SelectedMembers?> = arrayOfNulls(size)
            }
    }
}
// END