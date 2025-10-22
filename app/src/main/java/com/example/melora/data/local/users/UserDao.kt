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

    @Query("UPDATE users SET profilePicture = :profilePicture WHERE idUser = :id")
    suspend fun updateProfilePicture(id: Long, profilePicture:String)

    @Query("DELETE FROM users")
    suspend fun clearUser()

    @Query("SELECT * FROM users")
    suspend fun getAllUser(): List<UserEntity>


}