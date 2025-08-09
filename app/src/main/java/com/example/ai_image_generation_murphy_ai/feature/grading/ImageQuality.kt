package com.example.ai_image_generation_murphy_ai.feature.grading

import android.graphics.Bitmap
import kotlin.math.max

object ImageQuality {

    /** Fast Laplacian-variance sharpness score (bigger = sharper). */
    fun laplacianVariance(src: Bitmap): Double {
        val targetW = 320
        val scale = targetW.toDouble() / src.width.toDouble()
        val w = targetW
        val h = (src.height * scale).toInt().coerceAtLeast(1)
        val small = Bitmap.createScaledBitmap(src, w, h, true)

        val px = IntArray(w * h)
        small.getPixels(px, 0, w, 0, 0, w, h)
        val gray = IntArray(px.size) { i ->
            val p = px[i]
            val r = (p shr 16) and 0xFF
            val g = (p shr 8) and 0xFF
            val b = p and 0xFF
            (0.299 * r + 0.587 * g + 0.114 * b).toInt()
        }

        // 3x3 Laplacian
        val k = intArrayOf(0,1,0, 1,-4,1, 0,1,0)
        val out = DoubleArray(gray.size)
        var idx = 0
        for (y in 1 until h-1) {
            for (x in 1 until w-1) {
                var acc = 0.0; var ki = 0
                for (dy in -1..1) {
                    val yy = y + dy
                    val base = yy * w
                    for (dx in -1..1) {
                        val xx = x + dx
                        acc += gray[base + xx] * k[ki++]
                    }
                }
                out[idx++] = acc
            }
        }
        val n = idx
        var mean = 0.0
        for (i in 0 until n) mean += out[i]
        mean /= max(1, n)
        var varSum = 0.0
        for (i in 0 until n) {
            val d = out[i] - mean
            varSum += d * d
        }
        val variance = varSum / max(1, n - 1)
        small.recycle()
        return variance
    }

    /** Ratio of near-white pixels (glare proxy). Lower is better. */
    fun glareRatio(src: Bitmap): Double {
        val w = 320
        val scale = w.toDouble() / src.width.toDouble()
        val h = (src.height * scale).toInt().coerceAtLeast(1)
        val small = Bitmap.createScaledBitmap(src, w, h, true)

        val px = IntArray(w * h)
        small.getPixels(px, 0, w, 0, 0, w, h)
        var bright = 0
        val total = px.size
        for (p in px) {
            val r = (p shr 16) and 0xFF
            val g = (p shr 8) and 0xFF
            val b = p and 0xFF
            val y = 0.2126 * r + 0.7152 * g + 0.0722 * b
            if (y >= 250.0) bright++
        }
        small.recycle()
        return bright.toDouble() / max(1, total)
    }
}
