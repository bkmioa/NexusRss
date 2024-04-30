package io.github.bkmioa.nexusrss.tabs

import android.util.Log
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import io.github.bkmioa.nexusrss.db.AppDatabase
import io.github.bkmioa.nexusrss.model.Tab
import org.koin.core.KoinComponent
import org.koin.core.inject

data class UiState(
    val tabs: Array<Tab> = emptyArray()
) : MavericksState

class TabsViewModel(initialState: UiState) : MavericksViewModel<UiState>(initialState), KoinComponent {
    companion object {
         const val TAG = "TabsViewModel"
    }

    private val appDateBase: AppDatabase by inject()

    private val appDao = appDateBase.appDao()

    init {
        appDao.getAllTabFlow().setOnEach {
            copy(tabs = it)
        }
    }

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