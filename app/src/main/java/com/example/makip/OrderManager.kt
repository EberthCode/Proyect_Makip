package com.example.makip

object OrderManager {
    private val orders = mutableListOf<Order>()

    fun addOrder(order: Order) {
        // Agregamos al principio para que sea el más reciente
        orders.add(0, order)
    }

    fun getLatestOrder(): Order? {
        return orders.firstOrNull()
    }

    fun getAllOrders(): List<Order> {
        return orders
    }
}