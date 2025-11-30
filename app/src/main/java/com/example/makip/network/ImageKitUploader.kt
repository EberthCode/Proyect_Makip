package com.example.makip.network

import com.example.makip.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.util.Base64

object ImageKitUploader {

    private val client = OkHttpClient()
    
    // Usamos la clave desde BuildConfig
    private val PRIVATE_KEY = BuildConfig.IMAGEKIT_PRIVATE_KEY
    private val ENDPOINT_URL = "https://upload.imagekit.io/api/v1/files/upload"

    suspend fun subirFoto(rutaLocal: String, carpeta: String = "makip_pedidos"): String? {
        return withContext(Dispatchers.IO) {
            val file = File(rutaLocal)
            if (!file.exists()) return@withContext null

            try {
                // Autenticación básica en Base64
                val authString = "$PRIVATE_KEY:"
                val encodedAuth = Base64.getEncoder().encodeToString(authString.toByteArray())

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "file",
                        file.name,
                        file.asRequestBody("image/*".toMediaTypeOrNull())
                    )
                    .addFormDataPart("fileName", file.name)
                    .addFormDataPart("folder", carpeta) // Opcional: carpeta en ImageKit
                    .build()

                val request = Request.Builder()
                    .url(ENDPOINT_URL)
                    .addHeader("Authorization", "Basic $encodedAuth")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    val json = JSONObject(responseBody)
                    // Retorna la URL pública
                    return@withContext json.optString("url")
                } else {
                    println("Error ImageKit: ${response.message} - $responseBody")
                    return@withContext null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext null
            }
        }
    }
}
