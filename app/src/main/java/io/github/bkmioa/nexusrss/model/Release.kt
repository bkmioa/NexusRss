package io.github.bkmioa.nexusrss.model

import java.util.Date


class Release {
    var id: Int = 0
    var name: String? = null
    var tagName: String? = null
    var body: String? = null
    var htmlUrl: String? = null
    var assetsUrl: String? = null
    var prerelease: Boolean = false
    var publishedAt: Date? = null
}
