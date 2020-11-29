package io.github.bkmioa.nexusrss.download

import android.os.Parcelable
import io.reactivex.Single

interface DownloadNode : Parcelable {
    val host: String
    val userName: String
    val password: String
    val defaultPath: String?

    /**
     * @return success with string or error with error message
     */
    fun download(torrentUrl: String, path: String? = null): Single<String>
}