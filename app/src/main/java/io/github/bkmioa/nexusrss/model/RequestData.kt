package io.github.bkmioa.nexusrss.model

import android.os.Parcelable
import androidx.annotation.IntRange
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize


@Parcelize
data class RequestData(
    val mode: String = "normal",

    /**
     * 关键字
     */
    val keyword: String? = null,

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


    val labelsNew: Set<String>? = null,

    /**
     * 促銷
     */
    val discount: String? = null,

    /**
     * 不限 0
     * 僅活躍 1
     * 僅死種 2
     */
    val visible: Int = 1,

    val pageSize: Int = 20,

    @IntRange(from = 1)
    @IgnoredOnParcel
    val pageNumber: Int = 1
) : Parcelable {
    companion object {
        fun from(tab: Tab): RequestData {
            return RequestData(
                mode = tab.mode,
                categories = tab.categories,
                standards = tab.standards,
                videoCodecs = tab.videoCodecs,
                audioCodecs = tab.audioCodecs,
                processings = tab.processings,
                teams = tab.teams,
                labelsNew = tab.labels,
                discount = tab.discount,
                visible = tab.visible
            )
        }

    }
}
