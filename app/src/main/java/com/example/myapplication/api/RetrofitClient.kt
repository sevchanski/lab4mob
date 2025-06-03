package com.example.myapplication.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit

object RetrofitClient {
    private const val BASE_URL = "https://findwork.dev/";

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain -> val request = chain.request().newBuilder()
            .addHeader("Authorization", "ff5b3bc2cc8e200059de1b969ada66198633dac2")  // <- встав свій токен сюди
            .build()
            chain.proceed(request)
        }
        .build()
    val api: FindWorkApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(FindWorkApi::class.java)
}

