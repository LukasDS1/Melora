package com.example.melora.data.local.rol

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RolDao {


    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(rol: RolEntity): Long

    @Query("SELECT * FROM rol WHERE idRol = :idRol")
    suspend fun getRolById(idRol: Long): RolEntity

    @Query("SELECT * FROM rol")
    suspend fun getAllRol(): List<RolEntity>

    @Query("SELECT COUNT(*) FROM rol")
    suspend fun getAllRolesCount(): Int
}