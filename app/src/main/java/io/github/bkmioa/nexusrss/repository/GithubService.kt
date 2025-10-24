package io.github.bkmioa.nexusrss.repository

import io.github.bkmioa.nexusrss.model.Release
import retrofit2.http.GET
import retrofit2.http.Headers

interface GithubService {
    @GET("/repos/bkmioa/NexusRss/releases?per_page=1")
    @Headers("Accept: application/vnd.github+json", "X-GitHub-Api-Version: 2022-11-28")
    suspend fun releaseList(): Array<Release>
}