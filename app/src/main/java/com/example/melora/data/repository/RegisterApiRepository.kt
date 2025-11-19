package com.example.melora.data.repository

import android.net.http.HttpException
import com.example.melora.data.remote.RegisterApi
import com.example.melora.data.remote.RegisterRemoteModule
import com.example.melora.data.remote.dto.ArtistProfileData
import com.example.melora.data.remote.dto.RegisterUserDto
import java.io.IOException

class RegisterApiRepository(
    private val api: RegisterApi = RegisterRemoteModule.api()
) {
    suspend fun register(dto: RegisterUserDto): Result<String> {
        return try {
            val response = api.registerUser(dto)

            if (response.isSuccessful) {
                Result.success("Usuario registrado correctamente")
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception(errorBody ?: "Error desconocido"))
            }

        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: HttpException) {
            Result.failure(Exception("Error HTTP: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUser(idUser: Long, dto: RegisterUserDto): Result<String> {
        return try {
            val response = api.updateUser(idUser, dto)

            if (response.isSuccessful) {
                Result.success("Usuario actualizado exitosamente")
            } else {
                Result.failure(Exception(response.errorBody()?.string()))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUser(idUser: Long): Result<String> {
        return try {
            val response = api.deleteUser(idUser)

            if (response.isSuccessful) {
                Result.success("Usuario eliminado exitosamente")
            } else {
                Result.failure(Exception("No se pudo eliminar el usuario"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getById(idUser: Long): Result<Any> {
        return try {
            val response = api.getUserById(idUser)

            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("No se encontró el usuario"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchByNickname(query: String): Result<List<ArtistProfileData>> = try {
        Result.success(api.searchUsers(query))
    } catch (e: Exception) {
        Result.failure(e)
    }

}