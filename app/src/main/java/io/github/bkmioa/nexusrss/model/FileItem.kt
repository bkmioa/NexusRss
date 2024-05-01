package io.github.bkmioa.nexusrss.model

import android.text.format.Formatter
import io.github.bkmioa.nexusrss.App

class FileItem {
    var id: String = ""

    //var torrent: String = ""

    //var createdDate: String = ""

    var name: String = ""

    var size: Long = 0

    val sizeText: String
        get() = Formatter.formatShortFileSize(App.instance, size)
}