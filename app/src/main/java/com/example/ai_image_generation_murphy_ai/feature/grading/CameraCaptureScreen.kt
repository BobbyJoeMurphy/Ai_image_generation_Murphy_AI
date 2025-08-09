package com.example.ai_image_generation_murphy_ai.feature.grading

import android.Manifest
import android.graphics.Bitmap
import android.os.Build
import android.util.Size
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
@Composable
fun CameraCaptureScreen(vm: GradingViewModel = viewModel()) {
    val ctx = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by vm.state.collectAsState()

    var cameraExecutor: ExecutorService? by remember { mutableStateOf(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    // Permissions (simple inline check; replace with your own permission flow)
    LaunchedEffect(Unit) {
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    DisposableEffect(Unit) {
        onDispose { cameraExecutor?.shutdown() }
    }

    Column(Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                val previewView = PreviewView(it).apply {
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
                val cameraProviderFuture = ProcessCameraProvider.getInstance(it)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder()
                        .setTargetResolution(Size(1280, 720))
                        .build().also { p -> p.setSurfaceProvider(previewView.surfaceProvider) }

                    val analyzer = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setTargetResolution(Size(1280, 720))
                        .build().also { ia ->
                            ia.setAnalyzer(cameraExecutor!!, { proxy ->
                                proxy.image?.let { _ ->
                                    val bmp = proxy.toBitmap()
                                    val blur = ImageQuality.laplacianVariance(bmp)
                                    val glare = ImageQuality.glareRatio(bmp)
                                    vm.updateQuality(blur, glare)
                                    bmp.recycle()
                                }
                                proxy.close()
                            })
                        }

                    imageCapture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analyzer, imageCapture
                        )
                    } catch (_: Exception) {}
                }, ContextCompat.getMainExecutor(it))
                previewView
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        // Controls
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${state.side} • ${state.quality.message}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    enabled = state.captureEnabled,
                    onClick = {
                        // Burst capture and keep sharpest
                        val ic = imageCapture ?: return@Button
                        captureBurst(vm, ic, cameraExecutor!!)
                    }
                ) { Text("Capture ${state.side}") }

                OutlinedButton(
                    enabled = (state.front != null),
                    onClick = { vm.switchSide() }
                ) { Text("Next Side") }
            }
        }
    }
}

private fun captureBurst(vm: GradingViewModel, ic: ImageCapture, exec: ExecutorService, shots: Int = 4) {
    vm.setProcessing(true)
    val results = mutableListOf<Bitmap>()
    repeat(shots) { i ->
        ic.takePicture(exec, object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val bmp = image.toBitmap()
                results += bmp
                image.close()
                if (results.size == shots) {
                    // pick sharpest
                    val best = vm.pickSharpest(results)
                    // TODO: rectification will go here; for now we store the best as-is
                    if (vm.state.value.side == Side.FRONT) vm.setCaptured(Side.FRONT, best)
                    else vm.setCaptured(Side.BACK, best)

                    // Recycle the losers
                    results.filter { it !== best }.forEach { it.recycle() }
                    vm.setProcessing(false)
                }
            }
            override fun onError(exception: ImageCaptureException) {
                vm.setProcessing(false)
            }
        })
        try { Thread.sleep(90) } catch (_: InterruptedException) {}
    }
}

/** Quick YUV -> ARGB bitmap conversion for ImageProxy */
private fun ImageProxy.toBitmap(): Bitmap {
    val yBuffer = planes[0].buffer
    val uBuffer = planes[1].buffer
    val vBuffer = planes[2].buffer

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = android.graphics.YuvImage(nv21, android.graphics.ImageFormat.NV21, width, height, null)
    val out = java.io.ByteArrayOutputStream()
    yuvImage.compressToJpeg(android.graphics.Rect(0, 0, width, height), 95, out)
    val imageBytes = out.toByteArray()
    return android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}
