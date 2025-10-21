package io.github.bkmioa.nexusrss.download.edit

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import io.github.bkmioa.nexusrss.db.AppDatabase
import io.github.bkmioa.nexusrss.model.DownloadNodeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class UiState(
    val id: Long?,
    val name: String = "",
    val type: String = DownloadNodeModel.TYPE_QBITTORRENT,
    val host: String = "",
    val userName: String = "",
    val password: String = "",
    val defaultPath: String = ""
) : MavericksState {
    constructor(id: String?) : this(id = id?.toLongOrNull())
}

class EditDownloadNodeViewModel(initialState: UiState) : MavericksViewModel<UiState>(initialState), KoinComponent {
    private val appDatabase: AppDatabase by inject()

    private val downloadDao = appDatabase.downloadDao()

    init {
        if (initialState.id != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val model = downloadDao.getOne(initialState.id)
                setState {
                    copy(
                        id = model.id,
                        name = model.name,
                        type = model.type,
                        host = model.host,
                        userName = model.userName,
                        password = model.password,
                        defaultPath = model.defaultPath ?: ""
                    )
                }
            }
        }
    }

    suspend fun save() {
        val state = awaitState()
        val model = DownloadNodeModel(
            id = state.id,
            name = state.name,
            type = state.type,
            host = state.host,
            userName = state.userName,
            password = state.password,
            defaultPath = state.defaultPath.takeIf { it.isNotBlank() }
        )
        downloadDao.addOrUpdateNode(model)
    }

    fun setType(type: String) {
        setState { copy(type = type) }
    }

    fun setName(name: String) {
        setState { copy(name = name) }
    }

    fun setHost(host: String) {
        setState { copy(host = host) }
    }

    fun setUserName(userName: String) {
        setState { copy(userName = userName) }
    }

    fun setPassword(password: String) {
        setState { copy(password = password) }
    }

    fun setPath(path: String) {
        setState { copy(defaultPath = path) }
    }
}