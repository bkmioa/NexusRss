package io.github.bkmioa.nexusrss.model

class ThreadList {
    //@Selector(".torrents", index = 0, converter = ThreadListConverter::class)
    //var list: List<Item>? = null

    //class ThreadListConverter : ElementConverter<List<Item>> {
    //    override fun convert(node: Element, selector: Selector): List<Item>? {
    //        val trs = node.child(0).children() ?: return null
    //        val items = ArrayList<Item>()
    //        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    //
    //        for (tr in trs) {
    //            if (tr.select(".colhead").isNotEmpty()) continue
    //
    //            val item = Item()
    //            val tds = tr.children()
    //            item.imageUrl = tr.select(".torrentimg img").first()?.absUrl("src")
    //
    //            val selectTitle = tr.select("a[title]")
    //            item.title = selectTitle.attr("title")
    //            item.link = selectTitle.first()?.absUrl("href")
    //            item.subTitle = tr.select(".embedded:has(a[title])").takeIf { it.select("br").isNotEmpty() }?.first()?.childNodes()?.last()?.toString() ?: item.title
    //            item.pubDate = dateFormat.parse(tds[3].select("span").attr("title"))
    //            item.sizeText = tds[4].text()
    //            item.description = ""
    //            item.author = tds[9].select("b").text()
    //            item.category = tds[0].select("img").attr("title")
    //            item.torrentUrl = tr.select("a:has(.download)").first()?.absUrl("href")
    //            items.add(item)
    //        }
    //        return items
    //    }
    //}
}