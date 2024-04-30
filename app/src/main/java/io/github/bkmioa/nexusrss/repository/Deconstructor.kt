package io.github.bkmioa.nexusrss.repository

import io.github.bkmioa.nexusrss.login.VerifyRequestException
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import retrofit2.Response
import io.github.bkmioa.nexusrss.model.Result

object Deconstructor {
    fun <T> apply(): ObservableTransformer<Response<Result<T>>, T> {
        return ObservableTransformer<Response<Result<T>>, T> { upstream ->
            upstream.flatMap { response ->
                if (response.isSuccessful) {
                    val t = response.body() as Result<T>
                    val data = t.data
                    if (data != null) {
                        Observable.just(data)
                    } else {
                        Observable.error(Throwable("${t.message}(${t.code})"))
                    }
                } else {
                    if (response.code() == 401 || response.code() == 302 || response.code() == 503) {
                        Observable.error(VerifyRequestException())
                    } else {
                        Observable.error(Throwable())
                    }
                }
            }
        }
    }
}