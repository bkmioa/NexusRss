package io.github.bkmioa.nexusrss.base

import androidx.annotation.IntRange
import androidx.paging.Pager as PagingPager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class Pager<Key : Any, Value : Any> @JvmOverloads constructor(
    coroutineScope: CoroutineScope,

    /**
     * 每页加载的数量，默认为 15
     */
    pageSize: Int = 15,

    /**
     * 到达列表末尾/头部多少条数据时开始加载下一页/上一页，默认为 5
     */
    @IntRange(from = 0)
    prefetchDistance: Int = 5,

    /**
     * 第一次加载的数量，默认和 [pageSize] 一致
     */
    @IntRange(from = 1)
    initialLoadSize: Int = pageSize,

    /**
     * 第一次加载使用的 key
     */
    initialKey: Key? = null,

    /**
     * 列表支持的最大数量，当超过这个数量后，超过列表数据不会吐给下游，在加载超大列表时可减轻下游展示压力
     * 默认最大数列无限制
     */
    @IntRange(from = 2)
    maxSize: Int = PagingConfig.MAX_SIZE_UNBOUNDED,

    /**
     * 跳转阈值，当跳转的条数超过这个阈值时，会直接跳转到目标页，而不是逐页加载
     * 默认跳转阈值无限制
     */
    jumpThreshold: Int = PagingSource.LoadResult.Page.COUNT_UNDEFINED,

    enablePlaceholders: Boolean = false,
    /**
     * 提供数据源，每次需要返回一个新的实例
     */
    pagingFactory: PagingSourceFactory<Key, Value>,
) {
    fun interface PagingSourceFactory<Key : Any, Value : Any> {
        fun create(): PagingSource<Key, Value>
    }

    private val pager: PagingPager<Key, Value> = PagingPager(
        config = PagingConfig(
            pageSize = pageSize,
            prefetchDistance = prefetchDistance,
            enablePlaceholders = enablePlaceholders,
            initialLoadSize = initialLoadSize,
            maxSize = maxSize,
            jumpThreshold = jumpThreshold,
        ),
        initialKey = initialKey,
        pagingSourceFactory = {
            pagingFactory.create().also {
                currentPagingSource = it
            }
        })

    var currentPagingSource: PagingSource<Key, Value>? = null
        private set

    val flow: Flow<PagingData<Value>> = pager.flow.cachedIn(coroutineScope)

    fun invalidate() {
        currentPagingSource?.invalidate()
    }
}