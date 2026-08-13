# Comic Cam Suit-Up

Native Kotlin/Compose Android prototype for a gesture-controlled AR suit-up camera. The app uses a CameraX front-camera stream rendered through an OpenGL ES surface and scaffolds the MediaPipe-driven hand, pose, and segmentation pipeline needed for a Spider-Verse-style reveal effect without hardcoding a single character.

## Implemented in this pass

- CameraX front-camera binding to a GL `SurfaceTexture` / external OES texture path.
- MVVM UI with Compose, `StateFlow`, and a permission gate.
- Reveal gesture state machine with tunable thresholds in `GestureConfig.kt`.
- ML wrapper classes for hand, pose, and segmentation streams plus throttled frame dispatch.
- Theme repository backed by `assets/themes/*.json` and DataStore selection persistence.
- Theme picker carousel with three starter theme manifests.
- GPU shader assets for camera passthrough, costume warp, silhouette masking, and boundary glow.

## Validation note

A physical Android device is required for final acceptance. Emulator camera input is not sufficient to validate real-time gesture latency, MediaPipe GPU delegate behavior, or sustained camera/render FPS.
