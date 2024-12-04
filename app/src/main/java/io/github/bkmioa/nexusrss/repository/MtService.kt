package io.github.bkmioa.nexusrss.repository

import io.github.bkmioa.nexusrss.model.*
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface MtService {
    //@GET("/{path}")
    //fun queryList(
    //    @Path("path") path: String,
    //    @QueryMap queryMap: Map<String, String>,
    //    @Query("keyword", encoded = true) queryText: String? = null,
    //    @Query("pageNumber") page: Int?
    //): Observable<Response<ThreadList>>

    @POST
    @FormUrlEncoded
    fun remoteDownload(@Url remoteUrl: String, @Field("url") torrentUrl: String)
            : Observable<ResponseBody>

    @POST("api/torrent/search")
    fun queryList(@Body requestData: RequestData): Observable<Response<Result<ItemList<Item>>>>

    @POST("api/torrent/search")
    suspend fun search(@Body requestData: RequestData): Result<ItemList<Item>>

    @POST("api/torrent/detail")
    @FormUrlEncoded
    suspend fun getDetail(@Field("id") id: String): Result<Item>

    @POST("api/torrent/genDlToken")
    @FormUrlEncoded
    suspend fun getDownloadLink(@Field("id") id: String): Result<String>

    @POST("api/torrent/files")
    @FormUrlEncoded
    suspend fun getFileList(@Field("id") id: String): Result<List<FileItem>>

    @POST("api/comment/fetchList")
    suspend fun getComments(@Body requestData: CommentRequestBody): Result<ItemList<Comment>>

    @POST("api/member/bases")
    suspend fun getMemberInfos(@Body body: MemberRequestBody): Result<HashMap<String, MemberInfo>>
}
