package com.example.medicaredoctor.Models

import android.os.Parcel
import android.os.Parcelable

data class SlotAvailability(
    val date: String = "",
    val totalAvailableSlots: String = "",
    var totalBookedSlots: String = "",
    var remainingSlots: String = ""
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: ""
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(date)
        writeString(totalAvailableSlots)
        writeString(totalBookedSlots)
        writeString(remainingSlots)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SlotAvailability> = object : Parcelable.Creator<SlotAvailability> {
            override fun createFromParcel(source: Parcel): SlotAvailability = SlotAvailability(source)
            override fun newArray(size: Int): Array<SlotAvailability?> = arrayOfNulls(size)
        }
    }
}
