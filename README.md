# Comic Cam

Native Android prototype for a single flagship **Comic Shift** camera effect. The app opens into a CameraX front-camera preview and layers an interactive cyberpunk comic transition over the live feed.

## MVP implemented

- CameraX preview with front camera binding.
- ML Kit selfie segmentation analyzer wired at preview time to produce a person-mask signal.
- Explicit `ComicShiftState` state machine and normalized `transitionProgress`.
- Reusable `ComicPanel` model with geometry, transform, border, content, opacity, and animation progress fields.
- Drag/swipe gesture tracking for finger position, deltas, velocity, and duration.
- One-to-three horizontal comic panels that progressively enter as the transition increases.
- Original procedural cyberpunk comic background with neon city silhouettes.
- Halftone dots, RGB split glow, speed lines, finger glow, and full-comic color wash.
- Modular shader assets for posterization, edge detection, halftone, RGB split, glow, motion smear, and color grading.

## Notes

The current renderer uses Compose Canvas as the visible MVP compositor while the shader assets and camera/segmentation pipeline are scaffolded for a future dedicated OpenGL/Surface renderer. Full video recording and physical-device validation are intentionally left for later phases.
