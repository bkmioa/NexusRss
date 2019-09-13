package io.github.bkmioa.nexusrss.viewmodel

import android.app.Application
import io.github.bkmioa.nexusrss.base.BaseViewModel
import io.github.bkmioa.nexusrss.db.AppDatabase
import io.github.bkmioa.nexusrss.model.Tab
import org.koin.core.inject

class TabListViewModel(app: Application) : BaseViewModel(app) {

    private val appDatabase: AppDatabase by inject()

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