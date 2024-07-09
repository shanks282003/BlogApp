package com.example.bloglikho.Model

import android.os.Parcel
import android.os.Parcelable

data class BlogItemModel(
    var heading: String? ="null",
    val username: String? ="null",
    val date: String? ="null",
    val userid: String?="null",
    var post: String? ="null",
    var likecount: Int=0,
    val profileImage: String? ="null",
    var isSaved: Boolean=false,
    var postId: String="null",
    val likedBy: MutableList<String>?=null
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readByte()!=0.toByte(),
        parcel.readString()?:"null"
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(heading)
        parcel.writeString(username)
        parcel.writeString(date)
        parcel.writeString(userid)
        parcel.writeString(post)
        parcel.writeInt(likecount)
        parcel.writeString(profileImage)
        parcel.writeByte(if (isSaved) 1 else 0)
        parcel.writeString(postId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BlogItemModel> {
        override fun createFromParcel(parcel: Parcel): BlogItemModel {
            return BlogItemModel(parcel)
        }

        override fun newArray(size: Int): Array<BlogItemModel?> {
            return arrayOfNulls(size)
        }
    }
}
