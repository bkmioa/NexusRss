package io.github.bkmioa.nexusrss.download

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DownloadTask(
    val downloadNode: DownloadNode,
    val torrentUrl: String,
    val path: String?
) : Parcelable {
}