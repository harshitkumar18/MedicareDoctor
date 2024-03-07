package com.example.medicaredoctor.Models

import android.os.Parcel
import android.os.Parcelable
import com.example.medicaredoctor.AppointmentUser

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val image: String = "",
    val mobile: Long = 0,
    val age: Long = 0,
    val bloodgroup: String = "",
    val height: Long = 0,
    val weight: Long = 0,
    val diabetic: String = "",
    val gender: String = "",
    val fcmToken: String = "",
    val userappointment: ArrayList<AppointmentUser> = ArrayList(),
    val bookingappointment: ArrayList<AppointmentUser> = ArrayList(),
    val expiredappointment: ArrayList<AppointmentUser> = ArrayList()
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readLong(),
        source.readLong(),
        source.readString()!!,
        source.readLong(),
        source.readLong(),
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.createTypedArrayList(AppointmentUser.CREATOR) ?: ArrayList(),
        source.createTypedArrayList(AppointmentUser.CREATOR) ?: ArrayList(),
        source.createTypedArrayList(AppointmentUser.CREATOR) ?: ArrayList()

    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(name)
        writeString(email)
        writeString(image)
        writeLong(mobile)
        writeLong(age)
        writeString(bloodgroup)
        writeLong(height)
        writeLong(weight)
        writeString(diabetic)
        writeString(gender)
        writeString(fcmToken)
        writeTypedList(userappointment)
        writeTypedList(bookingappointment)
        writeTypedList(expiredappointment)

    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User = User(source)
            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
        }
    }
}
