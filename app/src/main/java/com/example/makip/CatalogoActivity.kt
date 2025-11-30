package com.example.makip

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.view.View
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class CatalogoActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager
    private lateinit var cartViewModel: CartViewModel
    private lateinit var productAdapter: ProductAdapter
    private lateinit var searchView: SearchView

    // Color para el hint del SearchView
    private val SEARCH_HINT_COLOR = Color.parseColor("#AAAAAA")
    private val TEXT_COLOR_WHITE = Color.WHITE
    
    // Variables para la seleccion de imagen
    private var selectedImageUris = mutableListOf<Uri>()
    private var currentImagePreviewContainer: android.widget.LinearLayout? = null
    private var imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris.isNotEmpty()) {
            val takenUris = uris.take(2) // Max 2 fotos
            selectedImageUris.clear()
            selectedImageUris.addAll(takenUris)
            updateImagePreviews()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authManager = AuthManager(this)
        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]

        // SIMPLE: El sistema reserva automáticamente el espacio para las barras
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContentView(R.layout.activity_catalogo)

        // Configurar barra de estado transparente con iconos NEGROS
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = true
        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())

        searchView = findViewById(R.id.search_bar)

        // Lógica de UI
        displayWelcomeMessage()
        setupRecyclerView()
        setupSearchView()
        setupCategoryChips()
        setupBottomNavigation()
        // Solución de color para el hint de búsqueda (ejecutar después de la inflación)

        // Solución de color para el hint de búsqueda
        searchView.post { setSearchViewHintColor() }
    }

    // --- NUEVAS FUNCIONALIDADES DEL CATÁLOGO ---

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_catalog)

        // Usar ProductManager en lugar de mockProducts
        productAdapter =
                ProductAdapter(ProductManager.getProducts()) { product ->
                    showProductCustomizationDialog(product)
                }

        recyclerView.apply {
            adapter = productAdapter
            layoutManager = GridLayoutManager(this@CatalogoActivity, 2)
        }
    }
    
    private fun showProductCustomizationDialog(product: Product) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_product_customization, null)
        dialog.setContentView(view)

        // Referencias a vistas
        val title = view.findViewById<TextView>(R.id.text_product_title)
        val price = view.findViewById<TextView>(R.id.text_product_price)
        val layoutImages = view.findViewById<View>(R.id.layout_upload_images)
        val layoutText = view.findViewById<TextInputLayout>(R.id.input_layout_text)
        val editText = view.findViewById<TextInputEditText>(R.id.edit_text_custom)
        val layoutSize = view.findViewById<TextInputLayout>(R.id.input_layout_size) // Using Spinner/Dropdown logic usually, but keeping simple for now or check layout
        val autoCompleteSize = view.findViewById<android.widget.AutoCompleteTextView>(R.id.auto_complete_size)
        val layoutColor = view.findViewById<TextInputLayout>(R.id.input_layout_color)
        val autoCompleteColor = view.findViewById<android.widget.AutoCompleteTextView>(R.id.auto_complete_color)
        val quantityInput = view.findViewById<TextInputEditText>(R.id.edit_quantity)
        val btnAddToCart = view.findViewById<MaterialButton>(R.id.btn_add_to_cart_dialog)
        val btnUpload = view.findViewById<MaterialButton>(R.id.btn_upload_photo)
        currentImagePreviewContainer = view.findViewById(R.id.layout_image_previews)

        title.text = product.name
        price.text = "$${product.price}"
        
        // Reset state
        selectedImageUris.clear()
        currentImagePreviewContainer?.removeAllViews()
        
        // Lógica de visibilidad según categoría/producto
        val category = product.category
        val nameLower = product.name.lowercase()

        // Default visibility: GONE
        layoutImages.visibility = View.GONE
        layoutText.visibility = View.GONE
        layoutSize.visibility = View.GONE
        layoutColor.visibility = View.GONE

        // Configurar campos según reglas
        when {
            // Tazas
            category == "Tazas" || nameLower.contains("taza") -> {
                layoutImages.visibility = View.VISIBLE // 1-2 fotos
                layoutText.visibility = View.VISIBLE
                layoutText.hint = "Texto/Dedicatoria (máx 50)"
                layoutColor.visibility = View.VISIBLE
                
                // Configurar adaptador para colores de taza
                val colors = arrayOf("Blanco", "Negro", "Interior Rojo", "Interior Azul")
                val adapter = android.widget.ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, colors)
                autoCompleteColor.setAdapter(adapter)
                autoCompleteColor.setText(colors[0], false) // Default
            }
            // Polos / Camisetas
            category == "Polos" || nameLower.contains("polo") || nameLower.contains("camiseta") -> {
                layoutImages.visibility = View.VISIBLE // 1 foto
                layoutText.visibility = View.VISIBLE
                layoutText.hint = "Texto corto (opcional)"
                layoutSize.visibility = View.VISIBLE
                layoutColor.visibility = View.VISIBLE
                
                val sizes = arrayOf("S", "M", "L", "XL", "XXL")
                val sizeAdapter = android.widget.ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, sizes)
                autoCompleteSize.setAdapter(sizeAdapter)
                autoCompleteSize.setText(sizes[1], false) // Default M

                // Colores genéricos de ropa
                val colors = arrayOf("Blanco", "Negro", "Gris", "Azul Marino")
                val colorAdapter = android.widget.ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, colors)
                autoCompleteColor.setAdapter(colorAdapter)
                 autoCompleteColor.setText(colors[0], false)
            }
            // Agendas / Cuadernos
            category == "Agendas" || nameLower.contains("agenda") || nameLower.contains("cuaderno") -> {
                layoutImages.visibility = View.VISIBLE // 1 foto portada
                layoutText.visibility = View.VISIBLE
                layoutText.hint = "Nombre o frase en portada"
                // Color solo si hay opciones, asumimos sí
                 layoutColor.visibility = View.VISIBLE
                 val colors = arrayOf("Negro", "Azul", "Rojo", "Marrón")
                 val colorAdapter = android.widget.ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, colors)
                 autoCompleteColor.setAdapter(colorAdapter)
                 autoCompleteColor.setText(colors[0], false)
            }
            // Tazones / Vasos
            category == "Tazones" || nameLower.contains("tazón") || nameLower.contains("vaso") -> {
                layoutImages.visibility = View.VISIBLE
                layoutText.visibility = View.VISIBLE
                layoutText.hint = "Texto corto"
            }
            // Otros
            else -> {
                 layoutImages.visibility = View.VISIBLE
                 layoutText.visibility = View.VISIBLE
                 layoutText.hint = "Texto corto"
            }
        }
        
        btnUpload.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        btnAddToCart.setOnClickListener {
            val qtyStr = quantityInput.text.toString()
            val qty = if (qtyStr.isNotEmpty()) qtyStr.toInt() else 1
            
            // Recopilar datos
            val size = if (layoutSize.visibility == View.VISIBLE) autoCompleteSize.text.toString() else null
            val color = if (layoutColor.visibility == View.VISIBLE) autoCompleteColor.text.toString() else null
            val text = if (layoutText.visibility == View.VISIBLE) editText.text.toString() else null
            
            // Validaciones básicas
            if (layoutText.visibility == View.VISIBLE && nameLower.contains("taza") && (text?.length ?: 0) > 50) {
                 layoutText.error = "Máximo 50 caracteres"
                 return@setOnClickListener
            }

            CartManager.addCartItem(
                product = product,
                quantity = qty,
                size = size,
                color = color,
                customText = text,
                customImageUris = selectedImageUris.map { it.toString() }
            )
            
            Toast.makeText(this, "${product.name} agregado al carrito", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }
    
    private fun updateImagePreviews() {
        currentImagePreviewContainer?.removeAllViews()
        selectedImageUris.forEach { uri ->
            val imageView = ImageView(this)
            val params = android.widget.LinearLayout.LayoutParams(150, 150)
            params.setMargins(0, 0, 16, 0)
            imageView.layoutParams = params
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setImageURI(uri)
            currentImagePreviewContainer?.addView(imageView)
        }
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean = false

                    override fun onQueryTextChange(newText: String?): Boolean {
                        filterProducts(newText ?: "")
                        return true
                    }
                }
        )
    }

    private fun setupCategoryChips() {
        val chipGroup =
                findViewById<com.google.android.material.chip.ChipGroup>(R.id.chip_group_categories)

        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            when {
                checkedIds.contains(R.id.chip_todos) -> filterByCategory("Todos")
                checkedIds.contains(R.id.chip_tazas) -> filterByCategory("Tazas")
                checkedIds.contains(R.id.chip_polos) -> filterByCategory("Polos")
                checkedIds.contains(R.id.chip_agendas) -> filterByCategory("Agendas")
                checkedIds.contains(R.id.chip_tazones) -> filterByCategory("Tazones")
                checkedIds.contains(R.id.chip_otros) -> filterByCategory("Otros")
                else -> filterByCategory("Todos") // Por defecto mostrar todos
            }
        }
    }

    private fun setupCartIcon() {
        findViewById<ImageView>(R.id.icon_cart).setOnClickListener {
            startActivity(Intent(this, CarritoActivity::class.java))
        }
    }

    private fun filterProducts(query: String) {
        val allProducts = ProductManager.getProducts()
        val filtered =
                if (query.isEmpty()) {
                    allProducts
                } else {
                    allProducts.filter { it.name.contains(query, ignoreCase = true) }
                }
        productAdapter.updateData(filtered)
    }

    private fun filterByCategory(category: String) {
        val allProducts = ProductManager.getProducts()
        val filtered =
                if (category == "Todos") {
                    allProducts
                } else {
                    allProducts.filter { it.category == category }
                }
        productAdapter.updateData(filtered)
    }

    // --- MÉTODOS EXISTENTES MANTENIDOS ---

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigation.isItemActiveIndicatorEnabled = false
        bottomNavigation.selectedItemId = R.id.nav_catalogo

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_catalogo -> {
                    true // Ya estamos aquí
                }
                R.id.nav_carrito -> {
                    startActivity(Intent(this, CarritoActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_pedidos -> {
                    // --- CAMBIO REALIZADO AQUÍ, MI REY ---
                    startActivity(Intent(this, TrackingActivity::class.java))
                    overridePendingTransition(0, 0) // Transición suave igual que los demás
                    true
                }
                R.id.nav_perfil -> {
                    startActivity(Intent(this, PerfilActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }

    private fun setSearchViewHintColor() {
        try {
            // Intenta obtener el TextView interno del SearchView
            val searchTextViewId = resources.getIdentifier("search_src_text", "id", packageName)

            val searchTextView: TextView? = searchView.findViewById(searchTextViewId)

            if (searchTextView != null) {
                searchTextView.setHintTextColor(SEARCH_HINT_COLOR)
                searchTextView.setTextColor(TEXT_COLOR_WHITE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** Establece el mensaje de bienvenida obteniendo el nombre del usuario de AuthManager. */
    private fun displayWelcomeMessage() {
        val welcomeTextView: TextView = findViewById(R.id.text_welcome_message)

        // Uso de la función real de AuthManager
        val userName = authManager.getUserName()

        welcomeTextView.text = "Hola, $userName"
    }

}