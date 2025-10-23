package io.github.bkmioa.nexusrss.search

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import io.github.bkmioa.nexusrss.model.Mode
import io.github.bkmioa.nexusrss.model.Option
import io.github.bkmioa.nexusrss.model.RequestData
import kotlinx.coroutines.launch

data class UiState(
    val active: Boolean = true,
    /**
     * 关键字，对应 [RequestData.keyword]
     */
    val keyword: String = "",
    val requestData: RequestData? = null,
    val history: List<String> = emptyList()
) : MavericksState {
    val filteredList: List<String>
        get() = if (keyword.isBlank()) history
        else history.filter { it.contains(keyword, ignoreCase = true) }
}

class SearchViewModel(initialState: UiState) : MavericksViewModel<UiState>(initialState) {
    init {
        SearchHistoryStore.getAllFlow().setOnEach {
            copy(history = it)
        }
    }

    fun setActive(active: Boolean) {
        setState {
            copy(active = active)
        }
    }

    fun removeHistory(keywords: String) {
        SearchHistoryStore.remove(keywords)
    }

    fun setKeywords(keywords: String) {
        setState {
            copy(keyword = keywords)
        }
    }

    fun submit(
        mode: Mode,
        categories: Set<Option>,
        standards: Set<Option>,
        videoCodecs: Set<Option>,
        audioCodecs: Set<Option>,
        processings: Set<Option>,
        teams: Set<Option>,
        labels: Set<Option>,
        discount: Option?
    ) = viewModelScope.launch {
        val state = awaitState()

        val keyword = state.keyword
        var active = state.active

        if (keyword.isNotBlank()) {
            SearchHistoryStore.put(keyword)
            active = false
        }

        val requestData = RequestData(
            mode = mode.mode,
            keyword = keyword,
            categories = categories.toList().map { it.value }.toSet(),
            standards = standards.toList().map { it.value }.toSet().takeIf { it.isNotEmpty() },
            videoCodecs = videoCodecs.toList().map { it.value }.toSet().takeIf { it.isNotEmpty() },
            audioCodecs = audioCodecs.toList().map { it.value }.toSet().takeIf { it.isNotEmpty() },
            processings = processings.toList().map { it.value }.toSet().takeIf { it.isNotEmpty() },
            teams = teams.toList().map { it.value }.toSet().takeIf { it.isNotEmpty() },
            labelsNew = labels.map { it.value }.toSet().takeIf { it.isNotEmpty() },
            discount = discount?.value?.takeIf { it.isNotEmpty() }
        )

        setState {
            copy(active = active, requestData = requestData)
        }
    }
}