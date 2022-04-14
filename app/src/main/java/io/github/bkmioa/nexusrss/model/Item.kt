package io.github.bkmioa.nexusrss.model

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import java.io.Serializable
import java.util.*

@Root(strict = false)
class Item : Serializable, Comparable<Item> {
    companion object {
        private val IGNORED_IMAGE_PATTERN = arrayOf(
            "pic/trans\\.gif", "pic/smilies/"
        ).joinToString("|").toRegex()
    }

    var title: String? = null

    var subTitle: String? = null

    @field:Element(required = false)
    var link: String? = null

    @field:Element(required = false)
    var pubDate: Date? = null

    @field:Element(required = false)
    lateinit var description: String

    @field:Element(required = false)
    lateinit var author: String

    @field:Element(required = false)
    var enclosure: Data? = null

    @field:Element(required = false)
    lateinit var category: String

    var imageUrl: String? = null

    var sizeText: String? = null

    var torrentUrl: String? = null

    @Root
    class Data : Serializable {
        @field:Attribute(required = false)
        var length: Long? = null

        @field:Attribute(required = false)
        var type: String? = null

        @field:Attribute(required = false)
        var url: String? = null
    }

    override fun compareTo(other: Item): Int {
        val date1 = pubDate ?: return -1
        val date2 = other.pubDate ?: return 1
        return date1.compareTo(date2)
    }
}
