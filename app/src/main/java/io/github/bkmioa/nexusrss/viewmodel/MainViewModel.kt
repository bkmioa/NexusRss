package io.github.bkmioa.nexusrss.viewmodel

import io.github.bkmioa.nexusrss.App
import io.github.bkmioa.nexusrss.base.BaseViewModel
import io.github.bkmioa.nexusrss.db.AppDatabase
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

    fun tabs() = appDao.getAllTab()

    fun checkNewVersion() = githubService.releaseList()
}