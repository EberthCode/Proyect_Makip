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
        super.onCreate(savedInstanceState)
        try {
            enableEdgeToEdge() // Activar Borde a Borde
            setContentView(R.layout.activity_carrito)

            // Ajustar padding para barras de sistema
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_carrito)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            // --- CORRECCIÓN: Iconos de barra de estado en BLANCO para fondo negro ---
            val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
            windowInsetsController.isAppearanceLightStatusBars = false // false = Iconos Blancos
            // ----------------------------------------------------------------------

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
                    // Crear Orden
                    createOrder(currentItems)
                    
                    Toast.makeText(this, "¡Compra realizada con éxito!", Toast.LENGTH_LONG).show()
                    cartViewModel.clearCart()
                    finish()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error en checkout: ${e.message}")
            }
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