package io.github.bkmioa.nexusrss.repository

import io.github.bkmioa.nexusrss.model.Item
import io.github.bkmioa.nexusrss.model.Rss
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface Service {
    @GET("https://kp.m-team.cc/movie.php")
    fun queryList(
        @QueryMap queryMap: Map<String, String>,
        @Query("startindex") startIndex: Int,
        @Query("rows") pageSize: Int,
        @Query("search", encoded = true) queryText: String? = null,
        @Query("passkey") passkey: String? = null
    ): Observable<Rss>

    @GET("/torrents.php")
    fun queryList(
        @QueryMap queryMap: Map<String, String>,
        @Query("search", encoded = true) queryText: String? = null,
        @Query("page") page: Int
    ): Observable<List<Item>>

    @POST
    @FormUrlEncoded
    fun remoteDownload(@Url remoteUrl: String, @Field("url") torrentUrl: String)
            : Observable<ResponseBody>
}
