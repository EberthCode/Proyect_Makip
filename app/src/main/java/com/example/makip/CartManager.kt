package com.example.makip

object CartManager {
    private val items = mutableListOf<CartItem>()
    private val listeners = mutableListOf<() -> Unit>()

    fun getItems(): List<CartItem> = items

    /**
     * Adds a product with specific customization options.
     * If an identical item exists (same product, size, color, text, images), quantity is increased.
     */
    fun addCartItem(
        product: Product,
        quantity: Int,
        size: String? = null,
        color: String? = null,
        customText: String? = null,
        customImageUris: List<String> = emptyList()
    ) {
        val existingItem = items.find {
            it.product.id == product.id &&
                    it.size == size &&
                    it.color == color &&
                    it.customText == customText &&
                    it.customImageUris == customImageUris
        }

        if (existingItem != null) {
            existingItem.quantity += quantity
        } else {
            items.add(CartItem(product, quantity, size, color, customText, customImageUris))
        }
        notifyListeners()
    }
    
    // Deprecated or legacy support wrapper
    fun addProduct(product: Product, size: String = "M") {
       addCartItem(product, 1, size = size)
    }

    fun increaseQuantity(item: CartItem) {
        // Logic relies on reference equality or finding the exact item object in the list
        // Since we are modifying the item in place if found in UI, we can just check existence or update directly if passed by reference
        val existingItem = items.find { it == item }
        existingItem?.let {
            it.quantity++
            notifyListeners()
        }
    }

    fun decreaseQuantity(item: CartItem) {
        val existingItem = items.find { it == item }
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
