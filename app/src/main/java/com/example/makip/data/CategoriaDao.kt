package com.example.makip.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categoria: Categoria): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categorias: List<Categoria>)

    @Update
    suspend fun update(categoria: Categoria)

    @Delete
    suspend fun delete(categoria: Categoria)

    @Query("SELECT * FROM categoria ORDER BY orden ASC")
    fun getAllCategorias(): Flow<List<Categoria>>

    @Query("SELECT * FROM categoria WHERE id = :id")
    suspend fun getCategoriaById(id: Long): Categoria?
}
