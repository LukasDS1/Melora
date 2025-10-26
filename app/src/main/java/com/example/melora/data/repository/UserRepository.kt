package com.example.melora.data.repository

import com.example.melora.data.local.rol.RolDao
import com.example.melora.data.local.rol.RolEntity
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.data.local.users.UserDao
import com.example.melora.data.local.users.UserEntity

class UserRepository(
    private val userDao : UserDao,
    private val rolDao: RolDao
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
        val user = userDao.getByEmail(email.trim().lowercase())
        return if (user != null && user.pass == password.trim()) {
            Result.success(user)
        } else {
            Result.failure(IllegalArgumentException("Credenciales inválidas"))
        }
    }

    suspend fun getUserById(id: Long): UserEntity? {
        return userDao.getById(id)
    }

    suspend fun register(nickname: String, email: String, password: String): Result<Long> {

        val exists = userDao.getByEmail(email) != null

        val existNickname = userDao.getBynickname(nickname) != null


        val roles = rolDao.getAllRolesCount()
        if (roles == 0) {
            rolDao.insert(RolEntity(idRol = 1, rolName = "Admin"))
            rolDao.insert(RolEntity(idRol = 2, rolName = "User"))
        }

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
                pass = password,
                rolId = 2L
            )
        )
        return Result.success(id)
    }
}