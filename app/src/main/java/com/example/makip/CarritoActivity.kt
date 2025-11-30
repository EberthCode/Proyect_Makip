package com.example.makip

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class CarritoActivity : AppCompatActivity() {

    private lateinit var cartViewModel: CartViewModel
    private lateinit var cartAdapter: CartAdapter

    // Views
    private lateinit var textSubtotal: TextView
    private lateinit var textShipping: TextView
    private lateinit var textTotal: TextView
    private lateinit var btnCheckout: Button
    private lateinit var btnBack: ImageButton

    companion object {
        private const val TAG = "CarritoActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
            // Configurar barra de estado transparente con iconos NEGROS
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
            windowInsetsController.isAppearanceLightStatusBars = true
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        super.onCreate(savedInstanceState)
            // SIMPLE: El sistema reserva automáticamente el espacio para las barras
            WindowCompat.setDecorFitsSystemWindows(window, true)
        try {
            setContentView(R.layout.activity_carrito)


            Log.d(TAG, "CarritoActivity creada")

            // Inicializar ViewModel
            cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]
            Log.d(TAG, "ViewModel inicializado")

            initViews()
            setupRecyclerView()
            setupObservers()
            setupCheckoutButton()
            setupBackButton()

            Log.d(TAG, "CarritoActivity configurada exitosamente")

        } catch (e: Exception) {
            Log.e(TAG, "Error en onCreate: ${e.message}", e)
            Toast.makeText(this, "Error al cargar el carrito", Toast.LENGTH_SHORT).show()
            finish()
        }
    }


    private fun initViews() {
        try {
            textSubtotal = findViewById(R.id.text_subtotal)
            textShipping = findViewById(R.id.text_shipping)
            textTotal = findViewById(R.id.text_total)
            btnCheckout = findViewById(R.id.btn_checkout)
            btnBack = findViewById(R.id.btn_back)
            Log.d(TAG, "Vistas inicializadas")
        } catch (e: Exception) {
            Log.e(TAG, "Error al inicializar vistas: ${e.message}")
            throw e
        }
    }

    private fun setupBackButton() {
        btnBack.setOnClickListener {
            finish() // Cierra la actividad y vuelve a la anterior (Catálogo)
        }
    }

    private fun setupRecyclerView() {
        try {
            val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_cart)
            cartAdapter = CartAdapter(
                emptyList(),
                onIncreaseClick = { item ->
                    cartViewModel.increaseQuantity(item)
                },
                onDecreaseClick = { item ->
                    cartViewModel.decreaseQuantity(item)
                },
                onRemoveClick = { item ->
                    cartViewModel.removeItem(item)
                }
            )

            recyclerView.apply {
                adapter = cartAdapter
                layoutManager = LinearLayoutManager(this@CarritoActivity)
            }
            Log.d(TAG, "RecyclerView configurado")
        } catch (e: Exception) {
            Log.e(TAG, "Error al configurar RecyclerView: ${e.message}")
            throw e
        }
    }

    private fun setupObservers() {
        cartViewModel.cartItems.observe(this) { items ->
            try {
                Log.d(TAG, "Actualizando carrito con ${items.size} items")
                cartAdapter.updateData(items)
                updateTotals()
            } catch (e: Exception) {
                Log.e(TAG, "Error en observer: ${e.message}")
            }
        }
    }

    private fun setupCheckoutButton() {
        btnCheckout.setOnClickListener {
            try {
                val currentItems = cartViewModel.cartItems.value
                if (currentItems.isNullOrEmpty()) {
                    Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
                } else {
                    // Enviar pedido por WhatsApp
                    enviarPedidoWhatsApp(currentItems)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error en checkout: ${e.message}")
            }
        }
    }

    private fun enviarPedidoWhatsApp(items: List<CartItem>) {
        val subtotal = cartViewModel.getSubtotal()
        val shipping = cartViewModel.getShipping()
        val total = subtotal + shipping

        // Construir el mensaje
        val sb = StringBuilder()
        sb.append("Hola Makip! 🎁 Quiero realizar el siguiente pedido:\n\n")
        sb.append("*MI PEDIDO*\n")
        sb.append("================================\n\n")
        
        items.forEachIndexed { index, item ->
            sb.append("*${index + 1}. ${item.product.name}*\n")
            sb.append("💰 Precio: S/${String.format("%.2f", item.product.price)}\n")
            sb.append("🔢 Cantidad: ${item.quantity}\n")
            
            if (!item.size.isNullOrEmpty()) {
                sb.append("📏 Talla: ${item.size}\n")
            }
            if (!item.color.isNullOrEmpty()) {
                sb.append("🎨 Color: ${item.color}\n")
            }
            if (!item.customText.isNullOrEmpty()) {
                sb.append("✍️ Texto personalizado: ${item.customText}\n")
            }
            
            val itemTotal = item.product.price * item.quantity
            sb.append("💵 Subtotal: S/${String.format("%.2f", itemTotal)}\n")
            sb.append("--------------------------------\n")
        }
        
        sb.append("\n*RESUMEN DEL PEDIDO*\n")
        sb.append("================================\n")
        sb.append("Subtotal: S/${String.format("%.2f", subtotal)}\n")
        sb.append("Envío: S/${String.format("%.2f", shipping)}\n")
        sb.append("*TOTAL: S/${String.format("%.2f", total)}*\n\n")
        sb.append("¡Espero tu respuesta! 😊")

        val mensaje = sb.toString()
        val numeroTelefono = "51981390836" // Número de WhatsApp de Makip

        try {
            val url = "https://api.whatsapp.com/send?phone=$numeroTelefono&text=${java.net.URLEncoder.encode(mensaje, "UTF-8")}"
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                data = android.net.Uri.parse(url)
            }
            startActivity(intent)
            
            // Opcional: Crear orden local para historial
            createOrder(items)
            
            Toast.makeText(this, "Abriendo WhatsApp...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Error al abrir WhatsApp: ${e.message}")
            Toast.makeText(this, "No se pudo abrir WhatsApp. ¿Está instalado?", Toast.LENGTH_LONG).show()
        }
    }

    private fun createOrder(items: List<CartItem>) {
        val subtotal = cartViewModel.getSubtotal()
        val shipping = cartViewModel.getShipping()
        val total = subtotal + shipping

        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        val order = Order(
            id = UUID.randomUUID().toString().substring(0, 8).uppercase(),
            date = currentDate,
            status = 2, // Empieza en "En Proceso"
            items = ArrayList(items), // Copia de items
            total = total
        )

        OrderManager.addOrder(order)
        Log.d(TAG, "Orden creada: ${order.id}")
    }

    private fun updateTotals() {
        try {
            val subtotal = cartViewModel.getSubtotal()
            val shipping = cartViewModel.getShipping()
            val total = subtotal + shipping

            textSubtotal.text = "S/${"%.2f".format(subtotal)}"
            textShipping.text = "S/${"%.2f".format(shipping)}"
            textTotal.text = "S/${"%.2f".format(total)}"

            Log.d(TAG, "Totales actualizados: Subtotal=$$subtotal, Total=$$total")
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar totales: ${e.message}")
        }
    }
}