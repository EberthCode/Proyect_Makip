package com.example.makip

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AdminOrderDetailActivity : AppCompatActivity() {

    private lateinit var textOrderId: TextView
    private lateinit var textOrderDate: TextView
    private lateinit var textOrderTotal: TextView
    private lateinit var textOrderAddress: TextView
    private lateinit var textOrderPayment: TextView
    private lateinit var spinnerStatus: Spinner
    private lateinit var btnUpdateStatus: Button
    private lateinit var rvOrderItems: RecyclerView

    private var orderId: String = ""
    private var currentOrder: Order? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar barra de estado transparente con iconos NEGROS
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(window, true)
        val windowInsetsController = androidx.core.view.WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = true
        windowInsetsController.show(androidx.core.view.WindowInsetsCompat.Type.systemBars())

        setContentView(R.layout.activity_admin_order_detail)

        orderId = intent.getStringExtra("ORDER_ID") ?: ""

        initViews()
        setupToolbar()
        setupStatusSpinner()
        loadOrderData()
        setupUpdateButton()
    }

    private fun initViews() {
        textOrderId = findViewById(R.id.text_detail_order_id)
        textOrderDate = findViewById(R.id.text_detail_order_date)
        textOrderTotal = findViewById(R.id.text_detail_order_total)
        textOrderAddress = findViewById(R.id.text_detail_order_address)
        textOrderPayment = findViewById(R.id.text_detail_order_payment)
        spinnerStatus = findViewById(R.id.spinner_order_status)
        btnUpdateStatus = findViewById(R.id.btn_update_status)
        rvOrderItems = findViewById(R.id.rv_detail_order_items)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_order_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detalle del Pedido"

        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupStatusSpinner() {
        val statuses = arrayOf("Pendiente", "En Proceso", "Enviado", "Entregado")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statuses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = adapter
    }

    private fun loadOrderData() {
        currentOrder = OrderManager.getOrderById(orderId)

        currentOrder?.let { order ->
            textOrderId.text = "Pedido #${order.id}"
            textOrderDate.text = "Fecha: ${order.date}"
            textOrderTotal.text = "Total: $${String.format("%.2f", order.total)}"
            textOrderAddress.text = "Dirección: ${order.address}"
            textOrderPayment.text = "Pago: ${order.paymentMethod}"

            // Seleccionar estado actual en spinner (status - 1 porque el array empieza en 0)
            spinnerStatus.setSelection(order.status - 1)

            // Configurar RecyclerView de items
            rvOrderItems.layoutManager = LinearLayoutManager(this)
            rvOrderItems.adapter = OrderItemAdapter(order.items)
        }
    }

    private fun setupUpdateButton() {
        btnUpdateStatus.setOnClickListener { updateOrderStatus() }
    }

    private fun updateOrderStatus() {
        val newStatus = spinnerStatus.selectedItemPosition + 1 // +1 porque los estados van de 1-4

        OrderManager.updateOrderStatus(orderId, newStatus)

        Toast.makeText(this, "Estado actualizado exitosamente", Toast.LENGTH_SHORT).show()
        finish()
    }
}
