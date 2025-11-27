package com.example.makip

data class CartItem(
    val product: Product,
    var quantity: Int,
    val size: String? = null,
    val color: String? = null,
    val customText: String? = null,
    val customImageUris: List<String> = emptyList()
) {
    fun getTotalPrice(): Double = product.price * quantity
}
