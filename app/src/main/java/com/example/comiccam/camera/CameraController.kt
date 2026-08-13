package com.example.comiccam.camera

import android.content.Context
import android.view.Surface
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraController(private val context: Context, private val lifecycleOwner: LifecycleOwner) {
    private val analysisExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    fun bind(surface: Surface, analyzer: ImageAnalysis.Analyzer, onError: (String) -> Unit) {
        val providerFuture = ProcessCameraProvider.getInstance(context)
        providerFuture.addListener({
            runCatching {
                val provider = providerFuture.get()
                val preview = Preview.Builder().setTargetResolution(android.util.Size(1280, 720)).build().also { preview -> preview.setSurfaceProvider { request -> request.provideSurface(surface, ContextCompat.getMainExecutor(context)) {} } }
                val analysis = ImageAnalysis.Builder().setTargetResolution(android.util.Size(1280, 720)).setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build().also { it.setAnalyzer(analysisExecutor, analyzer) }
                provider.unbindAll(); provider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_FRONT_CAMERA, preview, analysis)
            }.onFailure { onError(it.message ?: "Unable to open front camera") }
        }, ContextCompat.getMainExecutor(context))
    }
    fun shutdown() { analysisExecutor.shutdown() }
}
