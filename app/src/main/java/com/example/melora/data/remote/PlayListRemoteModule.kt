package com.example.melora.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PlaylistRemoteModule {
    private const val PLAYLIST_URL = "https://l6k80b0k-8085.brs.devtunnels.ms/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.NONE
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(PLAYLIST_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun api(): PlayListApi = retrofit.create(PlayListApi::class.java)
}
