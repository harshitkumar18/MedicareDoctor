package com.example.medicaredoctor.Models


import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi

data class Doctor(
    var documentId: String = "",
    val name: String = "",
    val email: String = "",
    val image: String = "",
    val mobile: Long = 0,
    val about: String = "",
    var timing: ArrayList<Timing> = ArrayList(),
    val speciality: String = "",
    val interest: String = "",
    val degree: String = "",
    val gender: String = "",
    val hospital: String = "",
    val address: String = "",
    val available: Boolean = true,
    val appointment: ArrayList<Appointment> = ArrayList(),
    val bookingappointment: ArrayList<Appointment> = ArrayList(),
    val expiredappointment: ArrayList<Appointment> = ArrayList(),

    val timeslots: ArrayList<String> = ArrayList()
) : Parcelable {
    @RequiresApi(Build.VERSION_CODES.Q)
    constructor(source: Parcel) : this(
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: "",
        source.readLong(),
        source.readString() ?: "",
        source.createTypedArrayList(Timing.CREATOR) ?: ArrayList(),
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: "",
        source.readBoolean() ?: true,
        source.createTypedArrayList(Appointment.CREATOR) ?: ArrayList(),
        source.createTypedArrayList(Appointment.CREATOR) ?: ArrayList(),
        source.createTypedArrayList(Appointment.CREATOR) ?: ArrayList(),
        ArrayList<String>().apply {
            source.readList(this, String::class.java.classLoader)
        }

    )

    override fun describeContents() = 0

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(documentId)
        writeString(name)
        writeString(email)
        writeString(image)
        writeLong(mobile)
        writeString(about)
        writeTypedList(timing)
        writeString(speciality)
        writeString(interest)
        writeString(degree)
        writeString(gender)
        writeString(hospital)
        writeString(address)
        writeBoolean(available)
        writeTypedList(appointment)
        writeTypedList(bookingappointment)
        writeTypedList(expiredappointment)
        writeList(timeslots)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Doctor> = object : Parcelable.Creator<Doctor> {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun createFromParcel(source: Parcel): Doctor = Doctor(source)
            override fun newArray(size: Int): Array<Doctor?> = arrayOfNulls(size)
        }
    }
}
