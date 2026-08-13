package com.example.comiccam.ml

import android.content.Context
import androidx.camera.core.ImageProxy
import com.example.comiccam.gesture.HandLandmarks
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HandGestureTracker(context: Context) {
    private val _hands = MutableStateFlow<List<HandLandmarks>>(emptyList())
    val hands: StateFlow<List<HandLandmarks>> = _hands
    fun analyze(imageProxy: ImageProxy) { imageProxy.close() }
    fun updateDebugHands(hands: List<HandLandmarks>) { _hands.value = hands }
    fun close() {}
}
