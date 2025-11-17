package com.example.melora.data.remote

object LoginRemoteModule {
    private const val LOGIN_URL = "https://l6k80b0k-8082.brs.devtunnels.ms/"


    private val logging = okhttp3.logging.HttpLoggingInterceptor().apply {
        level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
    }

    private val okHttp = okhttp3.OkHttpClient.Builder()
        .addInterceptor(logging).build()

    private val retrofit = retrofit2.Retrofit.Builder()
        .baseUrl(LOGIN_URL).client(okHttp)
        .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
        .build()

    fun api(): LoginApi = retrofit.create(LoginApi::class.java)
}