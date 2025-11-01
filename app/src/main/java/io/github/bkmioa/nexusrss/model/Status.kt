package io.github.bkmioa.nexusrss.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Status(
    var id: String = "",

    var toppingLevel: Int = 0,

    var toppingEndTime: String? = null,

    var seeders: String = "",

    var leechers: String = "",

    var comments: String = "",
) : Parcelable {
    companion object {
        val DEFAULT = Status()
    }

    val isTopped: Boolean
        get() = toppingLevel != 0
}