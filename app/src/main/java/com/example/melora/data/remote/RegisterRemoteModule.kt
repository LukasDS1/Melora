package com.example.melora.data.remote

object RegisterRemoteModule {

    //TODO:CAMBIAR URL DEVTUNELS SIEMPRE LA CAMBIA YA ME PASO :(
    private const val REGISTER_URL = "https://l6k80b0k-8082.brs.devtunnels.ms/"

    private val logging = okhttp3.logging.HttpLoggingInterceptor().apply {
        level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
    }

    private val okHttp = okhttp3.OkHttpClient.Builder()
        .addInterceptor(logging).build()

    private val retrofit = retrofit2.Retrofit.Builder()
        .baseUrl(REGISTER_URL).client(okHttp)
        .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
        .build()

    fun api(): RegisterApi = retrofit.create(RegisterApi::class.java)
}