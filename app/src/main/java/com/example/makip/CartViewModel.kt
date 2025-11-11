package com.example.makip

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.makip.Product

class CartViewModel : ViewModel() {
    private val _cartItems = MutableLiveData<MutableList<CartItem>>()
    val cartItems: LiveData<MutableList<CartItem>> get() = _cartItems

    private val _totalPrice = MutableLiveData<Double>()
    val totalPrice: LiveData<Double> get() = _totalPrice

    init {
        _cartItems.value = mutableListOf()
        _totalPrice.value = 0.0
    }

    fun addProductToCart(product: Product, size: String = "M") {
        val currentItems = _cartItems.value ?: mutableListOf()
        val existingItem = currentItems.find { it.product.id == product.id && it.size == size }

        if (existingItem != null) {
            existingItem.quantity++
        } else {
            currentItems.add(CartItem(product, 1, size))
        }

        _cartItems.value = currentItems
        calculateTotal()
    }

    fun increaseQuantity(item: CartItem) {
        item.quantity++
        _cartItems.value = _cartItems.value
        calculateTotal()
    }

    fun decreaseQuantity(item: CartItem) {
        val currentItems = _cartItems.value ?: return
        val existingItem = currentItems.find { it.product.id == item.product.id && it.size == item.size }

        existingItem?.let {
            if (it.quantity > 1) {
                it.quantity--
            } else {
                currentItems.remove(it)
            }
            _cartItems.value = currentItems
            calculateTotal()
        }
    }

    fun removeItem(item: CartItem) {
        val currentItems = _cartItems.value ?: return
        currentItems.remove(item)
        _cartItems.value = currentItems
        calculateTotal()
    }

    private fun calculateTotal() {
        val items = _cartItems.value ?: return
        val subtotal = items.sumOf { it.getTotalPrice() }
        val shipping = if (subtotal > 0) 5.0 else 0.0
        _totalPrice.value = subtotal + shipping
    }

    fun getSubtotal(): Double {
        return _cartItems.value?.sumOf { it.getTotalPrice() } ?: 0.0
    }

    fun getShipping(): Double {
        return if (getSubtotal() > 0) 5.0 else 0.0
    }

    fun clearCart() {
        _cartItems.value = mutableListOf()
        _totalPrice.value = 0.0
    }
}