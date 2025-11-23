package com.example.melora.data.remote

import com.example.melora.data.remote.dto.RecoverPassDto
import com.example.melora.data.remote.dto.RecoverPassResponse
import com.example.melora.data.remote.dto.ResetPasswordDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RecoverPassApi {

    @POST("api-v1/reset-password/request")
    suspend fun recoverPassword(
        @Body dto: RecoverPassDto
    ): Response<RecoverPassResponse>

    @PUT("api-v1/reset-password/reset/{token}")
    suspend fun resetPassword(
        @Path("token") token: String,
        @Body dto: ResetPasswordDto
    ): Response<RecoverPassResponse>

    @GET("api-v1/reset-password/validateToken/{token}")
    suspend fun validateToken(
        @Path("token") token: String
    ): Response<ResponseBody>

}