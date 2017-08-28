package io.github.bkmioa.nexusrss.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import java.util.*

@Entity(tableName = Tab.TABLE_NAME)
data class Tab(
        var title: String,
        var options: Array<String>,
        var order: Int,
        var isShow: Boolean = true)
    : Parcelable, Comparable<Tab> {

    @PrimaryKey(autoGenerate = true)
    var id: Long? = null

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Tab

        if (id != other.id) return false
        if (!Arrays.equals(options, other.options)) return false
        if (isShow != other.isShow) return false

        return true
    }

    override fun compareTo(other: Tab) = this.order.compareTo(other.order)

    constructor(source: Parcel) : this(
            source.readString(),
            source.createStringArray(),
            source.readInt(),
            1 == source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(title)
        writeStringArray(options)
        writeInt(order)
        writeInt((if (isShow) 1 else 0))
    }

    companion object {
        const val TABLE_NAME = "tab"

        @JvmField
        val CREATOR: Parcelable.Creator<Tab> = object : Parcelable.Creator<Tab> {
            override fun createFromParcel(source: Parcel): Tab = Tab(source)
            override fun newArray(size: Int): Array<Tab?> = arrayOfNulls(size)
        }
    }
}

