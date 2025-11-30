package com.example.makip.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "producto",
    foreignKeys = [
        ForeignKey(
            entity = Categoria::class,
            parentColumns = ["id"],
            childColumns = ["categoriaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["categoriaId"])]
)
data class Producto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val categoriaId: Long,
    val precioDesde: Double,
    val descripcion: String? = null,
    val imagenUrl: String,
    val activo: Boolean = true
)
