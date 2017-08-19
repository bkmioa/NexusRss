package io.github.bkmioa.nexusrss.model

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(strict = false)
class Rss {
    @field:ElementList(name = "channel", entry = "item", required = false, type = Item::class, empty = false)
    lateinit var items: List<Item>

    override fun toString(): String {
        return "Rss(items=$items)"
    }


}
