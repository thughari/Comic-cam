package com.example.comiccam.ml

import android.content.Context
import androidx.camera.core.ImageProxy
import com.example.comiccam.gesture.PoseKeypoints
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PoseTracker(context: Context) {
    private val _pose = MutableStateFlow<PoseKeypoints?>(null)
    val pose: StateFlow<PoseKeypoints?> = _pose
    fun analyze(imageProxy: ImageProxy) { imageProxy.close() }
    fun updateDebugPose(pose: PoseKeypoints?) { _pose.value = pose }
    fun close() {}
}
