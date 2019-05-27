package com.example.twitterapp.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class VariantsModel(val bitrate: Int, @SerializedName("content_type") val contentType: String?, val url: String?) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(bitrate)
        parcel.writeString(contentType)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VariantsModel> {
        override fun createFromParcel(parcel: Parcel): VariantsModel {
            return VariantsModel(parcel)
        }

        override fun newArray(size: Int): Array<VariantsModel?> {
            return arrayOfNulls(size)
        }
    }
}