package com.example.melora.data.local.estado

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EstadoDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertEstado(estado : EstadoEntity): Long

    @Query("SELECT COUNT(*) FROM estado")
    suspend fun countEstado():Int

    @Query("SELECT * FROM estado")
    suspend fun getAllEstado():List<EstadoEntity>

}