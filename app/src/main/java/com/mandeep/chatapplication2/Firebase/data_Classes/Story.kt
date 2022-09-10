package com.mandeep.chatapplication2.Firebase.data_Classes

import android.os.Parcel
import android.os.Parcelable

data class Story(val user_id:String?,val username:String?,val story_image:String?,val story_timing:String?,val story_doc_id:String?):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Story> {
        override fun createFromParcel(parcel: Parcel): Story {
            return Story(parcel)
        }

        override fun newArray(size: Int): Array<Story?> {
            return arrayOfNulls(size)
        }
    }
}
