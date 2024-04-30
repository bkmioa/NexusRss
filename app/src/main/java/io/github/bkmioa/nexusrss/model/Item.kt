package io.github.bkmioa.nexusrss.model

import android.os.Parcelable
import android.text.format.Formatter
import io.github.bkmioa.nexusrss.App
import io.github.bkmioa.nexusrss.Settings
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
class Item : Comparable<Item>, Parcelable {
    var id: String = ""

    var createdDate: String = ""

    var lastModifiedDate: String = ""

    var name: String = ""

    var smallDescr: String? = null

    var imdb: String = ""

    var imdbRating: String = ""

    var douban: String = ""

    var doubanRating: String = ""

    var author: String = ""

    var size: Long = 0

    var status: Status = Status.DEFAULT

    val sizeText: String
        get() = Formatter.formatShortFileSize(App.instance, size)

    var imageList: List<String> = emptyList()

    val title: String
        get() = smallDescr?.takeIf { it.isNotBlank() } ?: name

    val subTitle: String?
        get() = if (smallDescr.isNullOrBlank()) "" else name

    val link: String
        get() = Settings.BASE_URL + "/detail/$id"

    var pubDate: Date? = null

    val imageUrl: String?
        get() = imageList.firstOrNull()

    var torrentUrl: String? = null

    var descr: String? = null

    var originFileName: String? = null

    var mediainfo: String? = null

    override fun compareTo(other: Item): Int {
        val date1 = pubDate ?: return -1
        val date2 = other.pubDate ?: return 1
        return date1.compareTo(date2)
    }
}
