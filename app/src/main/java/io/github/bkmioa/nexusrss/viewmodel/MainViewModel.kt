package io.github.bkmioa.nexusrss.viewmodel

import android.app.Application
import io.github.bkmioa.nexusrss.base.BaseViewModel
import io.github.bkmioa.nexusrss.db.AppDatabase
import io.github.bkmioa.nexusrss.repository.GithubService
import org.koin.core.component.inject

class MainViewModel(app: Application) : BaseViewModel(app) {

    private val appDatabase: AppDatabase by inject()

    private val githubService: GithubService by inject()

    private val appDao by lazy {
        appDatabase.appDao()
    }

    fun tabs() = appDao.getAllTab()

    fun checkNewVersion() = githubService.releaseList()
}