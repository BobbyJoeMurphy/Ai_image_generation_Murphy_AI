package com.example.ai_image_generation_murphy_ai.data.repository.cv

import android.graphics.Bitmap
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

data class Centering(val lrPct: Pair<Int,Int>, val tbPct: Pair<Int,Int>)
data class Metrics(
    val side: String, // "front" or "back"
    val centering: Centering,
    val flags: List<String> = emptyList()
)

object MetricExtractor {

    /**
     * Compute inner-frame centering:
     * we look for the strongest vertical edge within a 15% margin band from left/right,
     * and strongest horizontal edge within a 15% band from top/bottom.
     * Returns percentages like 58/42.
     */
    fun centeringMetrics(rectified: Bitmap, side: String): Metrics {
        val w = rectified.width
        val h = rectified.height
        val px = IntArray(w * h)
        rectified.getPixels(px, 0, w, 0, 0, w, h)

        fun colEdge(x: Int): Double {
            var s = 0.0; var c = 0
            for (y in 1 until h - 1 step 2) {
                val i = y * w + x
                val l = px[i - 1] and 0xFF
                val r = px[i + 1] and 0xFF
                s += kotlin.math.abs(r - l)
                c++
            }
            return if (c == 0) 0.0 else s / c
        }
        fun rowEdge(y: Int): Double {
            var s = 0.0; var c = 0
            val row = y * w
            for (x in 1 until w - 1 step 2) {
                val u = px[row + x - w] and 0xFF
                val d = px[row + x + w] and 0xFF
                s += kotlin.math.abs(d - u)
                c++
            }
            return if (c == 0) 0.0 else s / c
        }

        val bandX = (w * 0.15).toInt()
        val bandY = (h * 0.15).toInt()

        // Left inner frame
        var bestL = bandX; var bestLv = 0.0
        for (x in 6 until bandX) {
            val v = colEdge(x)
            if (v > bestLv) { bestLv = v; bestL = x }
        }
        // Right inner frame
        var bestR = w - bandX; var bestRv = 0.0
        for (x in w - bandX until w - 6) {
            val v = colEdge(x)
            if (v > bestRv) { bestRv = v; bestR = x }
        }
        // Top inner frame
        var bestT = bandY; var bestTv = 0.0
        for (y in 6 until bandY) {
            val v = rowEdge(y)
            if (v > bestTv) { bestTv = v; bestT = y }
        }
        // Bottom inner frame
        var bestB = h - bandY; var bestBv = 0.0
        for (y in h - bandY until h - 6) {
            val v = rowEdge(y)
            if (v > bestBv) { bestBv = v; bestB = y }
        }

        val left = bestL
        val right = w - bestR
        val top = bestT
        val bottom = h - bestB

        val lrPct = toPct(left, right)
        val tbPct = toPct(top, bottom)

        val flags = mutableListOf<String>()
        // if the inner frame wasn't clearly found, add a flag
        val weak = listOf(bestLv, bestRv, bestTv, bestBv).count { it < 10.0 }
        if (weak >= 2) flags += "centering_uncertain"

        return Metrics(side = side, centering = Centering(lrPct, tbPct), flags = flags)
    }

    private fun toPct(a: Int, b: Int): Pair<Int, Int> {
        val sum = (a + b).coerceAtLeast(1)
        val left = (a * 100.0 / sum).roundToInt()
        val right = 100 - left
        return left to right
    }
}
