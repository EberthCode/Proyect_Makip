package com.example.makip

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class AdminActivity : AppCompatActivity() {


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
        setContentView(R.layout.activity_admin)
        
        authManager = AuthManager(this)
        setupToolbar()
        setupBottomNavigation()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_admin, AdminDashboardFragment())
                .commit()
        }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_admin)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Panel Administrador"
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_nav_admin)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_admin_dashboard -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_admin, AdminDashboardFragment())
                        .commit()
                    supportActionBar?.title = "Dashboard"
                    true
                }
                R.id.nav_admin_inventory -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_admin, AdminInventoryFragment())
                        .commit()
                    supportActionBar?.title = "Inventario"
                    true
                }
                R.id.nav_admin_orders -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_admin, AdminOrdersFragment())
                        .commit()
                    supportActionBar?.title = "Pedidos"
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_admin, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        authManager.logout()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
