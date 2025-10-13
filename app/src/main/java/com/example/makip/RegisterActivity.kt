package com.example.makip

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {

    // Declaración de vistas
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var createAccountButton: MaterialButton
    private lateinit var logInText: TextView
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicializar vistas
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
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        // Aquí iría tu lógica de registro real (validación, llamada a API, etc.)
        Toast.makeText(this, "Registrando cuenta para $email...", Toast.LENGTH_LONG).show()

        // Tras el registro exitoso, puedes navegar a la pantalla principal o al login.
        // val intent = Intent(this, MainActivity::class.java)
        // startActivity(intent)
        // finish()
    }

    /**
     * Navega a la pantalla de Inicio de Sesión y finaliza esta actividad.
     */
    private fun navigateToLogin() {
        // Al regresar, no necesitamos volver a crear la actividad de Login,
        // pero usar un Intent garantiza que no haya problemas.
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}