package io.github.bkmioa.nexusrss.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import io.github.bkmioa.nexusrss.Settings
import io.github.bkmioa.nexusrss.base.BaseViewModel
import io.github.bkmioa.nexusrss.login.VerifyManager
import io.github.bkmioa.nexusrss.model.Item
import io.github.bkmioa.nexusrss.model.ListData
import io.github.bkmioa.nexusrss.model.LoadingState
import io.github.bkmioa.nexusrss.repository.Deconstructor
import io.github.bkmioa.nexusrss.repository.Service
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject
import java.util.concurrent.atomic.AtomicBoolean

class RssListViewModel(app: Application) : BaseViewModel(app) {
    private val service: Service by inject()

    private var page: Int = 0
    private val isLoading = AtomicBoolean(false)
    private val noMore = AtomicBoolean(false)

    val loadingState = MutableLiveData<LoadingState>()
    val listData = MutableLiveData<ListData<Item>>()

    fun query(path: String, options: Array<String>? = null, queryText: String? = null, update: Boolean = true) {
        if (isLoading.get()) return

        if (noMore.get() && !update) return

        isLoading.set(true)

        if (update) {
            page = 0
            noMore.set(false)
        } else {
            page++
        }

        val pageSize = Settings.PAGE_SIZE
        val startIndex = page * pageSize

        val queryMap = HashMap<String, String>()
        options?.forEach { queryMap[it] = "1" }

        service.queryList(path, queryMap, queryText, page)
            .subscribeOn(Schedulers.io())
            .compose(Deconstructor.apply())
            .compose(VerifyManager.verify())
            .map { if (page == 0) it.sorted().reversed() else it }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : Observer<List<Item>> {
                override fun onSubscribe(@NonNull d: Disposable) {
                    if (update) {
                        loadingState.value = LoadingState()
                    } else {
                        loadingState.value = LoadingState(loadMore = true)
                    }
                }

                override fun onNext(@NonNull list: List<Item>) {
                    if (!list.isEmpty() || update) {
                        listData.value = ListData(list.toTypedArray(), !update)
                    }
                    noMore.set(list.size < pageSize)
                }

                override fun onError(@NonNull e: Throwable) {
                    e.printStackTrace()
                    isLoading.set(false)
                    if (!update) {
                        page--
                    } else {
                        noMore.set(true)
                    }
                    loadingState.value = LoadingState(false, false, e)
                }

                override fun onComplete() {
                    isLoading.set(false)
                    loadingState.value = LoadingState(false, false)
                }

            })
    }
}
