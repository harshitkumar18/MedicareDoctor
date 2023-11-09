package com.example.medicaredoctor

import android.os.Parcel
import android.os.Parcelable

data class AppointmentUser(
    var id: String = "",
    var doctor_id : String = "",
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
        writeString(doctor_id)
        writeString(date)
        writeString(time)
        writeString(status)
        writeLong(timestamp)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<AppointmentUser> = object : Parcelable.Creator<AppointmentUser> {
            override fun createFromParcel(source: Parcel): AppointmentUser = AppointmentUser(source)
            override fun newArray(size: Int): Array<AppointmentUser?> = arrayOfNulls(size)
        }
    }
}
