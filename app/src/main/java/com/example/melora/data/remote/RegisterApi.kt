package com.example.melora.data.remote

import com.example.melora.data.remote.dto.RegisterUserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface RegisterApi {


    //Registrar usuario
    @POST("api-v1/register/add")
    suspend fun registerUser(
        @Body dto: RegisterUserDto
    ): Response<Any>

    // Obtener usuario por ID
    @GET("api-v1/register/exists/{idUser}")
    suspend fun getUserById(
        @Path("idUser") id: Long
    ): Response<Any>

    // Actualizar usuario
    @PATCH("api-v1/register/update/{idUser}")
    suspend fun updateUser(
        @Path("idUser") id: Long,
        @Body dto: RegisterUserDto
    ): Response<Any>

    // Eliminar usuario
    @DELETE("api-v1/register/delete/{idUser}")
    suspend fun deleteUser(
        @Path("idUser") id: Long
    ): Response<Any>

}