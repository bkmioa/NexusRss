package io.github.bkmioa.nexusrss.home

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import io.github.bkmioa.nexusrss.db.AppDatabase
import io.github.bkmioa.nexusrss.model.Tab
import org.koin.core.KoinComponent
import org.koin.core.inject

data class UiState(
    val tabs: List<Tab> = emptyList()
) : MavericksState

class HomeViewModel(initialState: UiState) : MavericksViewModel<UiState>(initialState), KoinComponent {
    private val appDatabase: AppDatabase by inject()

    private val appDao = appDatabase.appDao()

    init {
        appDao.getActivateTabs()
            .setOnEach {
                copy(tabs = it)
            }
    }
}