package com.example.makip.data

import android.content.Context
import com.example.makip.network.ImageKitUploader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PedidoRepository(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val pedidoDao = db.pedidoDao()
    private val fotoReferenciaDao = db.fotoReferenciaDao()

    // 1. Crear Pedido Offline
    suspend fun crearPedidoConReferencias(pedido: Pedido, rutasLocalesFotos: List<String>): Long {
        return withContext(Dispatchers.IO) {
            // Insertar Pedido
            val pedidoId = pedidoDao.insert(pedido)
            
            // Insertar Fotos como referencias locales (sincronizada = false)
            val fotos = rutasLocalesFotos.map { ruta ->
                FotoReferencia(
                    pedidoId = pedidoId,
                    urlFoto = ruta,
                    sincronizada = false
                )
            }
            fotoReferenciaDao.insertAll(fotos)
            
            pedidoId
        }
    }

    // 2. Sincronizar Fotos (cuando haya internet)
    suspend fun sincronizarFotosPendientes(pedidoId: Long): Boolean {
        return withContext(Dispatchers.IO) {
            val fotosPendientes = fotoReferenciaDao.getFotosByPedido(pedidoId).filter { !it.sincronizada }
            
            if (fotosPendientes.isEmpty()) return@withContext true // Ya todo está ok

            var todasSubidas = true
            for (foto in fotosPendientes) {
                // Si es una ruta local, intentar subir
                // Validamos si empieza con http para no resubir (aunque el filtro !sincronizada debería bastar)
                if (!foto.urlFoto.startsWith("http")) {
                    val urlPublica = ImageKitUploader.subirFoto(foto.urlFoto)
                    if (urlPublica != null) {
                        // Actualizar DB con URL pública y marcar sincronizado
                        val fotoActualizada = foto.copy(urlFoto = urlPublica, sincronizada = true)
                        fotoReferenciaDao.update(fotoActualizada)
                    } else {
                        todasSubidas = false
                    }
                } else {
                    // Si ya era http pero estaba marcada false (raro), marcar true
                     val fotoActualizada = foto.copy(sincronizada = true)
                     fotoReferenciaDao.update(fotoActualizada)
                }
            }
            
            // Actualizar estado del pedido si todo se subió (opcional)
            if (todasSubidas) {
                val pedido = pedidoDao.getPedidoById(pedidoId)
                if (pedido != null && !pedido.sincronizado) {
                    pedidoDao.update(pedido.copy(sincronizado = true))
                }
            }
            
            todasSubidas
        }
    }

    suspend fun obtenerFotosDePedido(pedidoId: Long): List<FotoReferencia> {
        return withContext(Dispatchers.IO) {
            fotoReferenciaDao.getFotosByPedido(pedidoId)
        }
    }
    
    suspend fun obtenerPedido(pedidoId: Long): Pedido? {
        return withContext(Dispatchers.IO) {
            pedidoDao.getPedidoById(pedidoId)
        }
    }
}
