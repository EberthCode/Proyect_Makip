package com.example.makip

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TrackingActivity : AppCompatActivity() {

    private lateinit var rvOrderItems: RecyclerView
    private lateinit var adapter: OrderItemAdapter

    // Referencias al Timeline
    private lateinit var dot1: ImageView; private lateinit var line1: View
    private lateinit var dot2: ImageView; private lateinit var line2: View
    private lateinit var dot3: ImageView; private lateinit var line3: View
    private lateinit var dot4: ImageView
    private lateinit var tvStatusTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            enableEdgeToEdge() // Activar Borde a Borde
            setContentView(R.layout.activity_tracking)

            // Ajustar padding para barras de sistema
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_tracking)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            setupToolbar()
            setupTimelineViews()
            loadOrderData()
        } catch (e: Exception) {
            Log.e("TrackingActivity", "Error in onCreate", e)
            Toast.makeText(this, "Error al cargar pedidos: ${e.message}", Toast.LENGTH_LONG).show()
            finish() // Cierra la actividad para evitar que se quede en blanco o crashee peor
        }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarTracking)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupTimelineViews() {
        dot1 = findViewById(R.id.dotStep1); line1 = findViewById(R.id.lineStep1)
        dot2 = findViewById(R.id.dotStep2); line2 = findViewById(R.id.lineStep2)
        dot3 = findViewById(R.id.dotStep3); line3 = findViewById(R.id.lineStep3)
        dot4 = findViewById(R.id.dotStep4)
        tvStatusTitle = findViewById(R.id.tvStatusTitle)
    }

    private fun loadOrderData() {
        rvOrderItems = findViewById(R.id.rvOrderItems)
        rvOrderItems.layoutManager = LinearLayoutManager(this)

        // Intentamos obtener la orden real más reciente
        val latestOrder = OrderManager.getLatestOrder()

        val itemsToShow: List<CartItem>
        val statusToShow: Int

        if (latestOrder != null) {
            // CASO 1: Hay una compra real guardada en memoria
            itemsToShow = latestOrder.items
            statusToShow = latestOrder.status
        } else {
            // CASO 2: Modo demostración (si aún no has comprado nada)
            val mockProduct1 = Product(101, "Camiseta Demo", 20.0, "Ropa")
            val mockProduct2 = Product(102, "Gorra Demo", 15.0, "Accesorios")
            
            itemsToShow = listOf(
                CartItem(mockProduct1, 1, "M"),
                CartItem(mockProduct2, 2, "L")
            )
            statusToShow = 2 // Simulamos estado "En Proceso"
        }

        // Configuramos el adaptador con la lista correcta de CartItems
        adapter = OrderItemAdapter(itemsToShow)
        rvOrderItems.adapter = adapter

        // Actualizamos la línea de tiempo visual
        updateTimelineColor(statusToShow)
    }

    private fun updateTimelineColor(step: Int) {
        // Usamos purple_700 que es seguro y existe en colors.xml
        // Evitamos purple_500 porque puede haber conflictos con el archivo xml en res/color/
        val activeColor = ContextCompat.getColor(this, R.color.purple_700) 
        val inactiveColor = ContextCompat.getColor(this, android.R.color.darker_gray)
        
        val activeDrawable = R.drawable.bg_timeline_dot_active
        val inactiveDrawable = R.drawable.bg_timeline_dot_inactive

        // Lógica visual para los puntos
        dot1.setImageResource(if (step >= 1) activeDrawable else inactiveDrawable)
        dot2.setImageResource(if (step >= 2) activeDrawable else inactiveDrawable)
        dot3.setImageResource(if (step >= 3) activeDrawable else inactiveDrawable)
        dot4.setImageResource(if (step >= 4) activeDrawable else inactiveDrawable)

        // Lógica visual para las líneas
        line1.setBackgroundColor(if (step >= 2) activeColor else inactiveColor)
        line2.setBackgroundColor(if (step >= 3) activeColor else inactiveColor)
        line3.setBackgroundColor(if (step >= 4) activeColor else inactiveColor)

        // Actualizar Texto
        tvStatusTitle.text = when(step) {
            1 -> "Pedido Pendiente"
            2 -> "Tu pedido está en proceso"
            3 -> "Tu pedido ha sido enviado"
            4 -> "Pedido Entregado"
            else -> "Estado Desconocido"
        }
    }
}