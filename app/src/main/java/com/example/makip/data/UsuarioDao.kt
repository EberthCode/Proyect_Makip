package com.example.makip.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import androidx.room.OnConflictStrategy

@Dao
interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(usuario: Usuario): Long

    @Update
    suspend fun update(usuario: Usuario)

    @Delete
    suspend fun delete(usuario: Usuario)

    @Query("SELECT * FROM usuario WHERE id = :id")
    suspend fun getUsuarioById(id: Long): Usuario?

    @Query("SELECT * FROM usuario WHERE email = :email")
    suspend fun getUsuarioByEmail(email: String): Usuario?
}
