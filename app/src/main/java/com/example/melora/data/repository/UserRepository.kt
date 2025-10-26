package com.example.melora.data.repository

import com.example.melora.data.local.users.UserDao
import com.example.melora.data.local.users.UserEntity

class UserRepository(
    private val userDao : UserDao
) {


    suspend fun searchByNickname(query: String): Result<List<UserEntity>> {
        return try {
            val users = userDao.getByName(query)
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = userDao.getByEmail(email)
        return if (user != null && user.pass == password) {
            Result.success(user)
        } else {
            Result.failure(IllegalArgumentException("Credenciales inválidas"))
        }
    }

    suspend fun getUserById(id: Long?): UserEntity? {
        return userDao.getById(id)
    }

    suspend fun register(nickname: String, email: String, password: String): Result<Long> {

        val exists = userDao.getByEmail(email) != null

        val existNickname = userDao.getBynickname(nickname) != null

        if (exists) {
            return Result.failure(IllegalStateException("El correo ya está en uso."))
        }

        if(existNickname){
            return  Result.failure(IllegalStateException("The nickname has already in use"))
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

    // Methods for EditProfileViewModel
    suspend fun getById(id: Long): UserEntity? {
        return userDao.getById(id)
    }

    suspend fun updateNickname(id: Long, newNickname: String) {
        userDao.updateUserName(id, newNickname)
    }

    suspend fun updateEmail(id: Long, email: String) {
        val exists = userDao.getByEmail(email)
        if (exists != null) throw IllegalStateException("Email ya en uso")
        userDao.updateUserEmail(id, email)
    }

    suspend fun updatePassword(id: Long, password: String) = userDao.updateUserPassword(id, password)

    suspend fun updateProfilePicture(id: Long, newProfilePicture: String?) {
        userDao.updateProfilePicture(id, newProfilePicture)
    }


}