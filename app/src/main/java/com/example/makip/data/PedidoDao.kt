package com.example.makip.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import androidx.room.Transaction
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pedido: Pedido): Long

    @Update
    suspend fun update(pedido: Pedido)

    @Delete
    suspend fun delete(pedido: Pedido)

    @Query("SELECT * FROM pedido WHERE id = :id")
    suspend fun getPedidoById(id: Long): Pedido?

    @Query("SELECT * FROM pedido WHERE usuarioId = :usuarioId ORDER BY creado_en DESC")
    fun getPedidosByUsuario(usuarioId: Long): Flow<List<Pedido>>

    @Query("SELECT * FROM pedido ORDER BY creado_en DESC")
    fun getAllPedidos(): Flow<List<Pedido>>

    @Query("SELECT * FROM pedido WHERE sincronizado = 0")
    suspend fun getPedidosNoSincronizados(): List<Pedido>
}
