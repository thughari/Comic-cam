package com.example.comiccam.ml

import android.content.Context
import androidx.camera.core.ImageProxy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SegmentationEngine(context: Context) {
    private val _maskReady = MutableStateFlow(false)
    val maskReady: StateFlow<Boolean> = _maskReady
    fun analyze(imageProxy: ImageProxy) { _maskReady.value = true; imageProxy.close() }
    fun close() {}
}
