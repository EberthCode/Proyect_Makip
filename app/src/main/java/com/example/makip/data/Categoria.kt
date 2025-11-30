package com.example.makip.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categoria")
data class Categoria(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val imagenUrl: String,
    val orden: Int
)
