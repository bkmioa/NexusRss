package io.github.bkmioa.nexusrss.download.ui

import android.app.Application
import io.github.bkmioa.nexusrss.base.BaseViewModel
import io.github.bkmioa.nexusrss.db.DownloadDao
import io.github.bkmioa.nexusrss.model.DownloadNodeModel
import org.koin.core.inject

class DownloadNodeListViewModel(app: Application) : BaseViewModel(app) {

    private val downloadDao: DownloadDao by inject()

    fun getAllLiveData() = downloadDao.getAllLiveData()

    fun addDownloadNode(downloadNode: DownloadNodeModel) {
        downloadDao.addOrUpdateNode(downloadNode)
    }

    fun delete(downloadNode: DownloadNodeModel) {
        downloadDao.delete(downloadNode)
    }
}