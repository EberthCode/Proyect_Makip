package com.example.makip

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ProductManager {

    private const val PREFS_NAME = "ProductPrefs"
    private const val KEY_PRODUCTS = "products"

    private val products = mutableListOf<Product>()
    private val listeners = mutableListOf<() -> Unit>()
    private lateinit var sharedPrefs: SharedPreferences
    private val gson = Gson()

    /** Inicializa el ProductManager. Debe llamarse en Application o primera Activity. */
    fun initialize(context: Context) {
        sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadProducts()
    }

    /** Carga productos desde SharedPreferences o inicializa con productos mock. */
    private fun loadProducts() {
        val json = sharedPrefs.getString(KEY_PRODUCTS, null)
        if (json != null) {
            // Cargar productos guardados
            val type = object : TypeToken<List<Product>>() {}.type
            val savedProducts: List<Product> = gson.fromJson(json, type)
            products.clear()
            products.addAll(savedProducts)
        } else {
            // Inicializar con productos mock por defecto
            initializeMockProducts()
            saveProducts()
        }
    }

    /** Productos mock iniciales con URLs de imágenes. */
    private fun initializeMockProducts() {
        products.clear()
        products.addAll(
                listOf(
                        Product(
                                1,
                                "Taza Blanca Personalizada",
                                15.00,
                                "Tazas",
                                "https://images.unsplash.com/photo-1514228742587-6b1558fcca3d?w=400"
                        ),
                        Product(
                                2,
                                "Taza Mágica",
                                20.00,
                                "Tazas",
                                "https://images.unsplash.com/photo-1577937927133-66ef06acdf18?w=400"
                        ),
                        Product(
                                3,
                                "Polo Algodón Estampado",
                                35.00,
                                "Polos",
                                "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400"
                        ),
                        Product(
                                4,
                                "Camiseta Deportiva",
                                30.00,
                                "Polos",
                                "https://images.unsplash.com/photo-1581655353564-df123a1eb820?w=400"
                        ),
                        Product(
                                5,
                                "Agenda Ejecutiva 2024",
                                25.00,
                                "Agendas",
                                "https://images.unsplash.com/photo-1544816155-12df9643f363?w=400"
                        ),
                        Product(
                                6,
                                "Cuaderno Personalizado",
                                18.00,
                                "Agendas",
                                "https://images.unsplash.com/photo-1586075010923-2dd4570fb338?w=400"
                        ),
                        Product(
                                7,
                                "Tazón Grande",
                                22.00,
                                "Tazones",
                                "https://images.unsplash.com/photo-1577937927133-66ef06acdf18?w=400"
                        ),
                        Product(
                                8,
                                "Llavero Acrílico",
                                8.00,
                                "Otros",
                                "https://images.unsplash.com/photo-1563013544-824ae1b704d3?w=400"
                        ),
                        Product(
                                9,
                                "Gorra Bordada",
                                25.00,
                                "Otros",
                                "https://images.unsplash.com/photo-1588850561407-ed78c282e89b?w=400"
                        )
                )
        )
    }

    /** Guarda productos en SharedPreferences. */
    private fun saveProducts() {
        val json = gson.toJson(products)
        sharedPrefs.edit().putString(KEY_PRODUCTS, json).apply()
    }

    /** Obtiene todos los productos. */
    fun getProducts(): List<Product> = products.toList()

    /** Agrega un nuevo producto. */
    fun addProduct(product: Product) {
        products.add(product)
        saveProducts()
        notifyListeners()
    }

    /** Actualiza un producto existente. */
    fun updateProduct(updatedProduct: Product) {
        val index = products.indexOfFirst { it.id == updatedProduct.id }
        if (index != -1) {
            products[index] = updatedProduct
            saveProducts()
            notifyListeners()
        }
    }

    /** Elimina un producto. */
    fun deleteProduct(productId: Int) {
        products.removeAll { it.id == productId }
        saveProducts()
        notifyListeners()
    }

    /** Obtiene un producto por ID. */
    fun getProductById(id: Int): Product? {
        return products.find { it.id == id }
    }

    /** Genera un nuevo ID único para productos. */
    fun generateNewId(): Int {
        return (products.maxOfOrNull { it.id } ?: 0) + 1
    }

    /** Agrega un listener para notificaciones de cambios. */
    fun addListener(listener: () -> Unit) {
        listeners.add(listener)
    }

    /** Elimina un listener. */
    fun removeListener(listener: () -> Unit) {
        listeners.remove(listener)
    }

    /** Notifica a los listeners de cambios. */
    private fun notifyListeners() {
        listeners.forEach { it() }
    }

    /** Resetea los productos a los valores mock por defecto. */
    fun resetToDefaults() {
        initializeMockProducts()
        saveProducts()
        notifyListeners()
    }
}
