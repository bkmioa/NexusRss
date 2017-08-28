package io.github.bkmioa.nexusrss.viewmodel

import io.github.bkmioa.nexusrss.App
import io.github.bkmioa.nexusrss.base.BaseViewModel
import io.github.bkmioa.nexusrss.db.AppDatabase
import io.github.bkmioa.nexusrss.model.Tab
import javax.inject.Inject

class TabListViewModel @Inject constructor(app: App) : BaseViewModel(app) {

    @Inject lateinit var appDatabase: AppDatabase

    private val appDao by lazy { appDatabase.appDao() }

    fun tabs() = appDao.getAllTab()

    fun addTab(tab: Tab) {
        appDao.addTab(tab)
    }

    fun removeTab(tab: Tab) {
        appDao.deleteTab(tab)
    }

    fun update(vararg tabs: Tab) {
        appDao.updateTab(*tabs)
    }
}