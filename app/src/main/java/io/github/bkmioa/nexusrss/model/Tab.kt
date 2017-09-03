package io.github.bkmioa.nexusrss.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import java.util.*

@Entity(tableName = Tab.TABLE_NAME)
data class Tab(var title: String,
               var options: Array<String>,
               var order: Int = 0,
               var isShow: Boolean = true) : Parcelable, Comparable<Tab> {

    @PrimaryKey(autoGenerate = true)
    var id: Long? = null

    override fun compareTo(other: Tab) = this.order.compareTo(other.order)

    constructor(source: Parcel) : this(
            source.readString(),
            source.createStringArray(),
            source.readInt(),
            source.readInt() == 1) {
        id = source.readValue(Long::class.java.classLoader) as Long?
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(title)
        writeStringArray(options)
        writeInt(order)
        writeInt((if (isShow) 1 else 0))
        writeValue(id)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Tab

        if (id != other.id) return false
        if (title != other.title) return false
        if (!Arrays.equals(options, other.options)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + Arrays.hashCode(options)
        result = 31 * result + (id?.hashCode() ?: 0)
        return result
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

