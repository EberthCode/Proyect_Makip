package com.example.makip

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load

class AdminProductAdapter(
        private var products: List<Product>,
        private val onEditClick: (Product) -> Unit,
        private val onDeleteClick: (Product) -> Unit
) : RecyclerView.Adapter<AdminProductAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageProduct: ImageView = view.findViewById(R.id.image_admin_product)
        val textName: TextView = view.findViewById(R.id.text_admin_product_name)
        val textPrice: TextView = view.findViewById(R.id.text_admin_product_price)
        val textCategory: TextView = view.findViewById(R.id.text_admin_product_category)
        val btnEdit: Button = view.findViewById(R.id.btn_edit_product)
        val btnDelete: Button = view.findViewById(R.id.btn_delete_product)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_admin_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]

        // Cargar imagen con Coil
        if (product.imageUrl.isNotEmpty()) {
            holder.imageProduct.load(product.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_product_placeholder)
                error(R.drawable.ic_product_placeholder)
            }
        } else {
            holder.imageProduct.setImageResource(R.drawable.ic_product_placeholder)
        }

        holder.textName.text = product.name
        holder.textPrice.text = "$${String.format("%.2f", product.price)}"
        holder.textCategory.text = product.category

        holder.btnEdit.setOnClickListener { onEditClick(product) }
        holder.btnDelete.setOnClickListener { onDeleteClick(product) }
    }

    override fun getItemCount() = products.size

    fun updateData(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}
