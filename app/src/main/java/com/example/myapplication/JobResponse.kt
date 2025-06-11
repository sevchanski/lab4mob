package com.example.myapplication

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.gson.annotations.SerializedName

@Parcelize
data class Job(
    val id: String,
    @SerializedName("role") val title: String, // 'role' з API відповідає 'title' у вашому UI
    @SerializedName("company_name") val companyName: String,
    @SerializedName("employment_type") val employmentType: String?, // Це поле може бути null
    val location: String,
    val remote: Boolean,
    val url: String,
    @SerializedName("text") val description: String?, // <-- Опис з API називається 'text' і може бути NULL
    @SerializedName("logo") val logoUrl: String? = null // Також може бути NULL
    // Додайте інші поля, якщо вони є у відповіді API і ви їх використовуєте
) : Parcelable

data class JobsResponse(
    val results: List<Job>
)
