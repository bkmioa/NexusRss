package io.github.bkmioa.nexusrss.tabs

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import io.github.bkmioa.nexusrss.db.AppDatabase
import io.github.bkmioa.nexusrss.model.Tab
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class UiState(
    val tabs: List<Tab> = emptyList()
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

    fun removeTab(tab: Tab) {
        appDao.deleteTab(tab)
    }

    fun update(tab: Tab) {
        appDao.updateTab(tab)
    }
}