package com.example.melora.data.repository

import com.example.melora.data.remote.LoginApi
import com.example.melora.data.remote.LoginRemoteModule
import com.example.melora.data.remote.dto.LoginResponse
import com.example.melora.data.remote.dto.LoginUserDto

class LoginApiRepository (private val api: LoginApi = LoginRemoteModule.api()){
    suspend fun loginUser(dto: LoginUserDto): Result<LoginResponse> {
        return try {
            val response = api.login(dto)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Credenciales incorrectas"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}