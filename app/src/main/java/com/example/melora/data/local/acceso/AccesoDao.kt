package com.example.melora.data.local.acceso

import androidx.room.*

@Dao
interface AccesoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAcceso(acceso: AccesoEntity): Long

    @Query("SELECT * FROM acceso")
    suspend fun getAllAccesos(): List<AccesoEntity>

    @Query("SELECT * FROM acceso WHERE idAcceso = :id")
    suspend fun getAccesoById(id: Long): AccesoEntity?

    @Delete
    suspend fun deleteAcceso(acceso: AccesoEntity)
}
