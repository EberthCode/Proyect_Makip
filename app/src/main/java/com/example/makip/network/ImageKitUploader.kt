package com.example.makip.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.util.*

object ImageKitUploader {

    private const val IMAGEKIT_ENDPOINT = "https://upload.imagekit.io/api/v1/files/upload"
    private const val PUBLIC_KEY = "tu_public_key_aqui" // Reemplaza con tu clave pública de ImageKit
    private const val PRIVATE_KEY = "tu_private_key_aqui" // Reemplaza con tu clave privada de ImageKit
    private const val TAG = "ImageKitUploader"

    private val client = OkHttpClient()

    suspend fun subirFoto(filePath: String, folder: String): String? = withContext(Dispatchers.IO) {
        try {
            val file = File(filePath)
            if (!file.exists()) {
                Log.e(TAG, "El archivo no existe: $filePath")
                return@withContext null
            }

            // Crear el request body para multipart
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    file.name,
                    file.asRequestBody("image/*".toMediaTypeOrNull())
                )
                .addFormDataPart("fileName", "${UUID.randomUUID()}_${file.name}")
                .addFormDataPart("folder", folder)
                .addFormDataPart("publicKey", PUBLIC_KEY)
                .build()

            // Crear credentials para autenticación básica
            val credentials = Credentials.basic(PRIVATE_KEY, "")

            val request = Request.Builder()
                .url(IMAGEKIT_ENDPOINT)
                .header("Authorization", credentials)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                responseBody?.let { body ->
                    val jsonResponse = JSONObject(body)
                    val imageUrl = jsonResponse.getString("url")
                    Log.d(TAG, "Imagen subida exitosamente: $imageUrl")
                    return@withContext imageUrl
                }
            } else {
                Log.e(TAG, "Error en la respuesta: ${response.code}")
                val errorBody = response.body?.string()
                Log.e(TAG, "Error body: $errorBody")
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error al subir imagen", e)
        }

        return@withContext null
    }

    // Método alternativo que simula la subida (para desarrollo/testing)
    suspend fun subirFotoMock(filePath: String, folder: String): String? {
        // Simular delay de red
        kotlinx.coroutines.delay(2000)

        // Retornar una URL de ejemplo para testing
        return "https://example.com/images/${UUID.randomUUID()}.jpg"
    }
}
