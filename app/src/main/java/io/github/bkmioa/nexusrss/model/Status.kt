package io.github.bkmioa.nexusrss.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Status : Parcelable {
    companion object {
        val DEFAULT = Status()
    }

    var id: String = ""

    var toppingLevel: Int = 0

    var toppingEndTime: String? = null

    var seeders: String = ""

    var leechers: String = ""

    var comments: String = ""
}