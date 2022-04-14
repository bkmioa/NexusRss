package io.github.bkmioa.nexusrss.login

import android.content.Intent
import io.github.bkmioa.nexusrss.App
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.subjects.PublishSubject


object VerifyManager {
    private const val TAG = "VerifyManager"

    private var subject: PublishSubject<Unit>? = null

    private lateinit var app: App

    fun init(app: App) {
        this.app = app
    }

    private fun subscribeVerify(): Observable<Unit> {
        return requireNotNull(subject).hide()
    }

    fun notifyVerifySuccess() {
        subject?.onNext(Unit)
        subject = null
    }

    fun notifyVerifyFailure() {
        subject?.onError(VerifyFailureException())
        subject = null
    }

    fun <T> verify(): ObservableTransformer<T, T> {
        if (subject == null) {
            subject = PublishSubject.create()
        }
        return ObservableTransformer<T, T> { upstream ->
            upstream.retryWhen { error ->
                error.flatMap {
                    if (it is VerifyRequestException) {
                        startVerify()
                        subscribeVerify()
                    } else {
                        Observable.error(it)
                    }
                }
            }
        }
    }

    private fun startVerify() {
        val intent = LoginActivity.createIntent(app).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        app.startActivity(intent)
    }
}