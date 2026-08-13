# Suit-Up Theme Packs

Theme packs are data-only content placed under `app/src/main/assets/themes`, so designers can add looks without Kotlin changes.

## Manifest

Add `app/src/main/assets/themes/<theme_id>.json` with these fields:

- `id`: stable lowercase identifier.
- `displayName`: carousel label.
- `costumeTexture`: transparent PNG body suit, recommended 1024×2048.
- `maskTexture`: transparent PNG face mask, recommended 1024×1024.
- `normalMap`: optional PNG normal map matching the costume dimensions.
- `thumbnail`: optional 512×512 PNG for picker previews.
- `accentColorHex`, `glowColorHex`, `particleColorHex`: `#RRGGBB` colors used by UI, seam glow, and burst particles.
- `transformSoundFile`: optional short OGG, ideally under 750 ms.
- `idleAnimation`: named renderer preset such as `eye_glow_pulse`.
- `artStyle`: short descriptive tag.

## Asset requirements

Costume and mask images must include alpha. Keep transparent pixels outside the intended suit/mask shape; the runtime also multiplies these textures by the live selfie-segmentation mask. Use power-of-two dimensions when possible for faster uploads. Only the selected theme should be uploaded to GPU memory, so theme switching does not restart CameraX.

## Starter themes

This repository ships three starter manifests: Arachnid 2099, Nebula Guardian, and Ink-Wash Ronin. They prove the pipeline is theme-agnostic and avoid character-specific code branches.
