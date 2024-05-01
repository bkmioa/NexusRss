package io.github.bkmioa.nexusrss.search

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel

data class UiState(
    val active: Boolean = true,
    val text: String = "",
    val searchText: String = "",
    val history: List<String> = emptyList()
) : MavericksState {
    val filteredList: List<String>
        get() = if (text.isBlank()) history
        else history.filter { it.contains(text, ignoreCase = true) }
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

    fun onQueryChange(query: String) {
        setState {
            copy(text = query)
        }
    }

    fun removeHistory(keywords: String) {
        SearchHistoryStore.remove(keywords)
    }

    fun onSearch(keywords: String) {
        setState {
            copy(active = false, text = keywords, searchText = keywords)
        }
        SearchHistoryStore.put(keywords)
    }
}