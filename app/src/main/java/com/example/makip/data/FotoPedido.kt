package com.example.makip.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "foto_pedido",
    foreignKeys = [
        ForeignKey(
            entity = Pedido::class,
            parentColumns = ["id"],
            childColumns = ["pedidoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["pedidoId"])]
)
data class FotoPedido(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pedidoId: Long,
    val urlFoto: String
)
