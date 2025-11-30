package com.example.makip.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface FotoReferenciaDao {
    @Insert
    suspend fun insert(foto: FotoReferencia): Long

    @Insert
    suspend fun insertAll(fotos: List<FotoReferencia>)

    @Query("SELECT * FROM foto_referencia WHERE pedidoId = :pedidoId")
    suspend fun getFotosByPedido(pedidoId: Long): List<FotoReferencia>

    @Query("SELECT * FROM foto_referencia WHERE sincronizada = 0")
    suspend fun getFotosPendientes(): List<FotoReferencia>

    @Update
    suspend fun update(foto: FotoReferencia)
}
