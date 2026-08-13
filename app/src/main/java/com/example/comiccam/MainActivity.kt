package com.example.comiccam

import android.Manifest
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import java.util.concurrent.Executors
import kotlin.math.*

enum class ComicShiftState { IDLE_REAL, PANEL_ENTERING, PANEL_ACTIVE, MULTI_PANEL, COMIC_EXPANDING, FULL_COMIC, RETURNING_TO_REAL }
enum class PanelContentType { REAL, COMIC_PERSON, COMIC_BACKGROUND, FULL_COMIC }

data class ComicPanel(
    val x: Float, val y: Float, val width: Float, val height: Float,
    val rotation: Float = -1.5f, val scale: Float = 1f, val opacity: Float = 1f,
    val borderWidth: Float = 10f, val borderColor: Color = Color.Cyan,
    val contentType: PanelContentType = PanelContentType.COMIC_PERSON,
    val animationProgress: Float = 0f,
)

data class GestureSample(
    val fingerX: Float = 0f, val fingerY: Float = 0f, val deltaX: Float = 0f, val deltaY: Float = 0f,
    val velocityX: Float = 0f, val velocityY: Float = 0f, val gestureDuration: Long = 0L,
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { ComicCamApp() } }
}

@Composable
fun ComicCamApp() {
    var granted by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted = it }
    LaunchedEffect(Unit) { launcher.launch(Manifest.permission.CAMERA) }
    if (granted) ComicShiftCamera() else Box(Modifier.fillMaxSize().background(Color.Black), Alignment.Center) { Text("Camera permission is required", color = Color.White) }
}

@Composable
fun ComicShiftCamera() {
    val context = LocalContext.current; val lifecycleOwner = LocalLifecycleOwner.current
    val progress = remember { Animatable(0f) }
    var state by remember { mutableStateOf(ComicShiftState.IDLE_REAL) }
    var gesture by remember { mutableStateOf(GestureSample()) }
    var lastMaskSeen by remember { mutableStateOf(false) }
    val executor = remember { Executors.newSingleThreadExecutor() }

    LaunchedEffect(Unit) {
        val points = listOf(0f, .28f, .52f, .78f, 1f, 1f, 0f)
        for (p in points) { progress.animateTo(p, spring(stiffness = Spring.StiffnessLow, dampingRatio = .7f)); kotlinx.coroutines.delay(650) }
    }

    Box(Modifier.fillMaxSize().background(Color.Black).pointerInput(Unit) {
        var startX = 0f; var startTime = 0L
        detectDragGestures(
            onDragStart = { startX = it.x; startTime = System.currentTimeMillis() },
            onDragEnd = {
                val target = when { progress.value > .88f -> 1f; progress.value < .12f -> 0f; else -> (round(progress.value * 4f) / 4f).coerceIn(0f, 1f) }
                state = if (target == 0f) ComicShiftState.RETURNING_TO_REAL else if (target == 1f) ComicShiftState.FULL_COMIC else ComicShiftState.PANEL_ACTIVE
            },
        ) { change, drag ->
            change.consume(); val duration = (System.currentTimeMillis() - startTime).coerceAtLeast(1)
            val vx = (change.position.x - startX) / duration * 1000f
            val next = (progress.value + drag.x / size.width).coerceIn(0f, 1f)
            gesture = GestureSample(change.position.x, change.position.y, drag.x, drag.y, vx, drag.y / duration * 1000f, duration)
            state = when { next <= .02f -> ComicShiftState.IDLE_REAL; next < .33f -> ComicShiftState.PANEL_ENTERING; next < .66f -> ComicShiftState.MULTI_PANEL; next < .98f -> ComicShiftState.COMIC_EXPANDING; else -> ComicShiftState.FULL_COMIC }
            launch { progress.snapTo(next) }
        }
    }) {
        AndroidView(factory = { ctx ->
            PreviewView(ctx).also { view ->
                val provider = ProcessCameraProvider.getInstance(ctx).get()
                val preview = Preview.Builder().build().also { it.setSurfaceProvider(view.surfaceProvider) }
                val segmenter = Segmentation.getClient(SelfieSegmenterOptions.Builder().setDetectorMode(SelfieSegmenterOptions.STREAM_MODE).enableRawSizeMask().build())
                val analysis = ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build().also {
                    it.setAnalyzer(executor) { proxy ->
                        val media = proxy.image
                        if (media != null && proxy.imageInfo.timestamp % 3L == 0L) {
                            segmenter.process(InputImage.fromMediaImage(media, proxy.imageInfo.rotationDegrees)).addOnSuccessListener { lastMaskSeen = true }.addOnCompleteListener { proxy.close() }
                        } else proxy.close()
                    }
                }
                provider.unbindAll(); provider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_FRONT_CAMERA, preview, analysis)
            }
        }, modifier = Modifier.fillMaxSize())
        ComicComposer(progress.value, state, gesture, lastMaskSeen, Modifier.fillMaxSize())
        CameraChrome(state, lastMaskSeen)
    }
}

