package com.example.comiccam.gesture

data class Point2(val x: Float, val y: Float)
data class HandLandmarks(val thumbTip: Point2, val indexTip: Point2, val wrist: Point2, val boundsDiagonal: Float)
data class PoseKeypoints(val leftShoulder: Point2, val rightShoulder: Point2, val leftHip: Point2, val rightHip: Point2, val leftWrist: Point2? = null, val rightWrist: Point2? = null) {
    val shoulderWidth: Float get() = kotlin.math.abs(rightShoulder.x - leftShoulder.x).coerceAtLeast(0.001f)
    val torsoTopY: Float get() = (leftShoulder.y + rightShoulder.y) * 0.5f
    val torsoBottomY: Float get() = (leftHip.y + rightHip.y) * 0.5f
}
data class GestureFrame(val hands: List<HandLandmarks> = emptyList(), val pose: PoseKeypoints? = null, val timestampMs: Long = System.currentTimeMillis())
enum class RevealState { IDLE, GRABBED, DRAGGING, RELEASED, FULL_REVEAL, RESETTING }
data class RevealGestureUiState(val state: RevealState = RevealState.IDLE, val revealProgress: Float = 0f, val fullRevealPulse: Long = 0L, val statusText: String = "Pinch with both hands to grab the reveal band")
