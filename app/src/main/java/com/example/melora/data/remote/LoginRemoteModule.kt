package com.example.melora.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object LoginRemoteModule {
    private const val LOGIN_URL = "https://l6k80b0k-8082.brs.devtunnels.ms/"


    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.NONE
    }

    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(logging)
        .retryOnConnectionFailure(true)
        .build()


    private val retrofit = retrofit2.Retrofit.Builder()
        .baseUrl(LOGIN_URL).client(client)
        .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
        .build()

    fun api(): LoginApi = retrofit.create(LoginApi::class.java)
}