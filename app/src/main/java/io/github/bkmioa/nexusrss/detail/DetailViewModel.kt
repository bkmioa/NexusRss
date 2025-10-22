package io.github.bkmioa.nexusrss.detail

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import io.github.bkmioa.nexusrss.db.DownloadDao
import io.github.bkmioa.nexusrss.model.DownloadNodeModel
import io.github.bkmioa.nexusrss.model.FileItem
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
    val downloadNodes: List<DownloadNodeModel> = emptyList(),
    val downloadLink: Async<String> = Uninitialized,
    val showFileList: Boolean = false,
    val showCommentList: Boolean = false,
    val fileList: Async<List<FileItem>> = Uninitialized
) : MavericksState {

    constructor(args: DetailArgs) : this(
        id = args.id,
        data = args.data?.let { Success(it) } ?: Uninitialized,
    )
}

class DetailViewModel(initialState: UiState) : MavericksViewModel<UiState>(initialState), KoinComponent {
    private val mtService: MtService by inject()

    private val downloadDao: DownloadDao by inject()

    init {
        fetchItem(initialState.id)

        downloadDao.getAllFlow()
            .setOnEach { copy(downloadNodes = it) }

        onEach(UiState::showFileList) {
            if (awaitState().showFileList) {
                fetchFileList()
            }
        }
    }

    fun fetchItem(id: String) {
        suspend {
            mtService.getDetail(id).data!!
        }.execute(retainValue = UiState::data) {
            copy(data = it)
        }
    }

    suspend fun getDownloadLink(): String? {
        return withContext(Dispatchers.IO) {
            mtService.getDownloadLink(awaitState().id).data
        }
    }

    fun showFileList() {
        setState { copy(showFileList = true) }
    }

    fun fetchFileList() = viewModelScope.launch {
        val state = awaitState()

        if (state.fileList is Loading) return@launch

        if (state.fileList is Success && state.fileList() != null) return@launch

        suspend {
            mtService.getFileList(state.id).data!!
        }.execute(retainValue = UiState::fileList) {
            copy(fileList = it)
        }
    }

    fun hideFileList() {
        setState { copy(showFileList = false) }
    }

    fun showCommentList() {
        setState { copy(showCommentList = true) }
    }

    fun hideCommentList() {
        setState { copy(showCommentList = false) }
    }
}