package io.github.bkmioa.nexusrss.model

import java.io.Serializable
import java.util.*

class Item : Serializable, Comparable<Item> {
    var title: String? = null

    var subTitle: String? = null

    var link: String? = null

    var pubDate: Date? = null

    lateinit var description: String

    lateinit var author: String

    lateinit var category: String

    var imageUrl: String? = null

    var sizeText: String? = null

    var torrentUrl: String? = null

    override fun compareTo(other: Item): Int {
        val date1 = pubDate ?: return -1
        val date2 = other.pubDate ?: return 1
        return date1.compareTo(date2)
    }
}
