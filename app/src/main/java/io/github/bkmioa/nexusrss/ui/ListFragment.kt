package io.github.bkmioa.nexusrss.ui

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.epoxy.EpoxyController
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
import io.github.bkmioa.nexusrss.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_list.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject


class ListFragment : BaseFragment(), Scrollable, Injectable {


    private val data: MutableList<Item> = ArrayList()

    private val listController = ListController()

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

        recyclerView.layoutManager = LinearLayoutManager(activity)
        listController.setFilterDuplicates(true)
        recyclerView.adapter = listController.adapter
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View?, parent: RecyclerView, state: RecyclerView.State?) {
                val position = recyclerView.getChildAdapterPosition(view)
                if (position != recyclerView.adapter.itemCount - 1) {
                    outRect.bottom = activity.dp2px(10)
                }
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

        listViewModel.loadingState.observe(this, android.arch.lifecycle.Observer<LoadingState> {
            swipeRefreshLayout.isRefreshing = it!!.loading && !it.loadMore

            if (isLoadingMore.get() != it.loadMore) {
                isLoadingMore.set(it.loading)
                listController.requestModelBuild()
            }
        })

        listViewModel.listData.observe(this, android.arch.lifecycle.Observer<ListData<Item>> {
            if (!it!!.loadMore) {
                data.clear()
            }
            data.addAll(it.data)

            listController.requestModelBuild()
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
        if (!withSearch && swipeRefreshLayout != null && userVisibleHint && data.size == 0) {
            refresh()
        }
    }

    inner class ListController : EpoxyController() {

        override fun buildModels() {
            data.forEach {
                add(ItemViewModel_(it)
                        .onClickListener({ _ ->
                            val intent = DetailActivity.createIntent(activity, it)
                            startActivity(intent)
                        }))
            }
            LoadMoreViewModel_().addIf(isLoadingMore.get(), this)
            if (isLoadingMore.get()) {
                recyclerView.post {
                    recyclerView.smoothScrollToPosition(recyclerView.adapter.itemCount - 1)
                }
            }
        }

    }
}
