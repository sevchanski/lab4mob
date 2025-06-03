package com.example.myapplication.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchData(
    val position: String,
    val country: String,
    val city: String,
    val isRemote: Boolean,
    val experience: Int,
    val employmentType: String
) : Parcelable