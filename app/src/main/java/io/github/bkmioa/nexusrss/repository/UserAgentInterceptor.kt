package io.github.bkmioa.nexusrss.repository

import android.app.Application
import android.webkit.WebView
import okhttp3.Interceptor
import okhttp3.Response


class UserAgentInterceptor(app: Application) : Interceptor {
    private var USER_AGENT = WebView(app).settings.userAgentString

    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder().header("User-Agent", USER_AGENT).build()
        return chain.proceed(newRequest)
    }
}