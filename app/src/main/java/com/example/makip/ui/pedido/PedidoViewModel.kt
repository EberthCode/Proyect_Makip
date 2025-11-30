package com.example.makip.ui.pedido

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.makip.data.Pedido
import com.example.makip.data.PedidoRepository
import kotlinx.coroutines.launch
import java.net.URLEncoder

class PedidoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PedidoRepository(application)

    fun realizarPedido(
        pedido: Pedido,
        rutasLocales: List<String>,
        onSuccess: (Long) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // 1. Guardar Offline
                val pedidoId = repository.crearPedidoConReferencias(pedido, rutasLocales)
                
                // 2. Intentar Sincronizar de inmediato (si hay internet)
                // No bloqueamos la UI si falla, se puede reintentar luego o mandar rutas locales por texto
                try {
                    repository.sincronizarFotosPendientes(pedidoId)
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Si falla la subida, igual el pedido existe localmente
                }

                onSuccess(pedidoId)
            } catch (e: Exception) {
                onError(e.message ?: "Error al crear pedido")
            }
        }
    }

    fun enviarPedidoWhatsApp(pedidoId: Long, celularNegocio: String = "51999999999") {
        viewModelScope.launch {
            val pedido = repository.obtenerPedido(pedidoId) ?: return@launch // Necesitas getPedidoById en DAO
            val fotos = repository.obtenerFotosDePedido(pedidoId)

            val sb = StringBuilder()
            sb.append("Hola Makip, quiero confirmar mi pedido #${pedidoId}:\n")
            sb.append("- Producto ID: ${pedido.productoId}\n")
            sb.append("- Cantidad: ${pedido.cantidad}\n")
            sb.append("- Detalle: ${pedido.textoPersonalizado ?: ""}\n")
            sb.append("- Entrega en: ${pedido.distrito}\n\n")
            
            if (fotos.isNotEmpty()) {
                sb.append("Referencias adjuntas:\n")
                fotos.forEachIndexed { index, foto ->
                    if (foto.sincronizada) {
                        sb.append("Foto ${index + 1}: ${foto.urlFoto}\n")
                    } else {
                         sb.append("Foto ${index + 1}: (Pendiente de subida)\n")
                    }
                }
            }

            val url = "https://api.whatsapp.com/send?phone=$celularNegocio&text=${URLEncoder.encode(sb.toString(), "UTF-8")}"
            
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            
            getApplication<Application>().startActivity(intent)
        }
    }
}
