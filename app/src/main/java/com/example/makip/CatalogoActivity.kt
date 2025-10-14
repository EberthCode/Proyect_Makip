package com.example.makip

import android.content.Intent // IMPORTANTE: Importar Intent para iniciar la nueva actividad
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class CatalogoActivity : AppCompatActivity() {

    // DESCOMENTADO: Declaración de la clase AuthManager
    private lateinit var authManager: AuthManager

    private lateinit var searchView: SearchView
    // Color para el hint del SearchView
    private val SEARCH_HINT_COLOR = Color.parseColor("#AAAAAA")
    private val TEXT_COLOR_WHITE = Color.WHITE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // DESCOMENTADO: Inicialización de la clase AuthManager (usa el Context)
        authManager = AuthManager(this)

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
        // LLAMADA ACTIVADA: Ahora usa el nombre guardado por AuthManager
        displayWelcomeMessage()

        setupBottomNavigation() // Lógica de navegación con enlace a PerfilActivity
        setupPlaceholderCatalog()

        // Solución de color para el hint de búsqueda (ejecutar después de la inflación)
        searchView.post {
            setSearchViewHintColor()
        }
    }

    // --- LÓGICA DEL CATÁLOGO DE MAQUETAS ---

    /**
     * Configura el RecyclerView con un adaptador de prueba para mostrar maquetas de producto.
     */
    private fun setupPlaceholderCatalog() {
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_catalog)
        recyclerView.adapter = PlaceholderAdapter()
    }

    /**
     * Adaptador simple para inflar 10 vistas de item_product_card.xml (maquetas).
     */
    private class PlaceholderAdapter : RecyclerView.Adapter<PlaceholderViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceholderViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product_card, parent, false)
            return PlaceholderViewHolder(view)
        }

        override fun onBindViewHolder(holder: PlaceholderViewHolder, position: Int) {
            // No se requiere lógica de binding; la vista es un placeholder estático.
        }

        // Muestra 10 elementos de prueba
        override fun getItemCount() = 10
    }

    /**
     * ViewHolder simple para el adaptador.
     */
    private class PlaceholderViewHolder(view: View) : RecyclerView.ViewHolder(view)

    // --- LÓGICA DE NAVEGACIÓN Y BÚSQUEDA ---

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigation.isItemActiveIndicatorEnabled = false
        bottomNavigation.selectedItemId = R.id.nav_catalogo

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_catalogo -> {
                    true // Ya estamos aquí
                }
                R.id.nav_carrito -> true
                R.id.nav_pedidos -> true
                R.id.nav_perfil -> {
                    // CÓDIGO AÑADIDO: Enlace al PerfilActivity
                    startActivity(Intent(this, PerfilActivity::class.java))
                    // Opcional: Deshabilita la animación para transiciones limpias entre tabs
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