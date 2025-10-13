package com.example.makip

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CatalogoActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager
    private lateinit var searchView: SearchView // Declaramos la SearchView
    private val SEARCH_HINT_COLOR = Color.parseColor("#AAAAAA") // Gris claro para el hint

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authManager = AuthManager(this)

        enableEdgeToEdge()
        setContentView(R.layout.activity_catalogo)

        // Asumiendo que has corregido el ID del layout principal a 'main_layout'
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        searchView = findViewById(R.id.search_bar) // Inicializamos la vista

        displayWelcomeMessage()

        // LLAMADA CLAVE: Cambiar el color del hint después de que la vista se inicialice
        setSearchViewHintColor()
    }

    /**
     * Establece el color de la pista (hint) del SearchView a gris claro para visibilidad.
     */
    private fun setSearchViewHintColor() {
        try {
            // Obtener el TextView que contiene la pista (Hint) dentro del SearchView
            // El ID del campo de búsqueda de texto es una constante interna de Android.
            val searchTextViewId = searchView.context.resources.getIdentifier(
                "android:id/search_src_text",
                null,
                null
            )

            val searchTextView: TextView? = searchView.findViewById(searchTextViewId)

            if (searchTextView != null) {
                // Cambia el color del texto de la pista
                searchTextView.setHintTextColor(SEARCH_HINT_COLOR)
                // Opcional: Cambia el color del texto que escribe el usuario
                searchTextView.setTextColor(Color.WHITE)
            }
        } catch (e: Exception) {
            // Manejar excepciones si no se encuentra el ID interno (raro pero posible)
            e.printStackTrace()
        }
    }

    private fun displayWelcomeMessage() {
        val welcomeTextView: TextView = findViewById(R.id.text_welcome_message)
        val userName = authManager.getUserName()
        welcomeTextView.text = "Hola, $userName"
    }
}