package com.example.makip

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.makip.network.ImageKitUploader
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class AddProductActivity : AppCompatActivity() {

    private lateinit var editName: TextInputEditText
    private lateinit var editPrice: TextInputEditText
    private lateinit var btnSelectImage: Button
    private lateinit var ivProductPreview: ImageView
    private lateinit var progressUpload: ProgressBar
    private lateinit var tvImageStatus: TextView
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnSave: Button

    private var editMode = false
    private var productId = -1
    private var selectedImageUri: Uri? = null
    private var uploadedImageUrl: String? = null

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            showImagePreview(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        initViews()
        setupToolbar()
        setupCategorySpinner()
        setupImagePicker()
        loadProductData()
        setupSaveButton()
    }

    private fun initViews() {
        editName = findViewById(R.id.edit_product_name)
        editPrice = findViewById(R.id.edit_product_price)
        btnSelectImage = findViewById(R.id.btn_select_image)
        ivProductPreview = findViewById(R.id.iv_product_preview)
        progressUpload = findViewById(R.id.progress_upload)
        tvImageStatus = findViewById(R.id.tv_image_status)
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

                // Cargar imagen existente
                if (it.imageUrl.isNotEmpty()) {
                    uploadedImageUrl = it.imageUrl
                    ivProductPreview.load(it.imageUrl) {
                        crossfade(true)
                    }
                    ivProductPreview.visibility = View.VISIBLE
                    tvImageStatus.setText(R.string.image_loaded)
                    tvImageStatus.visibility = View.VISIBLE
                }

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

        // Si hay una imagen nueva seleccionada, subirla primero
        if (selectedImageUri != null) {
            uploadImageAndSave(name, price, category)
        } else if (uploadedImageUrl != null) {
            // Usar la URL existente
            saveProductWithImageUrl(name, price, category, uploadedImageUrl!!)
        } else {
            Toast.makeText(this, "Por favor selecciona una imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageAndSave(name: String, price: Double, category: String) {
        lifecycleScope.launch {
            try {
                // Mostrar progreso
                progressUpload.visibility = View.VISIBLE
                tvImageStatus.setText(R.string.uploading_image)
                tvImageStatus.visibility = View.VISIBLE
                btnSave.isEnabled = false

                // Copiar imagen al almacenamiento temporal
                val tempFile = createTempFileFromUri(selectedImageUri!!)

                if (tempFile == null) {
                    showError("Error al procesar la imagen")
                    return@launch
                }

                // Subir a ImageKit
                val imageUrl = ImageKitUploader.subirFoto(
                    tempFile.absolutePath,
                    "makip_productos"
                )

                // Limpiar archivo temporal
                tempFile.delete()

                if (imageUrl != null) {
                    tvImageStatus.setText(R.string.image_uploaded)
                    uploadedImageUrl = imageUrl
                    saveProductWithImageUrl(name, price, category, imageUrl)
                } else {
                    showError("Error al subir la imagen a ImageKit")
                }
            } catch (e: Exception) {
                showError("Error: ${e.message}")
            } finally {
                progressUpload.visibility = View.GONE
                btnSave.isEnabled = true
            }
        }
    }

    private fun saveProductWithImageUrl(name: String, price: Double, category: String, imageUrl: String) {
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

    private fun setupImagePicker() {
        btnSelectImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
    }

    private fun showImagePreview(uri: Uri) {
        ivProductPreview.load(uri) {
            crossfade(true)
        }
        ivProductPreview.visibility = View.VISIBLE
        tvImageStatus.setText(R.string.image_selected)
        tvImageStatus.visibility = View.VISIBLE
    }

    private fun createTempFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("upload_", ".jpg", cacheDir)
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            inputStream.close()
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun showError(message: String) {
        tvImageStatus.text = message
        tvImageStatus.visibility = View.VISIBLE
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
