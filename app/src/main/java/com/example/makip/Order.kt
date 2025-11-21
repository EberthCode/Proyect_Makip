package com.example.makip

data class Order(
    val id: String,
    val date: String,
    val status: Int, // 1: Pendiente, 2: Proceso, 3: Enviado, 4: Entregado
    val items: List<CartItem>,
    val total: Double,
    val address: String = "Calle Principal 123, Lima",
    val paymentMethod: String = "Visa terminada en 1234"
)