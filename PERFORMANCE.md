# Performance Notes

The implementation prioritizes a CameraX preview feeding an OpenGL ES `GL_TEXTURE_EXTERNAL_OES` surface, with ML analysis throttled independently through `FrameDispatcher`. The renderer never waits for inference; it holds the most recent gesture, pose, and mask state.

Current limitations: MediaPipe model assets and physical-device profiling are not included in this repository snapshot, so sustained FPS still needs validation on a mid-range Android device. Emulator camera testing is insufficient for acceptance because hand tracking, pose tracking, and GPU delegate behavior differ substantially from real hardware.
