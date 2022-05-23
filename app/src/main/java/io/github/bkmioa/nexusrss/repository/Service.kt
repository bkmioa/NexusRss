package io.github.bkmioa.nexusrss.repository

import io.github.bkmioa.nexusrss.model.ThreadList
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface Service {
    @GET("/{path}")
    fun queryList(
        @Path("path") path: String,
        @QueryMap queryMap: Map<String, String>,
        @Query("search", encoded = true) queryText: String? = null,
        @Query("page") page: Int
    ): Observable<Response<ThreadList>>

    @POST
    @FormUrlEncoded
    fun remoteDownload(@Url remoteUrl: String, @Field("url") torrentUrl: String)
            : Observable<ResponseBody>
}
