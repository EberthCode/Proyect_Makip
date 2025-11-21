package com.example.makip

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AdminInventoryFragment : Fragment() {

    private lateinit var rvProducts: RecyclerView
    private lateinit var fabAddProduct: FloatingActionButton
    private lateinit var adapter: AdminProductAdapter
    private val productUpdateListener: () -> Unit = { loadProducts() }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_inventory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRecyclerView()
        setupFab()
        loadProducts()

        // Agregar listener para actualizaciones
        ProductManager.addListener(productUpdateListener)
    }

    private fun initViews(view: View) {
        rvProducts = view.findViewById(R.id.rv_admin_products)
        fabAddProduct = view.findViewById(R.id.fab_add_product)
    }

    private fun setupRecyclerView() {
        adapter =
                AdminProductAdapter(
                        products = emptyList(),
                        onEditClick = { product -> editProduct(product) },
                        onDeleteClick = { product -> confirmDelete(product) }
                )

        rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        rvProducts.adapter = adapter
    }

    private fun setupFab() {
        fabAddProduct.setOnClickListener {
            val intent = Intent(requireContext(), AddProductActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadProducts() {
        val products = ProductManager.getProducts()
        adapter.updateData(products)
    }

    private fun editProduct(product: Product) {
        val intent = Intent(requireContext(), AddProductActivity::class.java)
        intent.putExtra("PRODUCT_ID", product.id)
        intent.putExtra("EDIT_MODE", true)
        startActivity(intent)
    }

    private fun confirmDelete(product: Product) {
        AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Producto")
                .setMessage("¿Estás seguro de que deseas eliminar '${product.name}'?")
                .setPositiveButton("Eliminar") { _, _ -> ProductManager.deleteProduct(product.id) }
                .setNegativeButton("Cancelar", null)
                .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ProductManager.removeListener(productUpdateListener)
    }
}
