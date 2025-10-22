package io.github.bkmioa.nexusrss.search

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import io.github.bkmioa.nexusrss.model.Category
import io.github.bkmioa.nexusrss.model.Option
import io.github.bkmioa.nexusrss.model.RequestData
import kotlinx.coroutines.launch

data class UiState(
    val active: Boolean = true,
    /**
     * 关键字，对应 [RequestData.keyword]
     */
    val keyword: String = "",
    /**
     * 主類別，对应 [RequestData.mode]
     */
    val category: Category = Category.NORMAL,
    /**
     * 類別，对应 [RequestData.categories]
     */
    val categories: Set<Option> = emptySet(),
    val standards: Set<Option> = emptySet(),
    val videoCodecs: Set<Option> = emptySet(),
    val audioCodecs: Set<Option> = emptySet(),
    val processings: Set<Option> = emptySet(),
    val teams: Set<Option> = emptySet(),
    val labels: Set<Option> = emptySet(),
    val discount: Option? = null,
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

    fun setCategory(category: Category) {
        setState {
            copy(category = category)
        }
    }

    fun selectCategory(option: Option, selected: Boolean) = setState {
        if (selected) {
            copy(categories = categories + option)
        } else {
            copy(categories = categories - option)
        }
    }

    fun clearCategory() = setState {
        copy(categories = emptySet())
    }

    fun selectStandard(option: Option, selected: Boolean) = setState {
        if (selected) {
            copy(standards = standards + option)
        } else {
            copy(standards = standards - option)
        }
    }

    fun clearStandard() = setState {
        copy(standards = emptySet())
    }

    fun selectVideoCodec(option: Option, selected: Boolean) = setState {
        if (selected) {
            copy(videoCodecs = videoCodecs + option)
        } else {
            copy(videoCodecs = videoCodecs - option)
        }
    }

    fun clearVideoCodec() = setState {
        copy(videoCodecs = emptySet())
    }


    fun selectAudioCodec(option: Option, selected: Boolean) = setState {
        if (selected) {
            copy(audioCodecs = audioCodecs + option)
        } else {
            copy(audioCodecs = audioCodecs - option)
        }
    }

    fun clearAudioCodec() = setState {
        copy(audioCodecs = emptySet())
    }

    fun selectProcessing(option: Option, selected: Boolean) = setState {
        if (selected) {
            copy(processings = processings + option)
        } else {
            copy(processings = processings - option)
        }
    }

    fun clearProcessing() = setState {
        copy(processings = emptySet())
    }

    fun selectTeam(option: Option, selected: Boolean) = setState {
        if (selected) {
            copy(teams = teams + option)
        } else {
            copy(teams = teams - option)
        }
    }

    fun clearTeam() = setState {
        copy(teams = emptySet())
    }

    fun selectLabel(option: Option, selected: Boolean) = setState {
        if (selected) {
            copy(labels = labels + option)
        } else {
            copy(labels = labels - option)
        }
    }

    fun clearLabel() = setState {
        copy(labels = emptySet())
    }

    fun setDiscount(option: Option?) = setState {
        copy(discount = option)
    }

    fun removeHistory(keywords: String) {
        SearchHistoryStore.remove(keywords)
    }

    fun setKeywords(keywords: String) {
        setState {
            copy(keyword = keywords)
        }
    }

    fun submit() {
        viewModelScope.launch {
            val requestData = buildRequestData()
            val keyword = requestData?.keyword
            var active = awaitState().active

            if (!keyword.isNullOrBlank()) {
                SearchHistoryStore.put(keyword)
                active = false
            }

            setState {
                copy(active = active, requestData = requestData)
            }
        }
    }

    private suspend fun buildRequestData(): RequestData? {
        val state = awaitState()

        if (state.keyword.isBlank()) return null

        return RequestData(
            mode = state.category.path,
            keyword = state.keyword,
            standards = state.standards.map { it.value }.toSet().takeIf { it.isNotEmpty() },
            categories = state.categories.map { it.value }.toSet(),
            videoCodecs = state.videoCodecs.map { it.value }.toSet().takeIf { it.isNotEmpty() },
            audioCodecs = state.audioCodecs.map { it.value }.toSet().takeIf { it.isNotEmpty() },
            processings = state.processings.map { it.value }.toSet().takeIf { it.isNotEmpty() },
            teams = state.teams.map { it.value }.toSet().takeIf { it.isNotEmpty() },
            labelsNew = state.labels.map { it.value }.toSet().takeIf { it.isNotEmpty() },
            discount = state.discount?.takeIf { it.value.isNotEmpty() }?.value

        )
    }
}