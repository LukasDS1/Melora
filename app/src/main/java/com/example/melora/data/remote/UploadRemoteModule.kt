package com.example.melora.data.remote

import okhttp3.logging.HttpLoggingInterceptor

object UploadRemoteModule {

    private const val UPLOAD_URL = "https://l6k80b0k-8084.brs.devtunnels.ms/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.HEADERS
    }

    private val okHttp = okhttp3.OkHttpClient.Builder()
        .addInterceptor(logging).build()

    private val retrofit = retrofit2.Retrofit.Builder()
        .baseUrl(UPLOAD_URL).client(okHttp)
        .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
        .build()

    fun api(): UploadApi = retrofit.create(UploadApi::class.java)
}