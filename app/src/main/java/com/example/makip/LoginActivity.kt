// LoginActivity.kt
package com.example.makip // Asegúrate de cambiar esto a tu package real
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
// Importa las vistas que usarás, aunque en este ejemplo no haremos mucho con ellas
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

        // 3. Ejemplo: Configurar un listener para el texto de Sign Up
        signUpText.setOnClickListener {
            // Aquí iría el código para navegar a la pantalla de registro
            // val intent = Intent(this, SignUpActivity::class.java)
            // startActivity(intent)
        }
    }
}