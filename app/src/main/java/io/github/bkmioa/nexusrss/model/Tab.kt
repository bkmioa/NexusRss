package io.github.bkmioa.nexusrss.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "tab")
@Parcelize
data class Tab(
        var title: String,
        var path: String,
        var options: Array<String>,
        var order: Int = 0,
        var isShow: Boolean = true,
        var columnCount: Int = 1,
        @PrimaryKey(autoGenerate = true)
        var id: Long? = null) : Parcelable, Comparable<Tab> {


    override fun compareTo(other: Tab) = this.order.compareTo(other.order)


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Tab

        if (id != other.id) return false
        if (title != other.title) return false
        if (path != other.path) return false
        if (columnCount != other.columnCount) return false
        if (!options.contentEquals(other.options)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + options.contentHashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + columnCount.hashCode()
        return result
    }

}

