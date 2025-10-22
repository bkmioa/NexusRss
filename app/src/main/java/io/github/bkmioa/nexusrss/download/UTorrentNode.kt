package io.github.bkmioa.nexusrss.download

import io.reactivex.Single
import kotlinx.parcelize.Parcelize
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.java.net.cookiejar.JavaNetCookieJar
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.net.CookieManager
import java.net.URLEncoder

@Parcelize
class UTorrentNode(
    override val host: String,
    override val userName: String,
    override val password: String,
    override val defaultPath: String?
) : DownloadNode {

    override fun download(torrentUrl: String, path: String?): Single<String> {
        val service: UTorrentService = getService()
        return service.token()
            .flatMap { service.addUrl(getTokenFromBody(it.string()), URLEncoder.encode(torrentUrl)) }
            .map { "add success" }
    }

    private fun getTokenFromBody(html: String): String {
        return Regex("<div id='token' style='display:none;'>([^<>]+)</div>")
            .find(html)?.groupValues?.getOrNull(1) ?: throw IllegalStateException()
    }

    private fun getService(): UTorrentService {
        val httpclient = OkHttpClient.Builder()
            //.addInterceptor(httpLoggingInterceptor)
            .cookieJar(JavaNetCookieJar(CookieManager()))
            .authenticator { _, response ->
                response.request.newBuilder()
                    .header("Authorization", Credentials.basic(userName, password))
                    .build()
            }
            .build()
        val baseUrl = if (host.endsWith("\\")) host else host + "\\"
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpclient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UTorrentService::class.java)
    }

    interface UTorrentService {
        @GET("/gui/?action=add-url")
        fun addUrl(
            @Query("token") token: String,
            @Query(value = "s") torrentUrl: String
        ): Single<Any>

        @GET("/gui/token.html")
        fun token(): Single<ResponseBody>
    }
}