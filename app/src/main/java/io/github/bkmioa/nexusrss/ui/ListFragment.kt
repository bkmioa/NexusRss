package io.github.bkmioa.nexusrss.ui

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
import io.github.bkmioa.nexusrss.model.Item
import io.github.bkmioa.nexusrss.repository.Service
import io.github.bkmioa.nexusrss.ui.viewModel.ItemViewModel_
import io.github.bkmioa.nexusrss.ui.viewModel.LoadMoreViewModel_
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_list.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject


class ListFragment : BaseFragment(), Scrollable, Injectable {

    @Inject lateinit internal var service: Service

    private val data: MutableList<Item> = ArrayList()
    private var page: Int = 0
    private val listController = ListController()

    private var options: Array<String>? = null
    private var queryText: String? = null
    private var withSearch = false

    private val isLoading = AtomicBoolean(false)
    private val isLoadingMore = AtomicBoolean(false)

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
                    outRect.bottom = 60
                }
            }
        })

        swipeRefreshLayout.setOnRefreshListener {
            refresh()
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!isLoading.get() && !recyclerView.canScrollVertically(1)) {
                    query(false)
                }
            }
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
        if (isLoading.get()) return

        isLoading.set(true)

        if (update) {
            page = 0
            swipeRefreshLayout.isRefreshing = true
        } else {
            page++
            isLoadingMore.set(true)
            listController.requestModelBuild()
        }

        val startIndex = page * Settings.PAGE_SIZE

        val queryMap = HashMap<String, String>()
        options?.forEach { queryMap[it] = "1" }

        service.queryList(queryMap, startIndex, Settings.PAGE_SIZE, queryText)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { it.items.filter { it.enclosure != null } }
                .subscribeWith(object : Observer<List<Item>> {
                    override fun onSubscribe(@NonNull d: Disposable) {

                    }

                    override fun onNext(@NonNull list: List<Item>) {
                        if (update) {
                            data.clear()
                        }
                        data.addAll(list)
                    }

                    override fun onError(@NonNull e: Throwable) {
                        e.printStackTrace()
                        isLoading.set(false)
                        if (!update) {
                            page--
                            isLoadingMore.set(false)
                        } else {
                            swipeRefreshLayout.isRefreshing = false
                        }
                        listController.requestModelBuild()
                    }

                    override fun onComplete() {
                        isLoading.set(false)
                        if (update) {
                            swipeRefreshLayout.isRefreshing = false
                        } else {
                            isLoadingMore.set(false)
                        }
                        listController.requestModelBuild()
                    }

                })
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
        if (!withSearch && swipeRefreshLayout != null && !isLoading.get()
                && userVisibleHint && data.size == 0) {
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
