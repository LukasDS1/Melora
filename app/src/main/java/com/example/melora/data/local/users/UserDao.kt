package com.example.melora.data.local.users

import androidx.room.*
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.data.local.song.SongEntity

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

    @Query("SELECT * FROM users WHERE idUser = :id")
    suspend fun getById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(nickname) LIKE '%' || LOWER(:query) || '%'")
    suspend fun getByName(query: String): List<UserEntity>



}