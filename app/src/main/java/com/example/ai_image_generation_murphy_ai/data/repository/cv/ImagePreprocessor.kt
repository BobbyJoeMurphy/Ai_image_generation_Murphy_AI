package com.example.ai_image_generation_murphy_ai.data.repository.cv

import android.graphics.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class RectifyResult(
    val bitmap: Bitmap,
    val cropRect: Rect,
    val rotated: Boolean,
    val flags: MutableList<String> = mutableListOf()
)

object ImagePreprocessor {

    /**
     * 1) Ensure portrait orientation
     * 2) Find outer card bounds using gradient scan from all four sides
     * 3) Crop to bounds with small padding
     * 4) Scale to target height (1024–1424 px)
     */
    fun rectifyAndCrop(src: Bitmap, targetHeight: Int = 1280): RectifyResult {
        val portrait = if (src.width > src.height) rotate(src, 90f) else src
        val g = toGrayscale(portrait)

        val left = scanEdgeX(g, fromLeft = true)
        val right = g.width - scanEdgeX(g, fromLeft = false)
        val top = scanEdgeY(g, fromTop = true)
        val bottom = g.height - scanEdgeY(g, fromTop = false)

        // sanity: ensure in-bounds, and that we detected something reasonable
        val pad = (min(g.width, g.height) * 0.01).toInt()
        val l = (left - pad).coerceAtLeast(0)
        val r = (right + pad).coerceAtMost(g.width - 1)
        val t = (top - pad).coerceAtLeast(0)
        val b = (bottom + pad).coerceAtMost(g.height - 1)
        val w = max(1, r - l)
        val h = max(1, b - t)
        val rect = Rect(l, t, r, b)

        val cropped = Bitmap.createBitmap(portrait, rect.left, rect.top, w, h)

        // scale to standard portrait height for downstream metrics
        val scaled = if (cropped.height != targetHeight) {
            val scale = targetHeight.toFloat() / cropped.height
            val tw = (cropped.width * scale).toInt().coerceAtLeast(1)
            Bitmap.createScaledBitmap(cropped, tw, targetHeight, true)
        } else cropped

        if (cropped !== portrait) portrait.recycle()
        if (scaled !== cropped && !cropped.isRecycled) cropped.recycle()
        g.recycle()

        val flags = mutableListOf<String>()
        // If bounds are too close to image edges, flag partial_crop
        if (l < pad || t < pad || r > g.width - pad || b > g.height - pad) flags += "partial_crop"

        return RectifyResult(bitmap = scaled, cropRect = rect, rotated = src.width > src.height, flags = flags)
    }

    private fun rotate(bmp: Bitmap, deg: Float): Bitmap {
        val m = Matrix().apply { postRotate(deg) }
        return Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, m, true)
    }

    private fun toGrayscale(src: Bitmap): Bitmap {
        val out = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        val c = Canvas(out)
        val p = Paint()
        val cm = ColorMatrix()
        cm.setSaturation(0f)
        p.colorFilter = ColorMatrixColorFilter(cm)
        c.drawBitmap(src, 0f, 0f, p)
        return out
    }

    /** Scan average gradient column-wise from an edge and find the first strong edge. */
    private fun scanEdgeX(gray: Bitmap, fromLeft: Boolean): Int {
        val w = gray.width
        val h = gray.height
        val step = 2
        val threshold = 18.0 // tune
        val px = IntArray(w * h)
        gray.getPixels(px, 0, w, 0, 0, w, h)

        fun colGrad(x: Int): Double {
            var sum = 0.0
            var cnt = 0
            val base = x
            for (y in 1 until h - 1 step step) {
                val idx = y * w + base
                val c = px[idx]
                val l = px[idx - 1]
                val r = px[idx + 1]
                val gy = abs(((c and 0xFF) - (px[idx - w] and 0xFF)))
                val gx = abs(((r and 0xFF) - (l and 0xFF)))
                sum += gx + gy
                cnt++
            }
            return if (cnt == 0) 0.0 else sum / cnt
        }

        if (fromLeft) {
            for (x in 6 until w / 2) if (colGrad(x) > threshold) return x
            return (w * 0.08).toInt()
        } else {
            for (x in w - 6 downTo w / 2) if (colGrad(x) > threshold) return w - x
            return (w * 0.08).toInt()
        }
    }

    /** Scan average gradient row-wise from an edge and find the first strong edge. */
    private fun scanEdgeY(gray: Bitmap, fromTop: Boolean): Int {
        val w = gray.width
        val h = gray.height
        val step = 2
        val threshold = 18.0
        val px = IntArray(w * h)
        gray.getPixels(px, 0, w, 0, 0, w, h)

        fun rowGrad(y: Int): Double {
            var sum = 0.0
            var cnt = 0
            val row = y * w
            for (x in 1 until w - 1 step step) {
                val idx = row + x
                val c = px[idx]
                val u = px[idx - w]
                val d = px[idx + w]
                val l = px[idx - 1]
                val r = px[idx + 1]
                val gx = abs((r and 0xFF) - (l and 0xFF))
                val gy = abs((d and 0xFF) - (u and 0xFF))
                sum += gx + gy
                cnt++
            }
            return if (cnt == 0) 0.0 else sum / cnt
        }

        if (fromTop) {
            for (y in 6 until h / 2) if (rowGrad(y) > threshold) return y
            return (h * 0.08).toInt()
        } else {
            for (y in h - 6 downTo h / 2) if (rowGrad(y) > threshold) return h - y
            return (h * 0.08).toInt()
        }
    }
}
