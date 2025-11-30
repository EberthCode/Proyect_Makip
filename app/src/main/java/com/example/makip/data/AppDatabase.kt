package com.example.makip.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Usuario::class, Categoria::class, Producto::class, Pedido::class, FotoPedido::class, FotoReferencia::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun productoDao(): ProductoDao
    abstract fun pedidoDao(): PedidoDao
    abstract fun fotoPedidoDao(): FotoPedidoDao
    abstract fun fotoReferenciaDao(): FotoReferenciaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "makip_database"
                )
                .fallbackToDestructiveMigration() // En desarrollo, limpiar DB si cambia versión
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
