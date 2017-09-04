package io.github.bkmioa.nexusrss.viewmodel

import android.arch.lifecycle.MutableLiveData
import io.github.bkmioa.nexusrss.App
import io.github.bkmioa.nexusrss.Settings
import io.github.bkmioa.nexusrss.base.BaseViewModel
import io.github.bkmioa.nexusrss.model.Item
import io.github.bkmioa.nexusrss.model.ListData
import io.github.bkmioa.nexusrss.model.LoadingState
import io.github.bkmioa.nexusrss.repository.Service
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class RssListViewModel @Inject constructor(app: App) : BaseViewModel(app) {
    @Inject lateinit internal var service: Service

    private var page: Int = 0
    private val isLoading = AtomicBoolean(false)

    val loadingState = MutableLiveData<LoadingState>()
    val listData = MutableLiveData<ListData<Item>>()

    fun query(options: Array<String>? = null, queryText: String? = null, update: Boolean = true) {
        if (isLoading.get()) return

        isLoading.set(true)

        if (update) {
            page = 0
            loadingState.value = LoadingState()
        } else {
            page++
            loadingState.value = LoadingState(loadMore = true)
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
                        listData.value = ListData(list.toTypedArray(), !update)
                    }

                    override fun onError(@NonNull e: Throwable) {
                        e.printStackTrace()
                        isLoading.set(false)
                        if (!update) {
                            page--
                        }
                        loadingState.value = LoadingState(false, false)
                    }

                    override fun onComplete() {
                        isLoading.set(false)
                        loadingState.value = LoadingState(false, false)
                    }

                })
    }
}