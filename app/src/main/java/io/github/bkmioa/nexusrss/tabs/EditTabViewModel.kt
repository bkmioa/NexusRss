package io.github.bkmioa.nexusrss.tabs

import android.content.Context
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.db.AppDatabase
import io.github.bkmioa.nexusrss.model.Mode
import io.github.bkmioa.nexusrss.model.Option
import io.github.bkmioa.nexusrss.model.Tab
import io.github.bkmioa.nexusrss.widget.ToastMessage
import io.github.bkmioa.nexusrss.widget.asToastMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class EditTabUiState(
    val tab: Tab,
    val toast: ToastMessage? = null,
    val placeholder: Unit
) : MavericksState {
    constructor(tab: Tab) : this(tab, placeholder = Unit)
}

class EditTabViewModel(initialState: EditTabUiState) : MavericksViewModel<EditTabUiState>(initialState), KoinComponent {
    private val context: Context by inject()

    private val appDateBase: AppDatabase by inject()

    private val appDao = appDateBase.appDao()

    suspend fun save(
        mode: Mode,
        categories: Set<Option>,
        standards: Set<Option>?,
        videoCodecs: Set<Option>?,
        audioCodecs: Set<Option>?,
        processings: Set<Option>?,
        teams: Set<Option>?,
        labels: Set<Option>?,
        discount: Option?
    ): Boolean = try {
        val state = awaitState()
        if (state.tab.title.isBlank()) {
            throw IllegalStateException(context.getString(R.string.tab_title_empty))
        }

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
        withContext(Dispatchers.IO) {
            appDao.addTab(tab)
        }
        true
    } catch (e: Exception) {
        val toast = e.message.asToastMessage()
        setState { copy(toast = toast) }
        false
    }

    fun updateTitle(title: String) {
        setState { copy(tab = tab.copy(title = title.trim())) }
    }
}