package com.example.melora.data.repository

import com.example.melora.data.local.users.UserDao
import com.example.melora.data.local.users.UserEntity

class UserRepository(

    private val userDao : UserDao
) {

    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = userDao.getByEmail(email)
        return if (user != null && user.pass == password) {
            Result.success(user)
        } else {
            Result.failure(IllegalArgumentException("Credenciales inválidas"))
        }
    }

    suspend fun register(nickname: String, email: String, password: String): Result<Long> {

        val exists = userDao.getByEmail(email) != null
        if (exists) {
            return Result.failure(IllegalStateException("El correo ya está en uso."))
        }

        val id = userDao.upsertUser(
            UserEntity(
                nickname = nickname,
                email = email,
                pass = password
            )
        )
        return Result.success(id)
    }
}