package com.example.twitterapp.model

import android.os.Parcel
import android.os.Parcelable

data class VideoInfoModel(val variants:ArrayList<VariantsModel>) : Parcelable{
    constructor(parcel: Parcel) : this(
        arrayListOf<VariantsModel>().apply {
            parcel.readArrayList(VariantsModel::class.java.classLoader)
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(variants)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VideoInfoModel> {
        override fun createFromParcel(parcel: Parcel): VideoInfoModel {
            return VideoInfoModel(parcel)
        }

        override fun newArray(size: Int): Array<VideoInfoModel?> {
            return arrayOfNulls(size)
        }
    }
}