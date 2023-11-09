package com.example.medicaredoctor.Models

import android.os.Parcel
import android.os.Parcelable

data class Appointment(
    var id: String = "",
    var user_id : String = "",
    var date:String ="",
    var time:String = "",
    var status: String = "",
    var timestamp: Long = 0 // Store timestamp as Long


) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readLong()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(user_id)
        writeString(date)
        writeString(time)
        writeString(status)
        writeLong(timestamp)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Appointment> = object : Parcelable.Creator<Appointment> {
            override fun createFromParcel(source: Parcel): Appointment = Appointment(source)
            override fun newArray(size: Int): Array<Appointment?> = arrayOfNulls(size)
        }
    }
}
