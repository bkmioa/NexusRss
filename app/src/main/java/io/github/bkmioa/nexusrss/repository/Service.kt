package io.github.bkmioa.nexusrss.repository

import io.github.bkmioa.nexusrss.model.Rss
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface Service {
    @GET("torrentrss.php?https=1&ismalldescr=1")
    fun queryList(@QueryMap queryMap: Map<String, String>,
                  @Query("startindex") startIndex: Int,
                  @Query("rows") pageSize: Int): Observable<Rss>
}
