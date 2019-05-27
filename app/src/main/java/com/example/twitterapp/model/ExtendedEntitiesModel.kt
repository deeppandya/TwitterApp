package com.example.twitterapp.model

import android.os.Parcel
import android.os.Parcelable

data class ExtendedEntitiesModel(val media: ArrayList<MediaModel>) : Parcelable {
    constructor(parcel: Parcel) : this(
        arrayListOf<MediaModel>().apply {
            parcel.readArrayList(MediaModel::class.java.classLoader)
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(media)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EntitiesModel> {
        override fun createFromParcel(parcel: Parcel): EntitiesModel {
            return EntitiesModel(parcel)
        }

        override fun newArray(size: Int): Array<EntitiesModel?> {
            return arrayOfNulls(size)
        }
    }
}