package io.github.bkmioa.nexusrss.model

import android.os.Parcel
import android.os.Parcelable

data class Tab(val title: String, val options: Array<Option>, val order: Int) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readParcelableArray(Option::class.java.classLoader) as Array<Option>,
            source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(title)
        writeParcelableArray(options, 0)
        writeInt(order)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Tab> = object : Parcelable.Creator<Tab> {
            override fun createFromParcel(source: Parcel): Tab = Tab(source)
            override fun newArray(size: Int): Array<Tab?> = arrayOfNulls(size)
        }
    }
}

