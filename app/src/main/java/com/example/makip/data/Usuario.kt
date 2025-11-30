package com.example.makip.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import androidx.room.ColumnInfo

@Entity(tableName = "usuario", indices = [Index(value = ["email"], unique = true)])
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val email: String,
    val password: String,
    val celular: String,
    @ColumnInfo(name = "creado_en") val creadoEn: Long = System.currentTimeMillis()
)
