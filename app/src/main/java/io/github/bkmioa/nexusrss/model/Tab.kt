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

    companion object {
        val EMPTY = Tab(title = "", mode = Mode.NORMAL.mode)
    }

    override fun compareTo(other: Tab) = this.order.compareTo(other.order)

    fun makeKey(): String {
        return hashCode().toString()
    }
}

