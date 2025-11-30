package com.example.makip.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "foto_referencia",
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
data class FotoReferencia(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pedidoId: Long,
    val urlFoto: String, // Puede ser ruta local (file://...) o remota (https://...)
    val sincronizada: Boolean = false // True si ya está en ImageKit
)
