package io.github.bkmioa.nexusrss.repository

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface UTorrentService {
    @GET("/gui/?action=add-url")
    fun addUrl(@Query("token") token: String,
               @Query(value = "s") torrentUrl: String)
            : Observable<ResponseBody>

    @GET("/gui/token.html")
    fun token(): Observable<ResponseBody>
}