@Composable
fun ComicComposer(progress: Float, state: ComicShiftState, gesture: GestureSample, maskSeen: Boolean, modifier: Modifier = Modifier) = Canvas(modifier) {
    val intensity = (abs(gesture.velocityX) / 2200f).coerceIn(0f, 1f); val rgb = 5f * intensity
    fun panel(i: Int, total: Int): ComicPanel { val h = size.height / total; val y = i * h + sin(progress * 8 + i) * 8f; val enter = ((progress * (total + 1)) - i).coerceIn(0f, 1f); return ComicPanel(size.width * (1f - enter) - 28f * sin(enter * PI).toFloat(), y, size.width + 56f, h * .96f, animationProgress = enter) }
    if (progress > .72f) drawComicBackground(progress, gesture.deltaX * .05f)
    val count = when { progress < .18f -> 0; progress < .45f -> 1; progress < .72f -> 2; else -> 3 }
    repeat(count) { idx -> drawPanel(panel(idx, count.coerceAtLeast(1)), progress, rgb, idx) }
    if (progress > .9f) drawRect(Color(0x99200635), blendMode = BlendMode.Screen)
    drawSpeedLines(gesture, intensity); drawFingerGlow(gesture, intensity)
}

fun androidx.compose.ui.graphics.drawscope.DrawScope.drawComicBackground(progress: Float, drift: Float) {
    drawRect(Brush.verticalGradient(listOf(Color(0xFF020611), Color(0xFF18022A))))
    repeat(9) { i -> val x = (i * size.width / 8f + drift * (i % 3 + 1)) % size.width; val h = size.height * (.25f + (i % 4) * .09f); drawRect(Color(0xFF071026), Offset(x - 45, size.height - h), androidx.compose.ui.geometry.Size(80f, h)); drawRect(if (i % 2 == 0) Color.Cyan else Color.Magenta, Offset(x - 20, size.height - h + 35), androidx.compose.ui.geometry.Size(38f, 6f), alpha = .7f * progress) }
}

fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPanel(p: ComicPanel, progress: Float, rgb: Float, idx: Int) {
    val rect = Rect(p.x, p.y, p.x + p.width, p.y + p.height)
    drawRect(Color(0xAA04101C), rect.topLeft, rect.size)
    drawRect(Brush.linearGradient(listOf(Color(0x884AFDFF), Color(0x88FF3DCE))), rect.topLeft, rect.size, alpha = .55f)
    repeat(120) { n -> val x = rect.left + (n * 47 % rect.width.toInt()); val y = rect.top + (n * 29 % rect.height.toInt()); val dot = ((x + y + progress * 120) % 31) / 31f; if (dot < .35f) drawCircle(Color.Black, 2.2f + dot * 3f, Offset(x, y), alpha = .28f) }
    drawRect(Color.Black, rect.topLeft, rect.size, style = Stroke(p.borderWidth)); drawRect(if (idx % 2 == 0) Color.Cyan else Color.Magenta, rect.topLeft, rect.size, style = Stroke(3f))
    drawLine(Color.White, Offset(rect.left + 20, rect.top + 22), Offset(rect.right - 20, rect.top + 22), strokeWidth = 3f, alpha = .45f)
    drawCircle(Color.Magenta, 55f, Offset(size.width / 2 + rgb, rect.center.y), alpha = .16f); drawCircle(Color.Cyan, 55f, Offset(size.width / 2 - rgb, rect.center.y), alpha = .16f)
}

fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSpeedLines(g: GestureSample, intensity: Float) { if (intensity < .12f) return; repeat(18) { i -> val y = (i * 73f + g.fingerY) % size.height; val dir = if (g.velocityX >= 0) -1 else 1; drawLine(listOf(Color.Cyan, Color.Magenta, Color.White)[i % 3], Offset(g.fingerX, y), Offset(g.fingerX + dir * (120 + i * 12), y + (i % 5 - 2) * 6f), strokeWidth = 2f + intensity * 4f, alpha = intensity * .7f) } }
fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFingerGlow(g: GestureSample, intensity: Float) { if (g.gestureDuration == 0L) return; drawCircle(Brush.radialGradient(listOf(Color(0x884AFDFF), Color.Transparent), Offset(g.fingerX, g.fingerY), 110f), 110f, Offset(g.fingerX, g.fingerY), alpha = .35f + intensity * .35f) }

@Composable
fun CameraChrome(state: ComicShiftState, maskSeen: Boolean) {
    Column(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.SpaceBetween) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Text("⚙", color = Color.White); Text("COMIC SHIFT • ${state.name}", color = Color.Cyan); Text(if (maskSeen) "MASK" else "FLASH", color = Color.White) }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) { Text("▣", color = Color.White); Button(onClick = {}) { Text("●") }; Text("⇄", color = Color.White); Text("PHOTO / VIDEO", color = Color.White) }
    }
}
