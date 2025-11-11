package com.example.makip

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

class CarritoActivity : AppCompatActivity() {

    private lateinit var cartViewModel: CartViewModel
    private lateinit var cartAdapter: CartAdapter

    companion object {
        private const val TAG = "CarritoActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        Log.d(TAG, "CarritoActivity creada")

        try {
            // Inicializar ViewModel
            cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]
            Log.d(TAG, "ViewModel inicializado")

            initViews()
            setupRecyclerView()
            setupObservers()
            setupCheckoutButton()

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
            Log.d(TAG, "Vistas inicializadas")
        } catch (e: Exception) {
            Log.e(TAG, "Error al inicializar vistas: ${e.message}")
            throw e
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
                if (cartViewModel.cartItems.value.isNullOrEmpty()) {
                    Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "¡Compra realizada con éxito!", Toast.LENGTH_LONG).show()
                    cartViewModel.clearCart()
                    finish()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error en checkout: ${e.message}")
            }
        }
    }

    private fun updateTotals() {
        try {
            val subtotal = cartViewModel.getSubtotal()
            val shipping = cartViewModel.getShipping()
            val total = subtotal + shipping

            textSubtotal.text = "$${"%.2f".format(subtotal)}"
            textShipping.text = "$${"%.2f".format(shipping)}"
            textTotal.text = "$${"%.2f".format(total)}"

            Log.d(TAG, "Totales actualizados: Subtotal=$$subtotal, Total=$$total")
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar totales: ${e.message}")
        }
    }

    // Añade estas declaraciones si no las tienes
    private lateinit var textSubtotal: TextView
    private lateinit var textShipping: TextView
    private lateinit var textTotal: TextView
    private lateinit var btnCheckout: Button
}