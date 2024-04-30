package io.github.bkmioa.nexusrss.detail

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import io.github.bkmioa.nexusrss.db.DownloadDao
import io.github.bkmioa.nexusrss.model.DownloadNodeModel
import io.github.bkmioa.nexusrss.model.Item
import io.github.bkmioa.nexusrss.repository.MtService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject

data class UiState(
    val id: String,
    val data: Async<Item> = Uninitialized,
    val downloadNodes: List<DownloadNodeModel> = emptyList(),
    val downloadLink: Async<String> = Uninitialized
) : MavericksState {

    constructor(args: Pair<String, Item?>) : this(
        id = args.first,
        data = args.second?.let { Success(it) } ?: Uninitialized,
    )
}

class DetailViewModel(initialState: UiState) : MavericksViewModel<UiState>(initialState), KoinComponent {
    private val mtService: MtService by inject()

    private val downloadDao: DownloadDao by inject()

    init {
        fetchItem(initialState.id)

        downloadDao.getAllFlow()
            .setOnEach { copy(downloadNodes = it) }
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