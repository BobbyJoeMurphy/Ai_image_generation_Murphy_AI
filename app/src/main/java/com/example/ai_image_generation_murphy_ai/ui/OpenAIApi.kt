package com.example.ai_image_generation_murphy_ai.ui

import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

object OpenAIApi {

    private const val API_KEY = "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" // 🔐 Replace this with your actual working key
    private const val API_URL = "https://api.openai.com/v1/chat/completions"

    fun gradeCardFromImage(base64Image: String, prompt: String): String {
        val client = OkHttpClient()

        val jsonBody = JSONObject().apply {
            put("model", "gpt-4o")
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", JSONArray().apply {
                        put(JSONObject().apply {
                            put("type", "text")
                            put("text", prompt)
                        })
                        put(JSONObject().apply {
                            put("type", "image_url")
                            put("image_url", JSONObject().apply {
                                put("url", "data:image/jpeg;base64,$base64Image")
                            })
                        })
                    })
                })
            })
            put("max_tokens", 500)
        }

        val body = jsonBody.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(API_URL)
            .addHeader("Authorization", "Bearer $API_KEY")
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string() ?: return "No response body"
            if (!response.isSuccessful) {
                Log.e("OpenAIApi", "Error ${response.code}: $responseBody")
                return "Error ${response.code}: $responseBody"
            }

            return try {
                val json = JSONObject(responseBody)
                json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
            } catch (e: Exception) {
                Log.e("OpenAIApi", "Parsing error: ${e.localizedMessage}")
                "Error parsing response: ${e.localizedMessage}"
            }
        }
    }
}
