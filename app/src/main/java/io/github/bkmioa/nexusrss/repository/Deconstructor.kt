package io.github.bkmioa.nexusrss.repository

import io.github.bkmioa.nexusrss.login.VerifyRequestException
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import retrofit2.Response


object Deconstructor {
    fun <T> apply(): ObservableTransformer<Response<T>, T> {
        return ObservableTransformer<Response<T>, T> { upstream ->
            upstream.flatMap { response ->
                if (response.isSuccessful) {
                    val t = response.body() as T
                    Observable.just(t)
                } else {
                    if (response.code() == 302 || response.code() == 503) {
                        Observable.error(VerifyRequestException())
                    } else {
                        Observable.error(Throwable())
                    }
                }
            }
        }
    }
}