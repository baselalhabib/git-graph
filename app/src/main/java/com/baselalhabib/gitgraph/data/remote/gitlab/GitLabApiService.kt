package com.baselalhabib.gitgraph.data.remote.gitlab

import com.baselalhabib.gitgraph.data.remote.gitlab.dto.GitLabCommitDto
import com.baselalhabib.gitgraph.data.remote.gitlab.dto.GitLabEventDto
import com.baselalhabib.gitgraph.data.remote.gitlab.dto.GitLabProjectDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface GitLabApiService {

    @GET("projects")
    suspend fun getUserProjects(
        @Header("PRIVATE-TOKEN") privateToken: String? = null,
        @Query("membership") membership: Boolean = true,
        @Query("per_page") perPage: Int = 100,
        @Query("order_by") orderBy: String = "last_activity_at"
    ): Response<List<GitLabProjectDto>>

    @GET("users/{username}/events")
    suspend fun getUserEvents(
        @Path("username") username: String,
        @Header("PRIVATE-TOKEN") privateToken: String? = null,
        @Query("per_page") perPage: Int = 100
    ): Response<List<GitLabEventDto>>

    @GET("projects/{id}/repository/commits")
    suspend fun getProjectCommits(
        @Path("id") projectId: String,
        @Header("PRIVATE-TOKEN") privateToken: String? = null,
        @Query("per_page") perPage: Int = 100
    ): Response<List<GitLabCommitDto>>
}
