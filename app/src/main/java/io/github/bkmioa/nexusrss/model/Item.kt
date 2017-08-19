package io.github.bkmioa.nexusrss.model

import io.github.bkmioa.nexusrss.Settings
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import java.io.Serializable

@Root(strict = false)
class Item : Serializable {
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

    var imageUrl: String? = null
        get() {
            var url = Regex("src=\"((https?)?[^\"]*)\"").find(description)?.groupValues?.get(1)
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
