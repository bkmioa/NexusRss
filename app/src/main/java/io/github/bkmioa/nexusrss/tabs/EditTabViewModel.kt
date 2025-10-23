package io.github.bkmioa.nexusrss.tabs

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import io.github.bkmioa.nexusrss.db.AppDatabase
import io.github.bkmioa.nexusrss.model.Mode
import io.github.bkmioa.nexusrss.model.Option
import io.github.bkmioa.nexusrss.model.Tab
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class EditTabUiState(
    val tab: Tab,
    val placeholder: Unit
) : MavericksState {
    constructor(tab: Tab) : this(tab, Unit)
}

class EditTabViewModel(initialState: EditTabUiState) : MavericksViewModel<EditTabUiState>(initialState), KoinComponent {

    private val appDateBase: AppDatabase by inject()

    private val appDao = appDateBase.appDao()

    fun save(
        mode: Mode,
        categories: Set<Option>,
        standards: Set<Option>?,
        videoCodecs: Set<Option>?,
        audioCodecs: Set<Option>?,
        processings: Set<Option>?,
        teams: Set<Option>?,
        labels: Set<Option>?,
        discount: Option?
    ) = viewModelScope.launch(Dispatchers.IO) {
        val state = awaitState()
        val tab = state.tab.copy(
            mode = mode.mode,
            categories = categories.map { it.value }.toSet(),
            standards = standards?.map { it.value }?.toSet()?.takeIf { it.isNotEmpty() },
            videoCodecs = videoCodecs?.map { it.value }?.toSet()?.takeIf { it.isNotEmpty() },
            audioCodecs = audioCodecs?.map { it.value }?.toSet()?.takeIf { it.isNotEmpty() },
            processings = processings?.map { it.value }?.toSet()?.takeIf { it.isNotEmpty() },
            teams = teams?.map { it.value }?.toSet()?.takeIf { it.isNotEmpty() },
            labels = labels?.map { it.value }?.toSet()?.takeIf { it.isNotEmpty() },
            discount = discount?.value
        )
        appDao.addTab(tab)
    }

    fun updateTitle(title: String) {
        setState { copy(tab = tab.copy(title = title)) }
    }
}