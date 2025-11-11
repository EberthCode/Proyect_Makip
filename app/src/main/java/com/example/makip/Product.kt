package com.example.makip

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val category: String,
    val imageRes: Int = R.drawable.ic_product_placeholder
)