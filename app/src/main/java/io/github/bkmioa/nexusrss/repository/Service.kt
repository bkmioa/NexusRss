package io.github.bkmioa.nexusrss.repository

import io.github.bkmioa.nexusrss.model.Rss
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*

interface Service {
    @GET("torrentrss.php?https=1&ismalldescr=1")
    fun queryList(@QueryMap queryMap: Map<String, String>,
                  @Query("startindex") startIndex: Int,
                  @Query("rows") pageSize: Int)
            : Observable<Rss>


    @POST
    @FormUrlEncoded
    fun remoteDownload(@Url remoteUrl: String, @Field("url") torrentUrl: String)
            : Observable<ResponseBody>
}
