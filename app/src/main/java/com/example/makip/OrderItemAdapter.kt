package com.example.makip

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderItemAdapter(private val items: List<CartItem>) :
    RecyclerView.Adapter<OrderItemAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProductName: TextView = itemView.findViewById(R.id.text_product_name)
        val tvProductPrice: TextView = itemView.findViewById(R.id.text_product_price)
        val tvProductSize: TextView? = itemView.findViewById(R.id.text_product_size)
        // Usamos los mismos IDs que item_product_card.xml
        // Nota: item_product_card tiene botones de eliminar/cantidad que quizás queramos ocultar aquí,
        // pero por ahora solo llenaremos los datos de texto.
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_card, parent, false)
        
        // Ocultar controles de edición del carrito ya que es un historial
        view.findViewById<View>(R.id.btn_remove)?.visibility = View.GONE
        view.findViewById<View>(R.id.layout_quantity)?.visibility = View.GONE
        
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val item = items[position]

        // Asignamos los datos
        holder.tvProductName.text = "${item.product.name} (x${item.quantity})"
        holder.tvProductPrice.text = "S/${item.getTotalPrice()}"
        holder.tvProductSize?.text = "Talla: ${item.size}"
    }

    override fun getItemCount() = items.size
}