package com.example.twitterapp.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


data class TweetModel(
    @SerializedName("id_str") val idStr: String?,
    val text: String?,
    val entities:EntitiesModel?,
    @SerializedName("extended_entities") val extendedEntities:ExtendedEntitiesModel?,
    @SerializedName("created_at") val createdAt: String?,
    val geo: GeoModel?,
    val user: UserModel?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(EntitiesModel::class.java.classLoader),
        parcel.readParcelable(ExtendedEntitiesModel::class.java.classLoader),
        parcel.readString(),
        parcel.readParcelable(GeoModel::class.java.classLoader),
        parcel.readParcelable(UserModel::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idStr)
        parcel.writeString(text)
        parcel.writeParcelable(entities, flags)
        parcel.writeParcelable(extendedEntities, flags)
        parcel.writeString(createdAt)
        parcel.writeParcelable(geo, flags)
        parcel.writeParcelable(user, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TweetModel> {
        override fun createFromParcel(parcel: Parcel): TweetModel {
            return TweetModel(parcel)
        }

        override fun newArray(size: Int): Array<TweetModel?> {
            return arrayOfNulls(size)
        }
    }
}