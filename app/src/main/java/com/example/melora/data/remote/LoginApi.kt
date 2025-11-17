package com.example.melora.data.remote

import com.example.melora.data.remote.dto.LoginResponse
import com.example.melora.data.remote.dto.LoginUserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {

    @POST("api-v1/auth/login")
    suspend fun login(
        @Body dto: LoginUserDto
    ): Response<LoginResponse>
}