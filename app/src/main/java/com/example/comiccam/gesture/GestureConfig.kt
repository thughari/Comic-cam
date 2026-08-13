package com.example.comiccam.gesture

object GestureConfig {
    const val PINCH_THRESHOLD_NORMALIZED = 0.05f
    const val PINCH_RELEASE_THRESHOLD_NORMALIZED = 0.075f
    const val PINCH_DEBOUNCE_FRAMES = 2
    const val ARM_SPREAD_SHOULDER_MULTIPLIER = 1.5f
    const val ARM_SPREAD_MAX_WRIST_Y_OVER_SHOULDERS = 0.12f
    const val RELEASE_SETTLE_MS = 200
    const val FULL_REVEAL_MS = 350
    const val RESET_MS = 400
    const val MIN_TORSO_HEIGHT = 0.08f
    const val BOUNDARY_FEATHER = 0.032f
    const val ML_TARGET_WIDTH = 480
    const val ML_TARGET_HEIGHT = 360
    const val ML_MIN_INTERVAL_MS = 66L
}
