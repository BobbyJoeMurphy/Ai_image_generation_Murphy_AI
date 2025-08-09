package com.example.ai_image_generation_murphy_ai.data.repository.psa

import com.example.ai_image_generation_murphy_ai.data.repository.cv.Metrics
import com.example.ai_image_generation_murphy_ai.data.repository.model.GradeResult
import java.util.UUID
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

data class Pct(val a:Int, val b:Int) { fun worse() = max(a,b) to min(a,b) }

object RulesGrader {

    // PSA front/back centering thresholds (v1)
    private data class Cut(val frontMax: Int, val backMax: Int, val grade: Double)
    // worse side expressed as "max(% difference)" from 50/50; e.g., 60/40 => 60
    private val cutsDesc = listOf(
        Cut(frontMax = 55, backMax = 75, grade = 10.0),
        Cut(frontMax = 60, backMax = 90, grade = 9.0),
        Cut(frontMax = 65, backMax = 90, grade = 8.0),
        Cut(frontMax = 70, backMax = 90, grade = 7.0),
        Cut(frontMax = 80, backMax = 90, grade = 6.0),
        Cut(frontMax = 85, backMax = 90, grade = 5.0),
        Cut(frontMax = 85, backMax = 90, grade = 4.0),
        Cut(frontMax = 90, backMax = 90, grade = 3.0),
        Cut(frontMax = 90, backMax = 90, grade = 2.0),
        Cut(frontMax = 90, backMax = 90, grade = 1.5),
        Cut(frontMax = 90, backMax = 90, grade = 1.0),
    )

    private fun pctToMajor(p: Pair<Int,Int>): Int = max(p.first, p.second)

    private fun baseFromCentering(front: Pair<Int,Int>, back: Pair<Int,Int>): Double {
        val f = pctToMajor(front)  // e.g., 58 for 58/42
        val b = pctToMajor(back)
        for (c in cutsDesc) {
            // small leeway (5%) on front for 7+
            val fMax = if (c.grade >= 7.0) c.frontMax + 5 else c.frontMax
            if (f <= fMax && b <= c.backMax) return c.grade
        }
        return 1.0
    }

    fun grade(front: Metrics, back: Metrics): GradeResult {
        val base = baseFromCentering(front.centering.lrPct, back.centering.lrPct)
        val subs = linkedMapOf(
            "centering" to base
            // corners/edges/surface will join in v2
        )

        var overall = base
        overall = (overall * 2).roundToInt() / 2.0

        // Qualifier if centering exceeds the ideal cut for that band
        val qualifiers = mutableListOf<String>()
        val fMajor = pctToMajor(front.centering.lrPct)
        val idealFront = cutsDesc.firstOrNull { it.grade == base }?.frontMax ?: 60
        if (fMajor > idealFront) qualifiers += "OC"

        // Confidence: drop near boundaries or when flags exist
        var conf = 0.92
        val fDist = abs(fMajor - idealFront).toDouble()
        if (fDist <= 2.0) conf -= 0.15
        val flags = (front.flags + back.flags).toMutableList()
        if (flags.contains("centering_uncertain")) conf -= 0.15
        conf = conf.coerceIn(0.1, 0.98)

        return GradeResult(
            cardId = UUID.randomUUID().toString(),
            grade = "PSA ${("%.1f".format(overall))}",
            subscores = subs,
            centering = mapOf(
                "front" to "${front.centering.lrPct.first}/${front.centering.lrPct.second}",
                "back"  to "${back.centering.lrPct.first}/${back.centering.lrPct.second}"
            ),
            qualifiers = qualifiers,
            confidence = conf,
            flags = flags,
            notes = listOf("Base grade from centering", "Front ${front.centering.lrPct.first}/${front.centering.lrPct.second}", "Back ${back.centering.lrPct.first}/${back.centering.lrPct.second}")
        )
    }
}
