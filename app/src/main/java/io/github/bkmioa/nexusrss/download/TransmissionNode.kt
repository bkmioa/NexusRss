package io.github.bkmioa.nexusrss.download

import androidx.annotation.Keep
import io.github.bkmioa.nexusrss.repository.JavaNetCookieJar
import io.reactivex.Single
import kotlinx.android.parcel.Parcelize
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import java.net.CookieManager

@Parcelize
class TransmissionNode(
    override val host: String,
    override val userName: String,
    override val password: String,
    override val defaultPath: String?
) : DownloadNode {

    override fun download(torrentUrl: String, path: String?): Single<String> {
        val service = getService()
        return service.getSessionId()
            .map { getSessionIdFromResponse(it) }
            .flatMap { service.addTorrent(it, AddTorrentArguments(torrentUrl, path ?: defaultPath)) }
            .map { if (it.isSuccess()) it.result else throw IllegalStateException(it.result) }
    }

    private fun getSessionIdFromResponse(result: Response<Any>): String {
        return result.headers().get("X-Transmission-Session-Id")
            ?: throw IllegalStateException("X-Transmission-Session-Id empty")
    }

    private fun getService(): TransmissionService {
        val httpclient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
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
            .create(TransmissionService::class.java)
    }

    interface TransmissionService {
        @GET(".")
        fun getSessionId(): Single<Response<Any>>

        @POST(".")
        fun addTorrent(@Header("X-Transmission-Session-Id") sessionId: String, @Body body: AddTorrentArguments): Single<AddTorrentResult>
    }

    @Keep
    class AddTorrentArguments(torrentUrl: String, downloadDir: String?) {
        val method = "torrent-add"

        val arguments = hashMapOf(
            "filename" to torrentUrl,
        )

        init {
            if (!downloadDir.isNullOrBlank()) {
                arguments["download-dir"] = downloadDir
            }
        }
    }

    class AddTorrentResult {
        var result: String? = null


        fun isSuccess(): Boolean {
            return result == "success"
        }
    }
}