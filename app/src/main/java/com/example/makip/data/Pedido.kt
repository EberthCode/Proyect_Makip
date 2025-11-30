package com.example.makip.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "pedido",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Producto::class,
            parentColumns = ["id"],
            childColumns = ["productoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["usuarioId"]),
        Index(value = ["celularCliente"]),
        Index(value = ["estado"]),
        Index(value = ["productoId"])
    ]
)
data class Pedido(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Long?,
    val celularCliente: String,
    val productoId: Long,
    val textoPersonalizado: String? = null,
    val colorOTalla: String,
    val cantidad: Int,
    val direccionEntrega: String,
    val distrito: String,
    val metodoPago: String,
    val estado: String, // "Pendiente", "Pago confirmado", etc.
    val precioFinal: Double? = null,
    @ColumnInfo(name = "creado_en") val creadoEn: Long = System.currentTimeMillis(),
    val sincronizado: Boolean = false
)
