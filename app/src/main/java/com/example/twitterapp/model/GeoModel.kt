package com.example.twitterapp.model

import android.os.Parcel
import android.os.Parcelable

data class GeoModel(val type: String?, val coordinates: Array<Any>?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readArray(Double::class.java.classLoader)
    )

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(type)
        dest?.writeArray(coordinates)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GeoModel> {
        override fun createFromParcel(parcel: Parcel): GeoModel {
            return GeoModel(parcel)
        }

        override fun newArray(size: Int): Array<GeoModel?> {
            return arrayOfNulls(size)
        }
    }
}