package com.example.ai_image_generation_murphy_ai.feature.grading

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

data class Quality(
    val ok: Boolean,
    val blurVar: Double,
    val glareRatio: Double,
    val message: String,
)

enum class Side { FRONT, BACK }

data class CaptureState(
    val front: Bitmap? = null,
    val back: Bitmap? = null,
    val side: Side = Side.FRONT,
    val quality: Quality = Quality(false, 0.0, 1.0, "Point camera at card"),
    val captureEnabled: Boolean = false,
    val isProcessing: Boolean = false
)

class GradingViewModel : ViewModel() {

    private val _state = MutableStateFlow(CaptureState())
    val state = _state.asStateFlow()

    fun updateQuality(blurVar: Double, glareRatio: Double) {
        // Thresholds to start with; we’ll tune later.
        val blurPass = blurVar > 120.0
        val glarePass = glareRatio < 0.04
        val ok = blurPass && glarePass

        val msg = when {
            !blurPass && !glarePass -> "Move closer & tilt to reduce glare"
            !blurPass -> "Hold steady or move closer (image is blurry)"
            !glarePass -> "Tilt phone 10–15° to kill glare"
            else -> "Ready—hold steady"
        }
        _state.value = _state.value.copy(
            quality = Quality(ok, blurVar, glareRatio, msg),
            captureEnabled = ok
        )
    }

    fun switchSide() {
        _state.value = _state.value.copy(side = if (_state.value.side == Side.FRONT) Side.BACK else Side.FRONT)
    }

    fun setCaptured(side: Side, bmp: Bitmap) {
        _state.value = if (side == Side.FRONT)
            _state.value.copy(front = bmp)
        else
            _state.value.copy(back = bmp)
    }

    fun canGrade(): Boolean = _state.value.front != null && _state.value.back != null

    fun setProcessing(v: Boolean) {
        _state.value = _state.value.copy(isProcessing = v)
    }

    /**
     * Keep sharpest out of N bitmaps using Laplacian variance.
     */
    fun pickSharpest(candidates: List<Bitmap>): Bitmap {
        var best: Bitmap = candidates.first()
        var bestScore = 0.0
        for (b in candidates) {
            val s = ImageQuality.laplacianVariance(b)
            if (s > bestScore) {
                best = b
                bestScore = s
            }
        }
        return best
    }
}

