package com.baselalhabib.gitgraph.data.remote.github

import com.baselalhabib.gitgraph.data.remote.github.dto.GitHubCommitDto
import com.baselalhabib.gitgraph.data.remote.github.dto.GitHubEventDto
import com.baselalhabib.gitgraph.data.remote.github.dto.GitHubRepoDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApiService {

    @GET("user/repos")
    suspend fun getAuthenticatedUserRepos(
        @Header("Authorization") authHeader: String,
        @Query("per_page") perPage: Int = 100,
        @Query("sort") sort: String = "updated"
    ): Response<List<GitHubRepoDto>>

    @GET("users/{username}/repos")
    suspend fun getUserRepos(
        @Path("username") username: String,
        @Query("per_page") perPage: Int = 100,
        @Query("sort") sort: String = "updated"
    ): Response<List<GitHubRepoDto>>

    @GET("users/{username}/events")
    suspend fun getUserEvents(
        @Path("username") username: String,
        @Header("Authorization") authHeader: String? = null,
        @Query("per_page") perPage: Int = 100
    ): Response<List<GitHubEventDto>>

    @GET("repos/{owner}/{repo}/commits")
    suspend fun getRepoCommits(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Header("Authorization") authHeader: String? = null,
        @Query("per_page") perPage: Int = 100
    ): Response<List<GitHubCommitDto>>
}
