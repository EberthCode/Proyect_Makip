package com.example.makip

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PerfilActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authManager = AuthManager(this)

        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_perfil)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar la UI
        displayUserName()
        setupProfileOptions()

        // Configurar el botón de Cerrar Sesión
        findViewById<Button>(R.id.btn_logout).setOnClickListener {
            logoutUser()
        }
    }

    /** Muestra el nombre del usuario logeado en el header. */
    private fun displayUserName() {
        // Muestra el nombre. Si no hay nombre guardado, AuthManager devuelve "Usuario"
        val userName = authManager.getUserName()
        findViewById<TextView>(R.id.text_user_name_detail).text = "Usuario: $userName"
    }

    /** Configura el texto y los íconos de las opciones del perfil. */
    private fun setupProfileOptions() {

        // 1. OPCIÓN: EDITAR PERFIL
        configureOption(
            R.id.option_edit_profile,
            "Editar Perfil",
            R.drawable.ic_person_white
        )

        // 2. OPCIÓN: CAMBIAR CONTRASEÑA
        configureOption(
            R.id.option_change_password,
            "Cambiar Contraseña",
            R.drawable.ic_lock_white
        )

        // 3. OPCIÓN: NOTIFICACIONES
        configureOption(
            R.id.option_notifications,
            "Notificaciones",
            R.drawable.ic_notifications_white
        )

        // 4. OPCIÓN: SOPORTE
        configureOption(
            R.id.option_support,
            "Soporte y Ayuda",
            R.drawable.ic_help_white
        )
    }

    /** Función auxiliar para configurar el texto y el ícono de una opción de perfil. */
    private fun configureOption(includeId: Int, title: String, iconResId: Int) {
        val optionView = findViewById<View>(includeId)

        // Establece el texto del título y el ícono
        optionView.findViewById<TextView>(R.id.option_title).text = title
        optionView.findViewById<ImageView>(R.id.option_icon).setImageResource(iconResId)

        // Agrega listener de clic
        optionView.setOnClickListener {
            // Aquí iría la lógica de navegación (e.g., a una nueva actividad)
        }
    }

    /** * Cierra la sesión llamando a authManager.logout() y redirige al Login.
     * Ahora usa la función logout() que establece isLoggedIn a false.
     */
    private fun logoutUser() {

        // 1. ACCIÓN CLAVE: Borrar el estado de sesión (is_logged_in = false)
        authManager.logout()

        // 2. Redirigir a la actividad de Login
        val intent = Intent(this, LoginActivity::class.java)
        // Limpia la pila de actividades para que el usuario no pueda volver con el botón "Atrás"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}