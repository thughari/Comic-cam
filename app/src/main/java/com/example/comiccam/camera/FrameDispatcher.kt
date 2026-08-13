package com.example.comiccam.camera

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.comiccam.gesture.GestureConfig
import com.example.comiccam.ml.HandGestureTracker
import com.example.comiccam.ml.PoseTracker
import com.example.comiccam.ml.SegmentationEngine

class FrameDispatcher(private val hands: HandGestureTracker, private val pose: PoseTracker, private val segmentation: SegmentationEngine) : ImageAnalysis.Analyzer {
    private var lastMlMs = 0L
    override fun analyze(image: ImageProxy) { val now = System.currentTimeMillis(); if (now - lastMlMs < GestureConfig.ML_MIN_INTERVAL_MS) { image.close(); return }; lastMlMs = now; segmentation.analyze(image) }
}
