package io.github.bkmioa.nexusrss.download

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class DownloadReceiver : BroadcastReceiver() {
    companion object {
        private const val KEY_NOTIFICATION_ID = "notification_id"
        private const val KEY_DOWNLOAD_TASK = "download_task"
        private const val ACTION_DOWNLOAD = "io.github.bkmioa.nexusrss.action_download"

        fun createDownloadIntent(context: Context, notificationId: Int? = null, downloadTask: DownloadTask? = null): Intent {
            return Intent(context, DownloadReceiver::class.java).apply {
                action = ACTION_DOWNLOAD
                putExtra(KEY_NOTIFICATION_ID, notificationId)
                putExtra(KEY_DOWNLOAD_TASK, downloadTask)
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == ACTION_DOWNLOAD) {
            val downloadTask = intent.getParcelableExtra<DownloadTask>(KEY_DOWNLOAD_TASK) ?: return

            RemoteDownloader.download(context, downloadTask)

            if (intent.hasExtra(KEY_NOTIFICATION_ID)) {
                val notificationId = intent.getIntExtra(KEY_NOTIFICATION_ID, 0)
                NotificationManagerCompat.from(context).cancel(notificationId)
            }

        }
    }
}