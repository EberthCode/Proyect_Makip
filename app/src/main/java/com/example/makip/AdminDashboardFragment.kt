package com.example.makip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AdminDashboardFragment : Fragment() {

    private lateinit var textTotalSales: TextView
    private lateinit var textPendingOrders: TextView
    private lateinit var textTotalProducts: TextView
    private lateinit var rvRecentActivity: RecyclerView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        loadMetrics()
        loadRecentActivity()
    }

    private fun initViews(view: View) {
        textTotalSales = view.findViewById(R.id.text_total_sales)
        textPendingOrders = view.findViewById(R.id.text_pending_orders)
        textTotalProducts = view.findViewById(R.id.text_total_products)
        rvRecentActivity = view.findViewById(R.id.rv_recent_activity)
    }

    private fun loadMetrics() {
        // Calcular métricas desde los managers
        val allOrders = OrderManager.getAllOrders()
        val totalSales = allOrders.sumOf { it.total }
        val pendingOrders = allOrders.count { it.status == 1 || it.status == 2 }
        val totalProducts = ProductManager.getProducts().size

        textTotalSales.text = "$${String.format("%.2f", totalSales)}"
        textPendingOrders.text = pendingOrders.toString()
        textTotalProducts.text = totalProducts.toString()
    }

    private fun loadRecentActivity() {
        val recentOrders = OrderManager.getAllOrders().take(5)

        rvRecentActivity.layoutManager = LinearLayoutManager(requireContext())
        rvRecentActivity.adapter = RecentActivityAdapter(recentOrders)
    }

    override fun onResume() {
        super.onResume()
        // Actualizar métricas al volver al fragmento
        loadMetrics()
        loadRecentActivity()
    }
}

// Adapter simple para actividad reciente
class RecentActivityAdapter(private val orders: List<Order>) :
        RecyclerView.Adapter<RecentActivityAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textOrderInfo: TextView = view.findViewById(R.id.text_order_info)
        val textOrderDate: TextView = view.findViewById(R.id.text_order_date)
        val textOrderAmount: TextView = view.findViewById(R.id.text_order_amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_recent_activity, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        holder.textOrderInfo.text = "Pedido #${order.id}"
        holder.textOrderDate.text = order.date
        holder.textOrderAmount.text = "$${String.format("%.2f", order.total)}"
    }

    override fun getItemCount() = orders.size
}
