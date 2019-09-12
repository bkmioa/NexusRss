package io.github.bkmioa.nexusrss.model

import io.github.bkmioa.nexusrss.Settings
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import java.io.Serializable

@Root(strict = false)
class Item : Serializable {
    companion object {
        private val IGNORED_IMAGE_PATTERN = arrayOf(
                "pic/trans\\.gif"
                , "pic/smilies/"
        ).joinToString("|").toRegex()
    }

    @set:Element(name = "title", required = false)
    @get:Element(name = "title", required = false)
    var originTitle: String? = null
        set(value) {
            if (value != null) {
                subTitle = Regex("\\[([^\\]].*)\\]").find(value)?.groupValues?.get(1)
                title = value.removeSuffix("[$subTitle]")
            }
        }
        get

    var title: String? = null

    var subTitle: String? = null

    @field:Element(required = false)
    lateinit var link: String

    @field:Element(required = false)
    lateinit var pubDate: String

    @field:Element(required = false)
    lateinit var description: String

    @field:Element(required = false)
    lateinit var author: String

    @field:Element(required = false)
    lateinit var guid: String

    @field:Element(required = false)
    var enclosure: Data? = null

    @field:Element(required = false)
    lateinit var category: String

    @field:Element(required = false)
    lateinit var comments: String

    val imageUrl: String? by lazy { resolveImageUrl() }

    private fun resolveImageUrl(): String? {
        var url = Regex("<img[^>]+src=\"([^\">]+)\"").findAll(description)
                .map { it.groupValues[1] }
                .find { !it.contains(IGNORED_IMAGE_PATTERN) }
        if (url != null && !url.startsWith("http")) {
            url = Settings.BASE_URL + "/" + url
        }
        return url
    }

    @Root
    class Data : Serializable {
        @field:Attribute(required = false)
        var length: Long? = null

        @field:Attribute(required = false)
        var type: String? = null

        @field:Attribute(required = false)
        var url: String? = null
    }
}
