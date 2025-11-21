package com.example.makip

import android.content.Context
import android.content.SharedPreferences

class AuthManager(context: Context) {

    // Nombre del archivo de SharedPreferences
    private val PREFS_NAME = "AppAuthPrefs"
    private val sharedPref: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Claves para el estado de la sesión
    private val KEY_IS_LOGGED_IN = "is_logged_in"
    private val KEY_USER_ROLE = "user_role"

    // Constantes de roles
    companion object {
        const val ROLE_USER = "user"
        const val ROLE_ADMIN = "admin"
        const val ADMIN_EMAIL = "admin@makip.com"
        const val ADMIN_PASSWORD = "admin123"
    }

    // Claves para simular el registro de usuario (¡DEMO!)
    private val KEY_NAME = "user_name"
    private val KEY_EMAIL = "user_email"
    private val KEY_PASSWORD = "user_password" // Simulamos guardar la password sin hash

    // ----------------------------------------------------------------------
    // Gestión del Estado de la Sesión
    // ----------------------------------------------------------------------

    fun setLoggedIn(isLoggedIn: Boolean) {
        sharedPref.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPref.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // ----------------------------------------------------------------------
    // Simulación de Base de Datos de Autenticación
    // ----------------------------------------------------------------------

    /** SIMULACIÓN DE REGISTRO Guarda el nombre, email y contraseña del usuario. */
    fun registerUser(name: String, email: String, passwordHash: String) {
        sharedPref.edit().apply {
            putString(KEY_NAME, name)
            putString(KEY_EMAIL, email)
            putString(KEY_PASSWORD, passwordHash)
            apply()
        }
    }

    /** SIMULACIÓN DE LOGIN Verifica si las credenciales coinciden con el usuario guardado. */
    fun validateCredentials(email: String, passwordHash: String): Boolean {
        // Obtenemos las credenciales guardadas
        val savedEmail = sharedPref.getString(KEY_EMAIL, null)
        val savedPasswordHash = sharedPref.getString(KEY_PASSWORD, null)

        // Comprobamos si coinciden
        return email == savedEmail && passwordHash == savedPasswordHash && savedEmail != null
    }

    /** Función para obtener el nombre del usuario registrado */
    fun getUserName(): String {
        return sharedPref.getString(KEY_NAME, "Usuario") ?: "Usuario"
    }

    /** Función para cerrar sesión */
    fun logout() {
        setLoggedIn(false)
        sharedPref.edit().remove(KEY_USER_ROLE).apply()
        // Opcional: Si quieres borrar las credenciales de demo:
        // sharedPref.edit().remove(KEY_EMAIL).remove(KEY_PASSWORD).remove(KEY_NAME).apply()
    }

    // ----------------------------------------------------------------------
    // Gestión de Roles de Usuario
    // ----------------------------------------------------------------------

    /** Verifica si el email es de administrador. */
    fun isAdmin(email: String): Boolean {
        return email == ADMIN_EMAIL
    }

    /** Establece el rol del usuario. */
    fun setUserRole(role: String) {
        sharedPref.edit().putString(KEY_USER_ROLE, role).apply()
    }

    /** Obtiene el rol del usuario actual. */
    fun getUserRole(): String {
        return sharedPref.getString(KEY_USER_ROLE, ROLE_USER) ?: ROLE_USER
    }

    /** Verifica si el usuario actual es administrador. */
    fun isCurrentUserAdmin(): Boolean {
        return getUserRole() == ROLE_ADMIN
    }

    /** Valida credenciales de administrador. */
    fun validateAdminCredentials(email: String, password: String): Boolean {
        return email == ADMIN_EMAIL && password == ADMIN_PASSWORD
    }
}
