package io.github.bkmioa.nexusrss.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.epoxy.EpoxyAdapter
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.Settings
import io.github.bkmioa.nexusrss.base.BaseFragment
import io.github.bkmioa.nexusrss.common.Scrollable
import io.github.bkmioa.nexusrss.di.Injectable
import io.github.bkmioa.nexusrss.dp2px
import io.github.bkmioa.nexusrss.model.Item
import io.github.bkmioa.nexusrss.model.ListData
import io.github.bkmioa.nexusrss.model.LoadingState
import io.github.bkmioa.nexusrss.ui.viewModel.ItemViewModel_
import io.github.bkmioa.nexusrss.ui.viewModel.LoadMoreViewModel_
import io.github.bkmioa.nexusrss.viewmodel.RssListViewModel
import kotlinx.android.synthetic.main.fragment_list.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject


class ListFragment : BaseFragment(), Scrollable, Injectable {

    private val listAdapter = ListAdapter()

    private var options: Array<String>? = null
    private var queryText: String? = null
    private var withSearch = false

    private val isLoadingMore = AtomicBoolean(false)

    @Inject lateinit
    internal var viewModelFactory: ViewModelProvider.Factory

    private lateinit var listViewModel: RssListViewModel

    companion object {
        fun newInstance(options: Array<String>? = null, withSearch: Boolean = false): ListFragment {
            val fragment = ListFragment()
            val args = Bundle()
            options?.let { args.putStringArray("options", options) }
            args.putBoolean("withSearch", withSearch)
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
        options = arguments.getStringArray("options")
        withSearch = arguments.getBoolean("withSearch")

        listViewModel = ViewModelProviders.of(this, viewModelFactory).get(RssListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gridLayoutManager = GridLayoutManager(activity, 1)
        listAdapter.spanCount = gridLayoutManager.spanCount
        gridLayoutManager.spanSizeLookup = listAdapter.spanSizeLookup

        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = listAdapter
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
                val position = recyclerView.getChildAdapterPosition(view)
                if (position != recyclerView.adapter.itemCount - 1) {
                    outRect.bottom = activity.dp2px(10)
                }
//
//                val params = view.layoutParams as GridLayoutManager.LayoutParams
//
//                if (params.spanSize == 1) {
//                    outRect.bottom = activity.dp2px(8)
//
//                    if (params.spanIndex == 0) {
//                        outRect.left = activity.dp2px(8)
//                        outRect.right = activity.dp2px(4)
//                    }
//                    if (params.spanIndex == 1) {
//                        outRect.left = activity.dp2px(4)
//                        outRect.right = activity.dp2px(8)
//                    }
//                }
            }
        })

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
            swipeRefreshLayout.isRefreshing = it!!.loading && !it.loadMore

            if (isLoadingMore.get() != it.loadMore) {
                isLoadingMore.set(it.loading)
                listAdapter.loadMore(it.loadMore)
            }
        })

        listViewModel.listData.observe(this, Observer<ListData<Item>> {
            listAdapter.buildModels(it!!.data, !it.loadMore)
        })
    }

    private fun refresh() {
        query(true)
    }

    fun query(queryText: String?) {
        this.queryText = queryText
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
                        .onClickListener({ _ ->
                            val intent = DetailActivity.createIntent(activity, it)
                            startActivity(intent)
                        })
            }
            addModels(list)
        }

        fun loadMore(loadMore: Boolean) {
            if (loadMore) {
                recyclerView.post {
                    addModel(loadMoreViewModel)
                    recyclerView.invalidateItemDecorations()
                    recyclerView.smoothScrollToPosition(recyclerView.adapter.itemCount - 1)
                }
            } else {
                removeModel(loadMoreViewModel)
            }


        }

    }
}
