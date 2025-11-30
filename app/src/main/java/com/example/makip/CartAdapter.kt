package com.example.makip

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load

class CartAdapter(
    private var cartItems: List<CartItem>,
    private val onIncreaseClick: (CartItem) -> Unit,
    private val onDecreaseClick: (CartItem) -> Unit,
    private val onRemoveClick: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val TAG = "CartAdapter"

    fun updateData(newItems: List<CartItem>) {
        Log.d(TAG, "Actualizando datos con ${newItems.size} items")
        cartItems = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        try {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_product_card, parent, false)
            return CartViewHolder(view)
        } catch (e: Exception) {
            Log.e(TAG, "Error en onCreateViewHolder: ${e.message}")
            throw e
        }
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        try {
            val cartItem = cartItems[position]
            holder.bind(cartItem)
        } catch (e: Exception) {
            Log.e(TAG, "Error en onBindViewHolder: ${e.message}")
        }
    }

    override fun getItemCount(): Int = cartItems.size

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.text_product_name)
        private val productSize: TextView = itemView.findViewById(R.id.text_product_size)
        private val productPrice: TextView = itemView.findViewById(R.id.text_product_price)
        private val productImage: ImageView = itemView.findViewById(R.id.product_image)
        private val quantityText: TextView = itemView.findViewById(R.id.text_quantity)
        private val btnIncrease: ImageButton = itemView.findViewById(R.id.btn_increase)
        private val btnDecrease: ImageButton = itemView.findViewById(R.id.btn_decrease)
        private val btnRemove: ImageButton = itemView.findViewById(R.id.btn_remove)

        fun bind(cartItem: CartItem) {
            try {
                productName.text = "${cartItem.product.name} personalizada"
                productSize.text = "Talla ${cartItem.size}"
                productPrice.text = "S/${cartItem.product.price}"
                quantityText.text = cartItem.quantity.toString()

                // Cargar imagen del producto
                if (cartItem.product.imageUrl.isNotEmpty()) {
                    productImage.load(cartItem.product.imageUrl) {
                        crossfade(true)
                        placeholder(R.drawable.ic_product_placeholder)
                        error(R.drawable.ic_product_placeholder)
                    }
                } else {
                    productImage.setImageResource(R.drawable.ic_product_placeholder)
                }

                btnIncrease.setOnClickListener { onIncreaseClick(cartItem) }
                btnDecrease.setOnClickListener { onDecreaseClick(cartItem) }
                btnRemove.setOnClickListener { onRemoveClick(cartItem) }
            } catch (e: Exception) {
                Log.e(TAG, "Error en bind: ${e.message}")
            }
        }
    }
}