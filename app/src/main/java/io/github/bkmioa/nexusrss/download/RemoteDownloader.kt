package io.github.bkmioa.nexusrss.download

import android.content.Context
import android.widget.Toast
import io.github.bkmioa.nexusrss.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RemoteDownloader(val context: Context) {
    fun download(downloadNode: DownloadNode, torrentUrl: String, path: String? = null) {
        Toast.makeText(context, R.string.downloading, Toast.LENGTH_SHORT).show()
        val ignore = downloadNode.download(torrentUrl, path)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }, {
                it.printStackTrace()
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            })
    }
}