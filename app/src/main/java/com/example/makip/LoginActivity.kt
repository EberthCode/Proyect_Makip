// LoginActivity.kt
package com.example.makip
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.Button
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    // Declaramos las vistas (opcional, pero buena práctica)
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var signUpText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Establece el layout que acabamos de crear
        setContentView(R.layout.activity_login)

        // 1. Inicializar las vistas (para poder usarlas después)
        emailEditText = findViewById(R.id.edit_text_email)
        passwordEditText = findViewById(R.id.edit_text_password)
        loginButton = findViewById(R.id.button_login)
        signUpText = findViewById(R.id.text_sign_up)

        // 2. Ejemplo: Configurar un listener para el botón de Log In
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Aquí iría tu lógica de autenticación
            // Log.d("LoginActivity", "Email: $email, Password: $password")
        }

        // 3. Configurar un listener para el texto de Sign Up
        signUpText.setOnClickListener {
            // Código para navegar a la pantalla de registro
            val intent = Intent(this, RegisterActivity::class.java) // ¡Cambiamos aquí!
            startActivity(intent)
        }
    }
}