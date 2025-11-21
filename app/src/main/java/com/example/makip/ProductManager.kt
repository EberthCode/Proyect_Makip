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
                                "Camiseta Básica",
                                25.00,
                                "Ropa",
                                "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400"
                        ),
                        Product(
                                2,
                                "Sudadera Premium",
                                45.00,
                                "Ropa",
                                "https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=400"
                        ),
                        Product(
                                3,
                                "Gorra Deportiva",
                                20.00,
                                "Accesorios",
                                "https://images.unsplash.com/photo-1588850561407-ed78c282e89b?w=400"
                        ),
                        Product(
                                4,
                                "Taza Térmica",
                                15.00,
                                "Otros",
                                "https://images.unsplash.com/photo-1514228742587-6b1558fcca3d?w=400"
                        ),
                        Product(
                                5,
                                "Bolso de Mano",
                                30.00,
                                "Accesorios",
                                "https://images.unsplash.com/photo-1590874103328-eac38a683ce7?w=400"
                        ),
                        Product(
                                6,
                                "Pegatina Pack",
                                5.00,
                                "Otros",
                                "https://images.unsplash.com/photo-1611532736570-dea7ee43db88?w=400"
                        ),
                        Product(
                                7,
                                "Llavero Metal",
                                8.00,
                                "Otros",
                                "https://images.unsplash.com/photo-1563013544-824ae1b704d3?w=400"
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
