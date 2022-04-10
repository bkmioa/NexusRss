package io.github.bkmioa.nexusrss.download

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.github.bkmioa.nexusrss.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object RemoteDownloader {
    fun download(context: Context, downloadNode: DownloadNode, torrentUrl: String, path: String? = null) {
        download(context, DownloadTask(downloadNode, torrentUrl, path))
    }

    fun download(context: Context, downloadTask: DownloadTask) {
        val app = context.applicationContext

        Toast.makeText(context, R.string.downloading, Toast.LENGTH_SHORT).show()
        val ignore = downloadTask.downloadNode.download(downloadTask.torrentUrl, downloadTask.path)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Toast.makeText(app, it, Toast.LENGTH_SHORT).show()
            }, {
                it.printStackTrace()
                showFailure(context, downloadTask, it.message)
                Toast.makeText(app, it.message, Toast.LENGTH_SHORT).show()
            })
    }

    private fun showFailure(context: Context, task: DownloadTask, message: String?) {
        val code = task.torrentUrl.hashCode()
        val downloadIntent = DownloadReceiver.createDownloadIntent(context, code, task)
        var flag = PendingIntent.FLAG_UPDATE_CURRENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flag = flag or PendingIntent.FLAG_IMMUTABLE
        }
        val retryIntent= PendingIntent.getBroadcast(context, code, downloadIntent, flag)
        val retryAction = NotificationCompat.Action.Builder(R.drawable.ic_refresh, "retry", retryIntent)
            .build()

        val notification = NotificationCompat.Builder(context, createChannelIfNeeded(context))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(0)
            .setContentTitle("Download Failure")
            .setContentText(message)
            .setAutoCancel(true)
            .addAction(retryAction)
            .build()

        NotificationManagerCompat.from(context)
            .notify(code, notification)
    }

    private fun createChannelIfNeeded(context: Context): String {
        val channelId = "download_state"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Download State", NotificationManager.IMPORTANCE_DEFAULT)
            NotificationManagerCompat.from(context).createNotificationChannel(channel)
        }
        return channelId
    }

}