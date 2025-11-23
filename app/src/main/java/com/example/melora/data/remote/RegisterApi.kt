package com.example.melora.data.remote

import com.example.melora.data.remote.dto.ArtistProfileData
import com.example.melora.data.remote.dto.LoginResponse
import com.example.melora.data.remote.dto.RegisterUserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RegisterApi {


    //Registrar usuario
    @POST("api-v1/auth/add")
    suspend fun registerUser(
        @Body dto: RegisterUserDto
    ): Response<Any>

    // Actualizar usuario
    @PATCH("api-v1/auth/update/{idUser}")
    suspend fun updateUser(
        @Path("idUser") id: Long,
        @Body dto: RegisterUserDto
    ): Response<Any>

    // Eliminar usuario
    @DELETE("api-v1/auth/delete/{idUser}")
    suspend fun deleteUser(
        @Path("idUser") id: Long
    ): Response<Any>

    @GET("api-v1/auth/exists/{idUser}")
    suspend fun getRawUserById(@Path("idUser") idUser: Long): Map<String, Any>

    // Obtener usuario
    @GET("api-v1/auth/exists/{idUser}")
    suspend fun getUserById(
        @Path("idUser") idUser: Long
    ): Response<RegisterUserDto>

    @GET("api-v1/auth/search")
    suspend fun searchUsers(@Query("nickname") nickname: String): List<ArtistProfileData>





}