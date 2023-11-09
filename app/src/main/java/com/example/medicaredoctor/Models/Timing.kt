package com.example.medicaredoctor.Models

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList  // Import ArrayList class

data class Timing(
    var time: String = "",
    val dateSlotMap: ArrayList<SlotAvailability> = ArrayList()
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString() ?: "",
        source.createTypedArrayList(SlotAvailability.CREATOR) ?: ArrayList()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(time)
        writeTypedList(dateSlotMap)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Timing> = object : Parcelable.Creator<Timing> {
            override fun createFromParcel(source: Parcel): Timing = Timing(source)
            override fun newArray(size: Int): Array<Timing?> = arrayOfNulls(size)
        }
    }
}
