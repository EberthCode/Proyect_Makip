package com.example.makip

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class AddProductActivity : AppCompatActivity() {

    private lateinit var editName: TextInputEditText
    private lateinit var editPrice: TextInputEditText
    private lateinit var editImageUrl: TextInputEditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnSave: Button

    private var editMode = false
    private var productId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        initViews()
        setupToolbar()
        setupCategorySpinner()
        loadProductData()
        setupSaveButton()
    }

    private fun initViews() {
        editName = findViewById(R.id.edit_product_name)
        editPrice = findViewById(R.id.edit_product_price)
        editImageUrl = findViewById(R.id.edit_product_image_url)
        spinnerCategory = findViewById(R.id.spinner_category)
        btnSave = findViewById(R.id.btn_save_product)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_add_product)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        editMode = intent.getBooleanExtra("EDIT_MODE", false)
        supportActionBar?.title = if (editMode) "Editar Producto" else "Agregar Producto"

        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupCategorySpinner() {
        val categories = arrayOf("Ropa", "Accesorios", "Otros")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
    }

    private fun loadProductData() {
        if (editMode) {
            productId = intent.getIntExtra("PRODUCT_ID", -1)
            val product = ProductManager.getProductById(productId)

            product?.let {
                editName.setText(it.name)
                editPrice.setText(it.price.toString())
                editImageUrl.setText(it.imageUrl)

                // Seleccionar categoría en spinner
                val categoryPosition =
                        when (it.category) {
                            "Ropa" -> 0
                            "Accesorios" -> 1
                            "Otros" -> 2
                            else -> 0
                        }
                spinnerCategory.setSelection(categoryPosition)
            }
        }
    }

    private fun setupSaveButton() {
        btnSave.setOnClickListener { saveProduct() }
    }

    private fun saveProduct() {
        val name = editName.text.toString().trim()
        val priceText = editPrice.text.toString().trim()
        val imageUrl = editImageUrl.text.toString().trim()
        val category = spinnerCategory.selectedItem.toString()

        // Validación
        if (name.isEmpty()) {
            Toast.makeText(this, "Ingresa el nombre del producto", Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceText.toDoubleOrNull()
        if (price == null || price <= 0) {
            Toast.makeText(this, "Ingresa un precio válido", Toast.LENGTH_SHORT).show()
            return
        }

        if (editMode) {
            // Actualizar producto existente
            val updatedProduct = Product(productId, name, price, category, imageUrl)
            ProductManager.updateProduct(updatedProduct)
            Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show()
        } else {
            // Crear nuevo producto
            val newId = ProductManager.generateNewId()
            val newProduct = Product(newId, name, price, category, imageUrl)
            ProductManager.addProduct(newProduct)
            Toast.makeText(this, "Producto agregado", Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}
