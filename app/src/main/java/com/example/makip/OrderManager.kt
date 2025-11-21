package com.example.makip

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object OrderManager {

    private const val PREFS_NAME = "OrderPrefs"
    private const val KEY_ORDERS = "orders"

    private val orders = mutableListOf<Order>()
    private val listeners = mutableListOf<() -> Unit>()
    private lateinit var sharedPrefs: SharedPreferences
    private val gson = Gson()

    /** Inicializa el OrderManager. Debe llamarse en Application o primera Activity. */
    fun initialize(context: Context) {
        sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadOrders()
    }

    /** Carga pedidos desde SharedPreferences. */
    private fun loadOrders() {
        val json = sharedPrefs.getString(KEY_ORDERS, null)
        if (json != null) {
            val type = object : TypeToken<List<Order>>() {}.type
            val savedOrders: List<Order> = gson.fromJson(json, type)
            orders.clear()
            orders.addAll(savedOrders)
        }
    }

    /** Guarda pedidos en SharedPreferences. */
    private fun saveOrders() {
        val json = gson.toJson(orders)
        sharedPrefs.edit().putString(KEY_ORDERS, json).apply()
    }

    /** Agrega un nuevo pedido. */
    fun addOrder(order: Order) {
        // Agregamos al principio para que sea el más reciente
        orders.add(0, order)
        saveOrders()
        notifyListeners()
    }

    /** Obtiene el pedido más reciente. */
    fun getLatestOrder(): Order? {
        return orders.firstOrNull()
    }

    /** Obtiene todos los pedidos. */
    fun getAllOrders(): List<Order> {
        return orders.toList()
    }

    /** Actualiza el estado de un pedido. */
    fun updateOrderStatus(orderId: String, newStatus: Int) {
        val order = orders.find { it.id == orderId }
        order?.let {
            val index = orders.indexOf(it)
            if (index != -1) {
                val updatedOrder = it.copy(status = newStatus)
                orders[index] = updatedOrder
                saveOrders()
                notifyListeners()
            }
        }
    }

    /** Obtiene un pedido por ID. */
    fun getOrderById(orderId: String): Order? {
        return orders.find { it.id == orderId }
    }

    /** Agrega un listener para notificaciones de cambios. */
    fun addListener(listener: () -> Unit) {
        listeners.add(listener)
    }

    /** Elimina un listener. */
    fun removeListener(listener: () -> Unit) {
        listeners.remove(listener)
    }

    /** Notifica a los listeners de cambios. */
    private fun notifyListeners() {
        listeners.forEach { it() }
    }

    /** Limpia todos los pedidos (útil para testing). */
    fun clearAllOrders() {
        orders.clear()
        saveOrders()
        notifyListeners()
    }
}
