package io.github.bkmioa.nexusrss.option

import android.os.Parcelable
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import io.github.bkmioa.nexusrss.model.Mode
import io.github.bkmioa.nexusrss.model.Option
import io.github.bkmioa.nexusrss.model.RequestData
import kotlinx.parcelize.Parcelize

data class OptionUiState(
    /**
     * 主類別，对应 [RequestData.mode]
     */
    val mode: Mode = Mode.NORMAL,
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
) : MavericksState {

    constructor(initArgs: OptionInitArgs) : this(
        mode = initArgs.mode,
        categories = initArgs.categories,
        standards = initArgs.standards,
        videoCodecs = initArgs.videoCodecs,
        audioCodecs = initArgs.audioCodecs,
        processings = initArgs.processings,
        teams = initArgs.teams,
        labels = initArgs.labels,
        discount = initArgs.discount,
    )
}

@Parcelize
class OptionInitArgs(
    val mode: Mode = Mode.NORMAL,
    val categories: Set<Option> = emptySet(),
    val standards: Set<Option> = emptySet(),
    val videoCodecs: Set<Option> = emptySet(),
    val audioCodecs: Set<Option> = emptySet(),
    val processings: Set<Option> = emptySet(),
    val teams: Set<Option> = emptySet(),
    val labels: Set<Option> = emptySet(),
    val discount: Option? = null,
) : Parcelable

class OptionViewModel(initialState: OptionUiState) : MavericksViewModel<OptionUiState>(initialState) {
    fun setMode(mode: Mode) {
        setState {
            copy(mode = mode)
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

    fun selectLabel(value: Option, selected: Boolean) = setState {
        if (selected) {
            copy(labels = labels + value)
        } else {
            copy(labels = labels - value)
        }
    }

    fun clearLabel() = setState {
        copy(labels = emptySet())
    }

    fun setDiscount(option: Option?, selected: Boolean) = setState {
        copy(discount = if (selected) option else null)
    }
}