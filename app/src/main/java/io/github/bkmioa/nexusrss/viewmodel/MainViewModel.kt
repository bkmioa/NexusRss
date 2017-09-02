package io.github.bkmioa.nexusrss.viewmodel

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import io.github.bkmioa.nexusrss.App
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.base.BaseViewModel
import io.github.bkmioa.nexusrss.db.AppDatabase
import io.github.bkmioa.nexusrss.model.Release
import io.github.bkmioa.nexusrss.repository.GithubService
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MainViewModel @Inject constructor(app: App) : BaseViewModel(app) {

    @Inject
    lateinit var appDatabase: AppDatabase

    @Inject
    lateinit var githubService: GithubService

    private val appDao by lazy {
        appDatabase.appDao()
    }

    private var downloadReceiver: DownloadReceiver? = null

    fun tabs() = appDao.getAllTab()


    fun checkNewVersion() = githubService.releaseList()

    fun downloadNewVersion(release: Release) {
        if (!release.assets.isEmpty()) {
            val apkAsset = release.assets.first { it.contentType == Release.Asset.TYPE_APK }

            val downloadManager = app.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(Uri.parse(apkAsset.browserDownloadUrl))
            request.apply {
                setTitle(app.getString(R.string.app_name))
                setDescription(release.name)
                setMimeType(apkAsset.contentType)
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            }
            val requestId = downloadManager.enqueue(request)

            downloadReceiver = DownloadReceiver(requestId)
            app.registerReceiver(downloadReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }


    }

    private inner class DownloadReceiver(val requestId: Long) : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val downloadCompletedId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
            if (requestId != downloadCompletedId) {
                return
            }

            val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val query = DownloadManager.Query()
            query.setFilterById(requestId)
            val cursor = downloadManager.query(query)
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(columnIndex)) {
                    val uriString = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        Uri.parse("file://" + cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME)))
                    } else {
                        downloadManager.getUriForDownloadedFile(requestId)
                    }

                    val installIntent = Intent(Intent.ACTION_VIEW)
                    installIntent.setDataAndType(uriString, Release.Asset.TYPE_APK)
                    installIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION

                    app.startActivity(installIntent)
                }
            }
            downloadReceiver ?: app.unregisterReceiver(downloadReceiver)
            downloadReceiver = null
        }

    }

    override fun onCleared() {
        super.onCleared()
        downloadReceiver ?: app.unregisterReceiver(downloadReceiver)
        downloadReceiver = null
    }
}