package io.github.bkmioa.nexusrss.model

import java.util.*

class Release {
    var id: Int = 0
    lateinit var name: String
    lateinit var body: String
    lateinit var assetsUrl: String
    var prerelease: Boolean = false
    lateinit var publishedAt: Date
    lateinit var assets: Array<Asset>

    class Asset {
        companion object {
            const val TYPE_APK = "application/vnd.android.package-archive"
        }

        lateinit var browserDownloadUrl: String
        lateinit var contentType: String
        var size: Int = 0
    }
}
