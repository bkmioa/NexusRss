package io.github.bkmioa.nexusrss.tabs

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import io.github.bkmioa.nexusrss.db.AppDatabase
import io.github.bkmioa.nexusrss.model.Tab
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class UiState(
    val tabs: List<Tab> = emptyList(),
    val undoDelete: Tab? = null,
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
        setState { copy(undoDelete = tab) }
    }

    fun update(tab: Tab) {
        appDao.updateTab(tab)
    }

    fun reorderTabs(from: Int, to: Int) {
        setState {
            val mutable = tabs.toMutableList()
            val remove = mutable.removeAt(from)
            mutable.add(to, remove)
            val newList = mutable.mapIndexed { index, tab -> tab.copy(order = index) }
            copy(tabs = newList)
        }
        withState {
            appDao.updateTab(*it.tabs.toTypedArray())
        }
    }

    fun performUndoDelete() {
        setState {
            undoDelete?.let { appDao.addTab(it) }
            copy(undoDelete = null)
        }

    }
}