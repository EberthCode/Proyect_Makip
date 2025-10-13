package com.example.makip

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    // Declaración de vistas
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var signUpText: TextView

    // DECLARACIÓN DEL GESTOR DE AUTENTICACIÓN
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // INICIALIZAMOS EL GESTOR DE AUTENTICACIÓN
        authManager = AuthManager(this)

        // 1. Inicializar las vistas
        emailEditText = findViewById(R.id.edit_text_email)
        passwordEditText = findViewById(R.id.edit_text_password)
        // Usamos MaterialButton si tu layout lo usa, si no, usa Button
        loginButton = findViewById(R.id.button_login) as MaterialButton
        signUpText = findViewById(R.id.text_sign_up)

        // 2. Configurar Listener para el botón de Log In
        loginButton.setOnClickListener {
            handleLogin()
        }

        // 3. Configurar Listener para el texto de Sign Up
        signUpText.setOnClickListener {
            // Navegar a la pantalla de registro
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish() // Cierra la pantalla de Login al ir a Registro
        }
    }

    /**
     * Lógica para manejar el inicio de sesión.
     */
    private fun handleLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa email y contraseña.", Toast.LENGTH_SHORT).show()
            return
        }

        // --------------------------------------------------------
        // LÓGICA DE LOGIN USANDO AuthManager (Simulación de BD)
        // --------------------------------------------------------

        if (authManager.validateCredentials(email, password)) {
            // Credenciales válidas
            authManager.setLoggedIn(true)
            val name = authManager.getUserName()

            Toast.makeText(this, "Inicio de sesión exitoso. Bienvenido, $name!", Toast.LENGTH_LONG).show()

            // Redirigir al Catálogo (MainActivity)
            val intent = Intent(this, CatalogoActivity::class.java)
            startActivity(intent)
            finish() // Cierra la actividad de Login
        } else {
            // Credenciales inválidas
            Toast.makeText(this, "Credenciales incorrectas o usuario no registrado.", Toast.LENGTH_LONG).show()
        }
    }
}