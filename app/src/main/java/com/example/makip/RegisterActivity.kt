package com.example.makip

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {

    // Declaración de vistas
    private lateinit var nameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var createAccountButton: MaterialButton
    private lateinit var logInText: TextView
    private lateinit var backButton: ImageButton

    // DECLARACIÓN DEL GESTOR DE AUTENTICACIÓN
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        // Configurar barra de estado transparente con iconos NEGROS
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = true
        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())


        // SIMPLE: El sistema reserva automáticamente el espacio para las barras
        WindowCompat.setDecorFitsSystemWindows(window, true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // INICIALIZAMOS EL GESTOR DE AUTENTICACIÓN
        authManager = AuthManager(this)

        // Inicializar vistas
        nameEditText = findViewById(R.id.edit_text_name)
        emailEditText = findViewById(R.id.edit_text_email)
        passwordEditText = findViewById(R.id.edit_text_password)
        createAccountButton = findViewById(R.id.button_create_account)
        logInText = findViewById(R.id.text_log_in)
        backButton = findViewById(R.id.button_back)

        // Configurar Listener para el botón de Registro
        createAccountButton.setOnClickListener {
            handleRegistration()
        }

        // Configurar Listener para el enlace "Log In" (navegación a LoginActivity)
        logInText.setOnClickListener {
            navigateToLogin()
        }

        // Configurar Listener para la flecha de atrás (navegación a LoginActivity)
        backButton.setOnClickListener {
            navigateToLogin()
        }

        // Opcional: Configurar listeners para Términos y Privacidad (ejemplo)
        findViewById<TextView>(R.id.text_terms).setOnClickListener {
            Toast.makeText(this, "Abriendo Términos de Servicio...", Toast.LENGTH_SHORT).show()
        }
        findViewById<TextView>(R.id.text_privacy).setOnClickListener {
            Toast.makeText(this, "Abriendo Política de Privacidad...", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Lógica para manejar el registro de usuario.
     */
    private fun handleRegistration() {
        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        // Validar que TODOS los campos estén completos
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        // --------------------------------------------------------
        // LÓGICA DE REGISTRO USANDO AuthManager
        // --------------------------------------------------------

        // 1. Simular guardar el usuario localmente
        // En una BD real, 'password' se hashearía antes de guardar.
        authManager.registerUser(name, email, password)

        // 2. Marcar la sesión como iniciada automáticamente tras el registro
        authManager.setLoggedIn(true)

        Toast.makeText(this, "Registro exitoso. ¡Bienvenido, $name!", Toast.LENGTH_LONG).show()

        // Tras el registro exitoso, navegamos al Catálogo (MainActivity)
        val intent = Intent(this, CatalogoActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Navega a la pantalla de Inicio de Sesión y finaliza esta actividad.
     */
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = true
        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
    }

}