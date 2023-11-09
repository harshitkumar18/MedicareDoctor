package com.example.medicaredoctor.Models


import android.os.Parcel
import android.os.Parcelable

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
    val hospital: String = "",
    val address: String = "",
    val appointment: ArrayList<Appointment> = ArrayList()
) : Parcelable {
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
        source.createTypedArrayList(Appointment.CREATOR) ?: ArrayList()
    )

    override fun describeContents() = 0

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
        writeString(hospital)
        writeString(address)
        writeTypedList(appointment)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Doctor> = object : Parcelable.Creator<Doctor> {
            override fun createFromParcel(source: Parcel): Doctor = Doctor(source)
            override fun newArray(size: Int): Array<Doctor?> = arrayOfNulls(size)
        }
    }
}
