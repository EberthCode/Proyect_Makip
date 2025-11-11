package com.example.makip

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class CatalogoActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager
    private lateinit var cartViewModel: CartViewModel
    private lateinit var productAdapter: ProductAdapter
    private lateinit var searchView: SearchView

    // Color para el hint del SearchView
    private val SEARCH_HINT_COLOR = Color.parseColor("#AAAAAA")
    private val TEXT_COLOR_WHITE = Color.WHITE

    // Datos mock actualizados
    private val mockProducts = listOf(
        Product(1, "Camiseta", 25.00, "Ropa", R.drawable.ic_tshirt),
        Product(2, "Sudadera", 45.00, "Ropa", R.drawable.ic_hoodie),
        Product(3, "Gorra", 20.00, "Accesorios", R.drawable.ic_cap),
        Product(4, "Taza", 15.00, "Otros", R.drawable.ic_mug),
        Product(5, "Bolso", 30.00, "Accesorios", R.drawable.ic_bag),
        Product(6, "Pegatina", 5.00, "Otros", R.drawable.ic_sticker),
        Product(7, "Llavero", 8.00, "Otros", R.drawable.ic_keychain)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authManager = AuthManager(this)
        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]

        enableEdgeToEdge()
        setContentView(R.layout.activity_catalogo)

        // Configuración de Insets (borde a borde)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        searchView = findViewById(R.id.search_bar)

        // Lógica de UI
        displayWelcomeMessage()
        setupRecyclerView() // REEMPLAZADO: Ahora usa el ProductAdapter real
        setupSearchView()
        setupCategoryChips()
        setupBottomNavigation()
        setupCartIcon()

        // Solución de color para el hint de búsqueda (ejecutar después de la inflación)
        searchView.post {
            setSearchViewHintColor()
        }
    }

    // --- NUEVAS FUNCIONALIDADES DEL CATÁLOGO ---

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_catalog)
        productAdapter = ProductAdapter(mockProducts) { product ->
            // Añadir producto al carrito
            cartViewModel.addProductToCart(product)
            Toast.makeText(this, "${product.name} añadida al carrito", Toast.LENGTH_SHORT).show()
        }

        recyclerView.apply {
            adapter = productAdapter
            layoutManager = GridLayoutManager(this@CatalogoActivity, 2)
        }
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                filterProducts(newText ?: "")
                return true
            }
        })
    }

    private fun setupCategoryChips() {
        val chipTodos = findViewById<com.google.android.material.chip.Chip>(R.id.chip_todos)
        val chipRopa = findViewById<com.google.android.material.chip.Chip>(R.id.chip_ropa)
        val chipAccesorios = findViewById<com.google.android.material.chip.Chip>(R.id.chip_accesorios)
        val chipOtros = findViewById<com.google.android.material.chip.Chip>(R.id.chip_otros)

        // Configurar chips para selección única
        val chipGroup = findViewById<com.google.android.material.chip.ChipGroup>(R.id.chip_group_categories)

        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            when {
                checkedIds.contains(R.id.chip_todos) -> filterByCategory("Todos")
                checkedIds.contains(R.id.chip_ropa) -> filterByCategory("Ropa")
                checkedIds.contains(R.id.chip_accesorios) -> filterByCategory("Accesorios")
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
        val filtered = if (query.isEmpty()) {
            mockProducts
        } else {
            mockProducts.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }
        productAdapter.updateData(filtered)
    }

    private fun filterByCategory(category: String) {
        val filtered = if (category == "Todos") {
            mockProducts
        } else {
            mockProducts.filter { it.category == category }
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
                    // TODO: Implementar actividad de pedidos
                    Toast.makeText(this, "Próximamente: Pedidos", Toast.LENGTH_SHORT).show()
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
            val searchTextViewId = resources.getIdentifier(
                "search_src_text",
                "id",
                packageName
            )

            val searchTextView: TextView? = searchView.findViewById(searchTextViewId)

            if (searchTextView != null) {
                searchTextView.setHintTextColor(SEARCH_HINT_COLOR)
                searchTextView.setTextColor(TEXT_COLOR_WHITE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Establece el mensaje de bienvenida obteniendo el nombre del usuario de AuthManager.
     */
    private fun displayWelcomeMessage() {
        val welcomeTextView: TextView = findViewById(R.id.text_welcome_message)

        // Uso de la función real de AuthManager
        val userName = authManager.getUserName()

        welcomeTextView.text = "Hola, $userName"
    }
}
