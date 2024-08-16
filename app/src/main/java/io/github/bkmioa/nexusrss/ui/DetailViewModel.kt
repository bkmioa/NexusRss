package io.github.bkmioa.nexusrss.ui

import android.os.Bundle
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import io.github.bkmioa.nexusrss.model.Item
import io.github.bkmioa.nexusrss.repository.MtService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class UiState(
    val id: String,
    val data: Async<Item> = Uninitialized,
    val downloadLink: Async<String> = Uninitialized
) : MavericksState {
    constructor(args: Bundle) : this(id = args.getString("id")!!)
}

class DetailViewModel(initialState: UiState) : MavericksViewModel<UiState>(initialState), KoinComponent {
    private val mtService: MtService by inject()

    init {
        fetchItem(initialState.id)
    }

    fun fetchItem(id: String) {
        suspend {
            mtService.getDetail(id).data!!
        }.execute {
            copy(data = it)
        }
    }

    suspend fun getDownloadLink(): String? {
        return withContext(Dispatchers.IO) {
            mtService.getDownloadLink(awaitState().id).data
        }
    }
}