package io.github.bkmioa.nexusrss.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "tab")
@Parcelize
data class Tab(
    val title: String,

    /**
     * 类型
     */
    val mode: String,

    /**
     * 類別
     */
    val categories: Set<String> = emptySet(),

    /**
     * 解析度
     */
    val standards: Set<String>? = null,

    /**
     * 視頻編碼
     */
    val videoCodecs: Set<String>? = null,

    /**
     * 音頻編碼
     */
    val audioCodecs: Set<String>? = null,

    /**
     * 地區
     */
    val processings: Set<String>? = null,

    /**
     * 製作組
     */
    val teams: Set<String>? = null,

    val labels: Set<String>? = null,

    /**
     * 促銷
     */
    val discount: String? = null,

    /**
     * 不限 0
     * 僅活躍 1
     * 僅死種 2
     */
    @ColumnInfo(defaultValue = "1")
    val visible: Int = 1,

    val order: Int = 0,

    val isShow: Boolean = true,

    val columnCount: Int = 1,

    @PrimaryKey(autoGenerate = true)
    val id: Long? = null
) : Parcelable, Comparable<Tab> {


    override fun compareTo(other: Tab) = this.order.compareTo(other.order)

    fun makeKey(): String {
        return hashCode().toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Tab

        if (visible != other.visible) return false
        if (columnCount != other.columnCount) return false
        if (id != other.id) return false
        if (title != other.title) return false
        if (mode != other.mode) return false
        if (categories != other.categories) return false
        if (standards != other.standards) return false
        if (videoCodecs != other.videoCodecs) return false
        if (audioCodecs != other.audioCodecs) return false
        if (processings != other.processings) return false
        if (teams != other.teams) return false
        if (labels != other.labels) return false
        if (discount != other.discount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = visible
        result = 31 * result + columnCount
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + title.hashCode()
        result = 31 * result + mode.hashCode()
        result = 31 * result + categories.hashCode()
        result = 31 * result + (standards?.hashCode() ?: 0)
        result = 31 * result + (videoCodecs?.hashCode() ?: 0)
        result = 31 * result + (audioCodecs?.hashCode() ?: 0)
        result = 31 * result + (processings?.hashCode() ?: 0)
        result = 31 * result + (teams?.hashCode() ?: 0)
        result = 31 * result + (labels?.hashCode() ?: 0)
        result = 31 * result + (discount?.hashCode() ?: 0)
        return result
    }

}

