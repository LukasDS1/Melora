package com.example.melora.data.local.categoria

import androidx.room.*

@Dao
interface CategoriaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoria(cat: CategoriaEntity): Long

    @Query("SELECT * FROM categoria")
    suspend fun getAllCategorias(): List<CategoriaEntity>

    @Query("SELECT * FROM categoria WHERE idCat = :id")
    suspend fun getCategoriaById(id: Long): CategoriaEntity?

    @Delete
    suspend fun deleteCategoria(cat: CategoriaEntity)
}
