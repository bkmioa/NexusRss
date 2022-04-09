package io.github.bkmioa.nexusrss.cookie

import android.webkit.CookieManager
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.net.CookieHandler
import java.net.URI

class SharedCookieJar : CookieHandler(), CookieJar {
    private val cookieManager: CookieManager = CookieManager.getInstance()

    override fun get(uri: URI, requestHeaders: Map<String?, List<String?>?>?): Map<String, List<String>> {
        val url: String = uri.toString()
        val cookieValue: String = cookieManager.getCookie(url) ?: ""
        return mapOf("Cookie" to listOf(cookieValue))
    }

    override fun put(uri: URI, responseHeaders: Map<String, List<String?>>) {
        val url: String = uri.toString()
        for (header in responseHeaders.keys) {
            if (header.equals("Set-Cookie", ignoreCase = true) || header.equals("Set-Cookie2", ignoreCase = true)) {
                responseHeaders[header]?.forEach { value ->
                    cookieManager.setCookie(url, value)
                }
            }
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return when (val cookies = cookieManager.getCookie(url.toString())) {
            null -> emptyList()
            else -> cookies.split("; ").mapNotNull { Cookie.parse(url, it) }
        }
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookies.forEach { cookie ->
            cookieManager.setCookie(url.toString(), cookie.toString())
        }
    }
}