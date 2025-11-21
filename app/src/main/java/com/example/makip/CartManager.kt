package com.example.makip

object CartManager {
    private val items = mutableListOf<CartItem>()
    private val listeners = mutableListOf<() -> Unit>()

    fun getItems(): List<CartItem> = items

    fun addProduct(product: Product, size: String = "M") {
        val existingItem = items.find { it.product.id == product.id && it.size == size }
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            items.add(CartItem(product, 1, size))
        }
        notifyListeners()
    }

    fun increaseQuantity(item: CartItem) {
        val existingItem = items.find { it.product.id == item.product.id && it.size == item.size }
        existingItem?.let {
            it.quantity++
            notifyListeners()
        }
    }

    fun decreaseQuantity(item: CartItem) {
        val existingItem = items.find { it.product.id == item.product.id && it.size == item.size }
        existingItem?.let {
            if (it.quantity > 1) {
                it.quantity--
            } else {
                items.remove(it)
            }
            notifyListeners()
        }
    }

    fun removeItem(item: CartItem) {
        items.remove(item)
        notifyListeners()
    }

    fun clear() {
        items.clear()
        notifyListeners()
    }

    fun addListener(listener: () -> Unit) {
        listeners.add(listener)
    }

    fun removeListener(listener: () -> Unit) {
        listeners.remove(listener)
    }

    private fun notifyListeners() {
        listeners.forEach { it() }
    }
}