package com.example.makip.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(producto: Producto): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(productos: List<Producto>)

    @Update
    suspend fun update(producto: Producto)

    @Delete
    suspend fun delete(producto: Producto)

    @Query("SELECT * FROM producto WHERE activo = 1")
    fun getAllActiveProductos(): Flow<List<Producto>>

    @Query("SELECT * FROM producto WHERE categoriaId = :categoriaId AND activo = 1")
    fun getProductosByCategoria(categoriaId: Long): Flow<List<Producto>>

    @Query("SELECT * FROM producto WHERE id = :id")
    suspend fun getProductoById(id: Long): Producto?
}
