package io.github.bkmioa.nexusrss.list

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.filter
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import io.github.bkmioa.nexusrss.Settings
import io.github.bkmioa.nexusrss.base.Pager
import io.github.bkmioa.nexusrss.model.Item
import io.github.bkmioa.nexusrss.model.RequestData
import io.github.bkmioa.nexusrss.model.Tab
import io.github.bkmioa.nexusrss.repository.MtService
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class UiState(
    val requestData: RequestData,
    val collapsePinedItems: Boolean = true
) : MavericksState {
    constructor(requestData: RequestData) : this(requestData, true)
}

class ListViewModel(initialState: UiState) : MavericksViewModel<UiState>(initialState), KoinComponent {
    companion object {
        private const val TAG = "ListViewModel"
    }

    private val mtService: MtService by inject()

    private val pager = Pager(
        viewModelScope,
        pageSize = Settings.PAGE_SIZE,
        initialKey = 1
    ) {
        runBlocking {
            ListDataSource(mtService, awaitState().requestData)
        }
    }

    val pagerFlow = pager.flow

    init {
        Log.d(TAG, "init called: $initialState")
    }

    override fun onCleared() {
        super.onCleared()
        withState {
            Log.d(TAG, "onCleared() called: ${it}")
        }
    }

    fun refresh() {
        pager.invalidate()
    }

    fun request(requestData: RequestData) = withState {
        val current = it.requestData
        if (current != requestData) {
            setState {
                copy(requestData = requestData)
            }
            pager.invalidate()
        }
    }

    fun setCollapsePinedItems(collapse: Boolean) {
        setState {
            copy(collapsePinedItems = collapse)
        }
    }
}

class ListDataSource(val mtService: MtService, val requestData: RequestData) : PagingSource<Int, Item>() {
    private val allList = ArrayList<Item>()

    override fun getRefreshKey(state: PagingState<Int, Item>): Int {
        return 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Item> = try {
        val page = params.key ?: 1
        val req = requestData.copy(pageNumber = page, pageSize = Settings.PAGE_SIZE)
        val res = mtService.search(req)
        if (res.code != 0) {
            LoadResult.Error(Exception(res.message))
        } else {
            val list = (res.data?.data ?: emptyList())
                .filter { item -> allList.none { it.id == item.id } }
            allList.addAll(list)

            LoadResult.Page(data = list, prevKey = null, nextKey = if (list.isEmpty()) null else page + 1)
        }
    } catch (e: Exception) {
        LoadResult.Error(e)
    }
}