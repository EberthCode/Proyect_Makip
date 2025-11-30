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
import java.io.IOException

object ImageKitUploader {

    private val client = OkHttpClient()

    // Se lee desde BuildConfig generado por gradle
    private val PRIVATE_KEY = BuildConfig.IMAGEKIT_PRIVATE_KEY
    private val ENDPOINT_URL = BuildConfig.IMAGEKIT_URL_ENDPOINT + "api/v1/files/upload"

    /**
     * Sube una imagen a ImageKit.
     * @param rutaLocal Ruta absoluta del archivo en el dispositivo.
     * @param carpeta Carpeta destino en ImageKit (ej: "makip_pedidos").
     * @return La URL pública de la imagen subida, o null si falla.
     */
    suspend fun subirFoto(rutaLocal: String, carpeta: String = "makip_pedidos"): String? {
        return withContext(Dispatchers.IO) {
            val file = File(rutaLocal)
            if (!file.exists()) return@withContext null

            // ImageKit requiere auth en Base64 de "private_key:"
            val authString = "$PRIVATE_KEY:"
            val authHeader = "Basic " + android.util.Base64.encodeToString(
                authString.toByteArray(),
                android.util.Base64.NO_WRAP
            )

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    file.name,
                    file.asRequestBody("image/*".toMediaTypeOrNull())
                )
                .addFormDataPart("fileName", file.name)
                .addFormDataPart("folder", carpeta)
                .addFormDataPart("useUniqueFileName", "true")
                .build()

            val request = Request.Builder()
                .url("https://upload.imagekit.io/api/v1/files/upload")
                .addHeader("Authorization", authHeader)
                .post(requestBody)
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        println("ImageKit Error: ${response.body?.string()}")
                        return@use null
                    }
                    
                    val responseBody = response.body?.string() ?: return@use null
                    val json = JSONObject(responseBody)
                    return@use json.optString("url", null)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }
}
