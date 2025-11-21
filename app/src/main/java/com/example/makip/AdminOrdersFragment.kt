package com.example.makip

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup

class AdminOrdersFragment : Fragment() {

    private lateinit var rvOrders: RecyclerView
    private lateinit var chipGroup: ChipGroup
    private lateinit var adapter: AdminOrderAdapter
    private val orderUpdateListener: () -> Unit = { loadOrders() }
    private var currentFilter = 0 // 0 = Todos

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRecyclerView()
        setupFilterChips()
        loadOrders()

        // Agregar listener para actualizaciones
        OrderManager.addListener(orderUpdateListener)
    }

    private fun initViews(view: View) {
        rvOrders = view.findViewById(R.id.rv_admin_orders)
        chipGroup = view.findViewById(R.id.chip_group_order_filter)
    }

    private fun setupRecyclerView() {
        adapter = AdminOrderAdapter { order -> openOrderDetail(order) }

        rvOrders.layoutManager = LinearLayoutManager(requireContext())
        rvOrders.adapter = adapter
    }

    private fun setupFilterChips() {
        chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            when {
                checkedIds.contains(R.id.chip_all_orders) -> currentFilter = 0
                checkedIds.contains(R.id.chip_pending) -> currentFilter = 1
                checkedIds.contains(R.id.chip_processing) -> currentFilter = 2
                checkedIds.contains(R.id.chip_shipped) -> currentFilter = 3
                checkedIds.contains(R.id.chip_delivered) -> currentFilter = 4
                else -> currentFilter = 0
            }
            loadOrders()
        }
    }

    private fun loadOrders() {
        val allOrders = OrderManager.getAllOrders()
        val filteredOrders =
                if (currentFilter == 0) {
                    allOrders
                } else {
                    allOrders.filter { it.status == currentFilter }
                }

        adapter.updateData(filteredOrders)
    }

    private fun openOrderDetail(order: Order) {
        val intent = Intent(requireContext(), AdminOrderDetailActivity::class.java)
        intent.putExtra("ORDER_ID", order.id)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        loadOrders()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        OrderManager.removeListener(orderUpdateListener)
    }
}
