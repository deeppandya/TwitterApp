package com.example.twitterapp.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class MediaModel(@SerializedName("media_url") val mediaUrl: String?, val type: String?, @SerializedName("video_info") val videoInfo: VideoInfoModel?) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(VideoInfoModel::class.java.classLoader)
    )

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(mediaUrl)
        dest?.writeString(type)
        dest?.writeParcelable(videoInfo, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MediaModel> {
        override fun createFromParcel(parcel: Parcel): MediaModel {
            return MediaModel(parcel)
        }

        override fun newArray(size: Int): Array<MediaModel?> {
            return arrayOfNulls(size)
        }
    }
}