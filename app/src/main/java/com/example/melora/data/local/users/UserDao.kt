package com.example.melora.data.local.users

import androidx.room.*

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUser(user: UserEntity): Long // update or insert user

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getByEmail(email: String): UserEntity?

    @Query("UPDATE users SET nickname = :nickname WHERE idUser = :id")
    suspend fun updateUserName(id: Long, nickname: String)

    @Query("UPDATE users SET profilePicture = :bytes WHERE idUser = :id")
    suspend fun updateProfilePicture(id: Long, bytes: ByteArray)

    @Query("DELETE FROM users")
    suspend fun clearUser()
}