package com.example.clinicadiseo.utils

import android.util.Base64
import org.json.JSONObject

fun decodeJwt(token: String): Map<String, String> {
    return try {
        val parts = token.split(".")
        val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
        val json = JSONObject(payload)
        val data = mutableMapOf<String, String>()
        json.keys().forEach { key ->
            data[key] = json.getString(key).toString()
        }
        data
    } catch (e: Exception) {
        emptyMap()
    }
}
