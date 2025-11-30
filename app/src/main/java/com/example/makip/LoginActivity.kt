package com.example.makip

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var signUpText: TextView
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // El tema y fitsSystemWindows en XML manejan las barras automáticamente
        setContentView(R.layout.activity_login)

        authManager = AuthManager(this)

        emailEditText = findViewById(R.id.edit_text_email)
        passwordEditText = findViewById(R.id.edit_text_password)
        loginButton = findViewById(R.id.button_login)
        signUpText = findViewById(R.id.text_sign_up)

        loginButton.setOnClickListener { handleLogin() }

        signUpText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun handleLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa email y contraseña.", Toast.LENGTH_SHORT).show()
            return
        }

        // Verificar si es admin
        if (authManager.validateAdminCredentials(email, password)) {
            authManager.setLoggedIn(true)
            authManager.setUserRole(AuthManager.ROLE_ADMIN)
            Toast.makeText(this, "Bienvenido, Administrador!", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, AdminActivity::class.java))
            finish()
            return
        }

        // Verificar usuario normal
        if (authManager.validateCredentials(email, password)) {
            authManager.setLoggedIn(true)
            authManager.setUserRole(AuthManager.ROLE_USER)
            val name = authManager.getUserName()
            Toast.makeText(this, "Inicio de sesión exitoso. Bienvenido, $name!", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, CatalogoActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Credenciales incorrectas o usuario no registrado.", Toast.LENGTH_LONG).show()
        }
    }
}
