package com.example.comiccam.gesture

import kotlin.math.hypot

class RevealGestureStateMachine {
    private var pinchFrames = 0
    private var pulse = 0L
    var uiState = RevealGestureUiState(); private set

    fun onFrame(frame: GestureFrame): RevealGestureUiState {
        if (isArmSpread(frame.pose)) return fullReveal()
        val pinching = frame.hands.size >= 2 && frame.hands.take(2).all(::isPinching)
        val progressFromHands = frame.pose?.let { pose ->
            if (pinching) mapPinchToTorso(frame.hands.take(2), pose) else null
        }
        uiState = when (uiState.state) {
            RevealState.IDLE, RevealState.RELEASED -> if (pinching && ++pinchFrames >= GestureConfig.PINCH_DEBOUNCE_FRAMES) uiState.copy(state = RevealState.GRABBED, statusText = "Reveal band grabbed") else uiState.copy(statusText = if (frame.hands.size < 2) "Show both hands" else "Pinch both hands")
            RevealState.GRABBED, RevealState.DRAGGING -> if (pinching && progressFromHands != null) uiState.copy(state = RevealState.DRAGGING, revealProgress = progressFromHands, statusText = "Dragging reveal") else { pinchFrames = 0; uiState.copy(state = RevealState.RELEASED, statusText = "Released — pinch again to continue") }
            RevealState.FULL_REVEAL -> uiState.copy(revealProgress = 1f, statusText = "Suit-up complete")
            RevealState.RESETTING -> uiState
        }
        if (!pinching) pinchFrames = 0
        return uiState
    }

    fun reset(): RevealGestureUiState { pinchFrames = 0; uiState = RevealGestureUiState(state = RevealState.RESETTING, statusText = "Resetting"); uiState = RevealGestureUiState(); return uiState }
    fun forceProgress(progress: Float) { uiState = uiState.copy(revealProgress = progress.coerceIn(0f, 1f)) }
    private fun fullReveal(): RevealGestureUiState { pulse++; pinchFrames = 0; uiState = RevealGestureUiState(RevealState.FULL_REVEAL, 1f, pulse, "Arms spread — full reveal"); return uiState }
    private fun isPinching(hand: HandLandmarks): Boolean = hypot(hand.thumbTip.x - hand.indexTip.x, hand.thumbTip.y - hand.indexTip.y) / hand.boundsDiagonal.coerceAtLeast(0.001f) < GestureConfig.PINCH_THRESHOLD_NORMALIZED
    private fun mapPinchToTorso(hands: List<HandLandmarks>, pose: PoseKeypoints): Float { val y = hands.map { (it.thumbTip.y + it.indexTip.y) * 0.5f }.average().toFloat(); val top = pose.torsoTopY; val bottom = pose.torsoBottomY.coerceAtLeast(top + GestureConfig.MIN_TORSO_HEIGHT); return ((y - top) / (bottom - top)).coerceIn(0f, 1f) }
    private fun isArmSpread(pose: PoseKeypoints?): Boolean { pose ?: return false; val lw = pose.leftWrist ?: return false; val rw = pose.rightWrist ?: return false; val spread = kotlin.math.abs(rw.x - lw.x); val highEnough = lw.y < pose.torsoTopY + GestureConfig.ARM_SPREAD_MAX_WRIST_Y_OVER_SHOULDERS && rw.y < pose.torsoTopY + GestureConfig.ARM_SPREAD_MAX_WRIST_Y_OVER_SHOULDERS; return spread > pose.shoulderWidth * GestureConfig.ARM_SPREAD_SHOULDER_MULTIPLIER && highEnough }
}
