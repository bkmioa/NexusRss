package io.github.bkmioa.nexusrss.download

import io.reactivex.Single
import kotlinx.parcelize.Parcelize
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okhttp3.java.net.cookiejar.JavaNetCookieJar
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.*
import java.net.CookieManager


@Parcelize
class QBittorrentNode(
    override val host: String,
    override val userName: String,
    override val password: String,
    override val defaultPath: String?
) : DownloadNode {

    override fun download(torrentUrl: String, path: String?): Single<String> {
        val service: UTorrentService = getService()

        return service.login(userName, password)
            .flatMap {
                service.addTorrent(
                    torrentUrl.toRequestBody(MultipartBody.FORM),
                    (path ?: defaultPath)?.toRequestBody(MultipartBody.FORM),
                    false.toString().toRequestBody(MultipartBody.FORM),
                )
            }
            .map { "add success" }
    }

    private fun getService(): UTorrentService {
        val httpclient = OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(CookieManager()))
            .build()
        val baseUrl = if (host.endsWith("\\")) host else host + "\\"
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpclient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(UTorrentService::class.java)
    }

    interface UTorrentService {
        @FormUrlEncoded
        @POST("/api/v2/auth/login")
        fun login(
            @Field("username") userName: String,
            @Field("password") password: String
        ): Single<ResponseBody>

        @Multipart
        @POST("/api/v2/torrents/add")
        fun addTorrent(
            @Part("urls") urls: RequestBody,
            @Part("savepath") savePath: RequestBody?,
            @Part("autoTMM") autoTMM: RequestBody
        ): Single<ResponseBody>
    }
}