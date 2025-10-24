package io.github.bkmioa.nexusrss.download.list

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import io.github.bkmioa.nexusrss.db.AppDatabase
import io.github.bkmioa.nexusrss.model.DownloadNodeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class UiState(
    val downloadNodes: List<DownloadNodeModel> = emptyList(),
    val undoDeleteNode: DownloadNodeModel? = null,
) : MavericksState

class DownloadSettingsViewModel(initialState: UiState) : MavericksViewModel<UiState>(initialState), KoinComponent {


    private val appDatabase: AppDatabase by inject()

    private val downloadDao = appDatabase.downloadDao()

    init {
        downloadDao.getAllFlow().setOnEach {
            copy(downloadNodes = it)
        }
    }

    fun duplicateNode(node: DownloadNodeModel) {
        downloadDao.addOrUpdateNode(node.copy(id = null))
    }

    fun deleteNode(node: DownloadNodeModel) {
        downloadDao.delete(node)
        setState { copy(undoDeleteNode = node) }
    }

    suspend fun performUndoDelete() {
        awaitState().undoDeleteNode?.let {
            withContext(Dispatchers.IO) {
                downloadDao.addOrUpdateNode(it)
            }
        }
        setState {
            copy(undoDeleteNode = null)
        }
    }

    fun resetUndoDelete() {
        setState {
            copy(undoDeleteNode = null)
        }
    }
}