package io.github.bkmioa.nexusrss.model

import android.content.Context
import android.os.Parcelable
import android.text.format.DateUtils
import android.text.format.Formatter
import io.github.bkmioa.nexusrss.App
import io.github.bkmioa.nexusrss.Settings
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Parcelize
class Item(
    var id: String = "",

    var name: String = "",

    var createdDate: String = "",

    var lastModifiedDate: String = "",

    var smallDescr: String? = null,

    var category: String? = null,

    var source: String? = null,

    var medium: String? = null,

    var standard: String? = null,

    var videoCodec: String? = null,

    var audioCodec: String? = null,

    var team: String? = null,

    var processing: String? = null,

    var imdb: String? = null,

    var imdbRating: String? = null,

    var douban: String? = null,

    var doubanRating: String? = null,

    var anonymous: Boolean = false,

    var author: String? = null,

    var size: Long = 0,

    var status: Status = Status.DEFAULT,

    var imageList: List<String> = emptyList(),

    var torrentUrl: String? = null,

    var descr: String? = null,

    var originFileName: String? = null,

    var mediainfo: String? = null,

    val labelsNew: List<String>? = null,
) : Comparable<Item>, Parcelable {
    val sizeText: String
        get() = Formatter.formatShortFileSize(App.instance, size)

    val title: String
        get() = smallDescr?.takeIf { it.isNotBlank() } ?: name

    val subTitle: String?
        get() = if (smallDescr.isNullOrBlank()) "" else name

    val link: String
        get() = Settings.BASE_URL + "/detail/$id"

    val pubDate: Date?
        get() = try {
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(createdDate)
        } catch (e: Exception) {
            null
        }
    val imageUrl: String?
        get() = imageList.firstOrNull()


    override fun compareTo(other: Item): Int {
        val date1 = pubDate ?: return -1
        val date2 = other.pubDate ?: return 1
        return date1.compareTo(date2)
    }

    fun formatRelativeDateText(): String {
        val date = pubDate ?: return ""
        return DateUtils.getRelativeTimeSpanString(date.time).toString()
    }

    fun getAuthorText(context: Context): String {
        if (anonymous) return "匿名"

        return author ?: "未知用户"
    }
}
