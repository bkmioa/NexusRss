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
    val categories: List<String> = emptyList(),

    /**
     * 解析度
     */
    val standards: List<String>? = null,

    /**
     * 視頻編碼
     */
    val videoCodecs: List<String>? = null,

    /**
     * 音頻編碼
     */
    val audioCodecs: List<String>? = null,

    /**
     * 地區
     */
    val processings: List<String>? = null,

    /**
     * 製作組
     */
    val teams: List<String>? = null,


    /**
     * 標記
     * DIY 1
     * 国配 2
     * 中字 4
     */
    val labels: Int? = null,

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
                mode = tab.path,
                categories = resolveCategories(tab.options),
                standards = resolveStandards(tab.options),
            )
        }

        private fun resolveStandards(options: Array<String>?): List<String>? {
            return options?.filter { it.startsWith("standard_") }?.map { it.split("_")[1] }
        }

        private fun resolveCategories(options: Array<String>?) = (options ?: emptyArray()).filter { it.startsWith("cat_") }.map { it.split("_")[1] }
    }
}
