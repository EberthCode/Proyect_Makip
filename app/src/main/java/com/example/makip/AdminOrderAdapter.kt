package com.example.makip

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdminOrderAdapter(
        private var orders: List<Order> = emptyList(),
        private val onOrderClick: (Order) -> Unit
) : RecyclerView.Adapter<AdminOrderAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textOrderId: TextView = view.findViewById(R.id.text_order_id)
        val textOrderDate: TextView = view.findViewById(R.id.text_order_date)
        val textOrderTotal: TextView = view.findViewById(R.id.text_order_total)
        val textOrderStatus: TextView = view.findViewById(R.id.text_order_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_admin_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]

        holder.textOrderId.text = "Pedido #${order.id}"
        holder.textOrderDate.text = order.date
        holder.textOrderTotal.text = "$${String.format("%.2f", order.total)}"

        // Estado y color
        val (statusText, statusColor) =
                when (order.status) {
                    1 -> "Pendiente" to Color.parseColor("#FF9800")
                    2 -> "En Proceso" to Color.parseColor("#2196F3")
                    3 -> "Enviado" to Color.parseColor("#9C27B0")
                    4 -> "Entregado" to Color.parseColor("#4CAF50")
                    else -> "Desconocido" to Color.GRAY
                }

        holder.textOrderStatus.text = statusText
        holder.textOrderStatus.setTextColor(statusColor)

        holder.itemView.setOnClickListener { onOrderClick(order) }
    }

    override fun getItemCount() = orders.size

    fun updateData(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}
