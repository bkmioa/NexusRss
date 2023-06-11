package io.github.bkmioa.nexusrss.repository

import io.github.bkmioa.nexusrss.model.Release
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers

interface GithubService {
    @GET("/repos/bkmioa/NexusRss/releases?per_page=1")
    @Headers("Accept: application/vnd.github.v3+json")
    fun releaseList(): Single<Array<Release>>
}