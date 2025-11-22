package com.example.melora.data.remote

object UploadRemoteModule {

    private const val UPLOAD_URL = "https://3rx2vqh0-8084.brs.devtunnels.ms/"

    private val logging = okhttp3.logging.HttpLoggingInterceptor().apply {
        level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
    }

    private val okHttp = okhttp3.OkHttpClient.Builder()
        .addInterceptor(logging).build()

    private val retrofit = retrofit2.Retrofit.Builder()
        .baseUrl(UPLOAD_URL).client(okHttp)
        .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
        .build()

    fun api(): UploadApi = retrofit.create(UploadApi::class.java)
}