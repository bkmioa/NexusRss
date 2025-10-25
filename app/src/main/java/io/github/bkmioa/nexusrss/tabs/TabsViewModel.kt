package io.github.bkmioa.nexusrss.tabs

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import io.github.bkmioa.nexusrss.db.AppDatabase
import io.github.bkmioa.nexusrss.model.Tab
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    suspend fun reorderTabs(from: Int, to: Int) {
        setState {
            val mutable = tabs.toMutableList()
            mutable.add(to, mutable.removeAt(from))
            val newList = mutable.mapIndexed { index, tab -> tab.copy(order = index) }
            copy(tabs = newList)
        }
        appDao.updateTab(* awaitState().tabs.toTypedArray())
    }

    suspend fun performUndoDelete() {
        awaitState().undoDelete?.let {
            // Re-add the tab with a new id
            // because of https://slack-chats.kotlinlang.org/t/28924907/hi-everyone-wave-i-m-running-into-a-frustrating-issue-with-s
            appDao.addTab(it.copy(id = null))
        }
        setState {
            copy(undoDelete = null)
        }

    }

    fun resetUndoDelete() {
        setState {
            copy(undoDelete = null)
        }
    }
}