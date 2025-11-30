package com.example.makip

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.makip.data.Pedido
import com.example.makip.data.PedidoRepository
import kotlinx.coroutines.launch
import java.net.URLEncoder

class PedidoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PedidoRepository(application)

    /**
     * Crea un pedido y guarda las rutas de las fotos localmente.
     * Inmediatamente intenta sincronizar con ImageKit si hay internet.
     */
    fun realizarPedido(
        pedido: Pedido,
        rutasLocales: List<String>,
        onSuccess: (Long) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // 1. Guardar pedido y referencias locales (Offline First)
                val pedidoId = repository.crearPedidoConReferencias(pedido, rutasLocales)
                
                // 2. Intentar subir fotos en segundo plano
                // No bloqueamos el éxito del pedido si esto falla (se puede reintentar luego)
                launch {
                    try {
                        repository.sincronizarFotosPendientes(pedidoId)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                
                onSuccess(pedidoId)
            } catch (e: Exception) {
                onError("Error al guardar pedido: ${e.message}")
            }
        }
    }

    /**
     * Genera el mensaje de WhatsApp con los datos del pedido y las URLs de las fotos.
     * Si las fotos aún no se han subido, mostrará un aviso de pendiente.
     */
    fun enviarPedidoWhatsApp(context: Context, pedidoId: Long) {
        viewModelScope.launch {
            val pedido = repository.obtenerPedido(pedidoId)
            if (pedido == null) {
                Toast.makeText(context, "Pedido no encontrado", Toast.LENGTH_SHORT).show()
                return@launch
            }

            // Reintentar sincronización antes de enviar si es necesario
            repository.sincronizarFotosPendientes(pedidoId)
            
            val fotos = repository.obtenerFotosDePedido(pedidoId)
            
            val sb = StringBuilder()
            sb.append("Hola Makip! 🎁 Quiero realizar el siguiente pedido:\n\n")
            sb.append("*PEDIDO #${pedido.id}*\n")
            sb.append("--------------------------------\n")
            sb.append("👤 Cliente: ${pedido.celularCliente}\n")
            sb.append("📦 Producto ID: ${pedido.productoId}\n") // Podrías hacer un join para sacar el nombre
            sb.append("🔢 Cantidad: ${pedido.cantidad}\n")
            sb.append("🎨 Detalle: ${pedido.colorOTalla}\n")
            if (!pedido.textoPersonalizado.isNullOrEmpty()) {
                sb.append("✍️ Texto: ${pedido.textoPersonalizado}\n")
            }
            sb.append("💰 Pago: ${pedido.metodoPago}\n")
            sb.append("📍 Dirección: ${pedido.direccionEntrega}, ${pedido.distrito}\n\n")
            
            sb.append("*FOTOS DE REFERENCIA:*\n")
            if (fotos.isEmpty()) {
                sb.append("(Sin fotos de referencia)\n")
            } else {
                fotos.forEachIndexed { index, foto ->
                    if (foto.sincronizada) {
                        sb.append("${index + 1}. ${foto.urlFoto}\n")
                    } else {
                        sb.append("${index + 1}. [Subiendo foto...] (Aún no sincronizada)\n")
                    }
                }
            }

            val mensaje = sb.toString()
            val numeroTelefono = "51999888777" // TU NÚMERO DE WHATSAPP AQUÍ

            try {
                val url = "https://api.whatsapp.com/send?phone=$numeroTelefono&text=${URLEncoder.encode(mensaje, "UTF-8")}"
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(url)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
