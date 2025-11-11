package com.example.makip

import com.example.makip.Product

data class CartItem(
    val product: Product,
    var quantity: Int,
    val size: String = "M"
) {
    fun getTotalPrice(): Double = product.price * quantity
}