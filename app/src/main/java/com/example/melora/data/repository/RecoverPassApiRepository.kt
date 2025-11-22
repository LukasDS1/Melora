package com.example.melora.data.repository

import com.example.melora.data.remote.RecoverPassRemoteModule
import com.example.melora.data.remote.dto.RecoverPassDto
import com.example.melora.data.remote.dto.RecoverPassResponse
import com.example.melora.data.remote.dto.ResetPasswordDto
import retrofit2.Response

class RecoverPassApiRepository {

    private val api = RecoverPassRemoteModule.api()

    suspend fun recoverPassword(email: String): Response<RecoverPassResponse> {
        return api.recoverPassword(RecoverPassDto(email))
    }

    suspend fun resetPassword(token: String, newPassword: String): Response<RecoverPassResponse> {
        return api.resetPassword(token, ResetPasswordDto(newPassword))
    }
}