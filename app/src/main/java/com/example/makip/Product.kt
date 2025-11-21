package com.example.makip

data class Product(
        val id: Int,
        val name: String,
        val price: Double,
        val category: String,
        val imageUrl: String = "" // URL de imagen, vacío usa placeholder
)
