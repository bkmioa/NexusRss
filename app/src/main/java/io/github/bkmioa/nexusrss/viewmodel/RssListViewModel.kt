package io.github.bkmioa.nexusrss.viewmodel

import android.app.Application
import androidx.annotation.IntRange
import androidx.lifecycle.MutableLiveData
import io.github.bkmioa.nexusrss.Settings
import io.github.bkmioa.nexusrss.base.BaseViewModel
import io.github.bkmioa.nexusrss.login.VerifyManager
import io.github.bkmioa.nexusrss.model.Item
import io.github.bkmioa.nexusrss.model.ListData
import io.github.bkmioa.nexusrss.model.LoadingState
import io.github.bkmioa.nexusrss.model.RequestData
import io.github.bkmioa.nexusrss.repository.Deconstructor
import io.github.bkmioa.nexusrss.repository.MtService
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.koin.core.component.inject
import java.util.concurrent.atomic.AtomicBoolean

class RssListViewModel(app: Application) : BaseViewModel(app) {
    private val service: MtService by inject()

    @IntRange(from = 1)
    private var page: Int = 1
    private val isLoading = AtomicBoolean(false)
    private val noMore = AtomicBoolean(false)

    val loadingState = MutableLiveData<LoadingState>()
    val listData = MutableLiveData<ListData<Item>>()

    fun query(path: String, options: Array<String>? = null, queryText: String? = null, update: Boolean = true) {
        if (isLoading.get()) return

        if (noMore.get() && !update) return

        isLoading.set(true)

        if (update) {
            page = 1
            noMore.set(false)
        } else {
            page++
        }

        val pageSize = Settings.PAGE_SIZE

        val requestData = RequestData(
            mode = path,
            categories = resolveCategories(options),
            standards = resolveStandards(options),
            keyword = queryText,
            pageSize = pageSize,
            pageNumber = page,
        )

        service.queryList(requestData)
            .subscribeOn(Schedulers.io())
            .compose(Deconstructor.apply())
            .compose(VerifyManager.verify())
            .map { it.data }
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

    private fun resolveStandards(options: Array<String>?): List<String>? {
        return options?.filter { it.startsWith("standard_") }?.map { it.split("_")[1] }
    }

    private fun resolveCategories(options: Array<String>?) = (options ?: emptyArray()).filter { it.startsWith("cat_") }.map { it.split("_")[1] }
}
