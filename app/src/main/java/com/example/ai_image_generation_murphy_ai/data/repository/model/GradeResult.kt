package com.example.ai_image_generation_murphy_ai.data.repository.model

data class GradeResult(
    val cardId: String,
    val grade: String,                       // "PSA 8.5"
    val subscores: Map<String, Double>,      // centering/corners/edges/surface
    val centering: Map<String, String>,      // F:"58/42", B:"80/20"
    val qualifiers: List<String>,            // e.g., ["OC","ST"]
    val confidence: Double,                  // 0.10..0.98
    val flags: List<String>,                 // glare_high, blur_high, partial_crop
    val notes: List<String>                  // 1–3 short bullets
)
