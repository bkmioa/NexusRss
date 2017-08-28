package io.github.bkmioa.nexusrss.viewmodel

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import io.github.bkmioa.nexusrss.App
import io.github.bkmioa.nexusrss.Settings
import io.github.bkmioa.nexusrss.model.Tab
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainViewModel @Inject constructor(app: App) : AndroidViewModel(app) {

    val tabs = MutableLiveData<Array<Tab>>()

    fun requestRefresh() {
        tabs.value = Settings.tabs.toTypedArray()
    }
}