package com.example.makip

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CartViewModel : ViewModel() {
    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> get() = _cartItems

    private val _totalPrice = MutableLiveData<Double>()
    val totalPrice: LiveData<Double> get() = _totalPrice

    // Listener para actualizar LiveData cuando cambia el CartManager
    private val cartListener: () -> Unit = {
        updateState()
    }

    init {
        // Suscribirse a cambios en el Singleton
        CartManager.addListener(cartListener)
        updateState()
    }

    override fun onCleared() {
        super.onCleared()
        CartManager.removeListener(cartListener)
    }

    fun addProductToCart(product: Product, size: String = "M") {
        CartManager.addProduct(product, size)
    }

    fun increaseQuantity(item: CartItem) {
        CartManager.increaseQuantity(item)
    }

    fun decreaseQuantity(item: CartItem) {
        CartManager.decreaseQuantity(item)
    }

    fun removeItem(item: CartItem) {
        CartManager.removeItem(item)
    }

    fun clearCart() {
        CartManager.clear()
    }

    fun getSubtotal(): Double {
        return CartManager.getItems().sumOf { it.getTotalPrice() }
    }

    fun getShipping(): Double {
        return if (getSubtotal() > 0) 5.0 else 0.0
    }

    private fun updateState() {
        val items = CartManager.getItems().toList() // Copia defensiva
        _cartItems.postValue(items)
        
        val subtotal = items.sumOf { it.getTotalPrice() }
        val shipping = if (subtotal > 0) 5.0 else 0.0
        _totalPrice.postValue(subtotal + shipping)
    }
}