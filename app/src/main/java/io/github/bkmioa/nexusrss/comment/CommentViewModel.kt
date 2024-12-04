package io.github.bkmioa.nexusrss.comment

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import io.github.bkmioa.nexusrss.base.Pager
import io.github.bkmioa.nexusrss.model.Comment
import io.github.bkmioa.nexusrss.model.CommentRequestBody
import io.github.bkmioa.nexusrss.model.ItemList
import io.github.bkmioa.nexusrss.model.MemberInfo
import io.github.bkmioa.nexusrss.model.MemberRequestBody
import io.github.bkmioa.nexusrss.model.Result
import io.github.bkmioa.nexusrss.repository.MtService
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class UiState(
    val relationId: String
) : MavericksState

class CommentViewModel(initialState: UiState) : MavericksViewModel<UiState>(initialState), KoinComponent {
    companion object {
        private const val TAG = "CommentViewModel"
    }

    private val mtService: MtService by inject()

    private val pager = Pager(
        viewModelScope,
        pageSize = 20,
        initialKey = 1
    ) {
        runBlocking {
            CommentListSource(mtService, awaitState().relationId)
        }
    }

    val pagerFlow = pager.flow

    init {
        Log.d(TAG, "init() called")
    }
}

class CommentListSource(val mtService: MtService, val relationId: String) : PagingSource<Int, Comment>() {
    override fun getRefreshKey(state: PagingState<Int, Comment>): Int {
        return 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Comment> = try {
        Log.d("CommentListSource", "load() called with: params = $params")
        val page = params.key ?: 1
        val res = getComments(page)
        if (res.code != 0) {
            LoadResult.Error(Exception(res.message))
        } else {
            val list = res.data?.data ?: emptyList()
            LoadResult.Page(data = list, prevKey = null, nextKey = if (list.isEmpty()) null else page + 1)
        }
    } catch (e: Exception) {
        LoadResult.Error(e)
    }

    private suspend fun getComments(page: Int): Result<ItemList<Comment>> {
        val req = CommentRequestBody(relationId = relationId, pageNumber = page)
        val res = mtService.getComments(req)
        inflateMemberInfo(res.data?.data)
        return res
    }

    private suspend fun inflateMemberInfo(data: List<Comment>?) {
        data ?: return

        inflateFromCache(data)

        val ids = data.filter { it.member == null }
            .map { it.author }
            .distinct()

        if (ids.isEmpty()) return

        val requestBody = MemberRequestBody(ids)
        val result = mtService.getMemberInfos(requestBody).data ?: return

        memberCache.putAll(result)

        inflateFromCache(data)
    }

    private fun inflateFromCache(data: List<Comment>) {
        data.filter { it.member == null }
            .forEach { comment ->
                memberCache[comment.author]?.let { comment.member = it }
            }
    }
}

val memberCache = HashMap<String, MemberInfo>()