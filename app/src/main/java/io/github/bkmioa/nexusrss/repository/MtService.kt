package io.github.bkmioa.nexusrss.repository

import io.github.bkmioa.nexusrss.model.Comment
import io.github.bkmioa.nexusrss.model.CommentRequestBody
import io.github.bkmioa.nexusrss.model.FileItem
import io.github.bkmioa.nexusrss.model.Item
import io.github.bkmioa.nexusrss.model.ItemList
import io.github.bkmioa.nexusrss.model.MemberInfo
import io.github.bkmioa.nexusrss.model.MemberRequestBody
import io.github.bkmioa.nexusrss.model.RequestData
import io.github.bkmioa.nexusrss.model.Result
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface MtService {

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
