package com.example.myapplication

import com.google.gson.annotations.SerializedName

data class Job(
    val id: String,
    val title: String,
    @SerializedName("company_name") val companyName: String,
    val location: String,
    val description: String,
    val url: String,
    val remote: Boolean
)

data class JobsResponse(
    val results: List<Job>
)
