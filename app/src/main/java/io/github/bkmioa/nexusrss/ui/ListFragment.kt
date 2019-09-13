package io.github.bkmioa.nexusrss.ui

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.graphics.Rect
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.epoxy.EpoxyAdapter
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.Settings
import io.github.bkmioa.nexusrss.base.BaseFragment
import io.github.bkmioa.nexusrss.common.Scrollable
import io.github.bkmioa.nexusrss.dp2px
import io.github.bkmioa.nexusrss.model.Item
import io.github.bkmioa.nexusrss.model.ListData
import io.github.bkmioa.nexusrss.model.LoadingState
import io.github.bkmioa.nexusrss.ui.viewModel.ItemViewModel_
import io.github.bkmioa.nexusrss.ui.viewModel.LoadMoreViewModel_
import io.github.bkmioa.nexusrss.viewmodel.RssListViewModel
import kotlinx.android.synthetic.main.fragment_list.*
import org.koin.android.ext.android.inject
import java.util.concurrent.atomic.AtomicBoolean


class ListFragment : BaseFragment(), Scrollable {

    private val listAdapter = ListAdapter()

    private var options: Array<String>? = null
    private var queryText: String? = null
    private var withSearch = false
    private var columnCount = 1

    private val isLoadingMore = AtomicBoolean(false)

    private lateinit var listViewModel: RssListViewModel

    companion object {
        fun newInstance(options: Array<String>? = null, withSearch: Boolean = false, columnCount: Int = 1): ListFragment {
            val fragment = ListFragment()
            val args = Bundle()
            options?.let { args.putStringArray("options", options) }
            args.putBoolean("withSearch", withSearch)
            args.putInt("columnCount", columnCount)
            fragment.arguments = args
            return fragment
        }

    }

    override fun scrollToTop() {
        if (recyclerView.canScrollVertically(-1)) {
            if ((recyclerView.layoutManager as LinearLayoutManager)
                            .findFirstVisibleItemPosition() < Settings.PAGE_SIZE) {
                recyclerView.smoothScrollToPosition(0)
            } else {
                recyclerView.scrollToPosition(0)
            }
        } else {
            refresh()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            options = getStringArray("options")
            withSearch = getBoolean("withSearch", false)
            columnCount = getInt("columnCount", 1)
        }
        listViewModel = ViewModelProviders.of(this).get(RssListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = activity?: throw IllegalStateException()

        val gridLayoutManager = GridLayoutManager(context, columnCount)
        listAdapter.spanCount = gridLayoutManager.spanCount
        gridLayoutManager.spanSizeLookup = listAdapter.spanSizeLookup

        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = listAdapter

        val dp4 = context.dp2px(4)
        if (columnCount > 1) {
            recyclerView.setPadding(0, dp4, 0, 0)
        }
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val position = recyclerView.getChildAdapterPosition(view)
                val count = listAdapter.itemCount - 1
                val params = view.layoutParams as GridLayoutManager.LayoutParams
                val spanSize = params.spanSize
                val spanIndex = params.spanIndex
                if (columnCount == 1) {
                    if (position != count) {
                        outRect.bottom = context.dp2px(10)
                    }
                } else {
                    if (spanSize == 1) {
                        outRect.set(dp4, dp4, dp4, dp4)
                        if (spanIndex == 0) outRect.left = 2 * dp4
                        if (spanIndex == columnCount - 1) outRect.right = 2 * dp4
                    } else {
                        outRect.top = 2 * dp4
                    }
                }
            }
        })

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)

        swipeRefreshLayout.setOnRefreshListener {
            refresh()
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    query(false)
                }
            }
        })

        listViewModel.loadingState.observe(this, Observer<LoadingState> {
            it ?: throw IllegalStateException()

            swipeRefreshLayout.isRefreshing = it.loading && !it.loadMore

            if (isLoadingMore.get() != it.loadMore) {
                isLoadingMore.set(it.loading)
                listAdapter.loadMore(it.loadMore)
            }
        })

        listViewModel.listData.observe(this, Observer<ListData<Item>> {
            it ?: throw IllegalStateException()

            listAdapter.buildModels(it.data, !it.loadMore)
        })
    }

    private fun refresh() {
        query(true)
    }

    fun query(queryText: String?, options: Array<String>? = null) {
        this.queryText = queryText
        this.options = options
        refresh()
    }

    private fun query(update: Boolean) {
        listViewModel.query(options, queryText, update)

    }

    override fun onResume() {
        super.onResume()
        tryInitRefresh()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        tryInitRefresh()
    }

    private fun tryInitRefresh() {
        if (!withSearch && swipeRefreshLayout != null && userVisibleHint && listAdapter.isEmpty) {
            refresh()
        }
    }

    inner class ListAdapter : EpoxyAdapter() {
        private val loadMoreViewModel = LoadMoreViewModel_()

        fun buildModels(data: Array<Item>, refresh: Boolean) {
            if (refresh) {
                removeAllModels()
            }
            val list = data.map {
                ItemViewModel_(it)
                        .onClickListener { _ ->
                            val intent = DetailActivity.createIntent(activity!!, it)
                            startActivity(intent)
                        }
            }
            addModels(list)
        }

        fun loadMore(loadMore: Boolean) {
            if (loadMore) {
                recyclerView.post {
                    addModel(loadMoreViewModel)
                    recyclerView.invalidateItemDecorations()
                    recyclerView.smoothScrollToPosition(listAdapter.itemCount - 1)
                }
            } else {
                removeModel(loadMoreViewModel)
            }


        }

    }
}
