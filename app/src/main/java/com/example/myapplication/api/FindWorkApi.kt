package com.example.myapplication.api

import com.example.myapplication.JobsResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response


interface FindWorkApi {
    @GET("api/jobs/")
    suspend fun getJobs(
        @Query("search") position: String,
        @Query("location") location: String? = null,
        @Query("remote") remote: Boolean? = null,
        @Query("page") page: Int? = 1
    ): Response<JobsResponse> // <--- ось це правильно
}