package com.example.makip.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow

@Dao
interface FotoPedidoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fotoPedido: FotoPedido): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(fotos: List<FotoPedido>)

    @Delete
    suspend fun delete(fotoPedido: FotoPedido)

    @Query("SELECT * FROM foto_pedido WHERE pedidoId = :pedidoId")
    fun getFotosByPedido(pedidoId: Long): Flow<List<FotoPedido>>
}
